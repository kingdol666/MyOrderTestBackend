package com.example.myorder.service;

import com.example.myorder.model.Order;
import com.example.myorder.repository.OrderRepository;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WxPaymentService {
    private final OrderRepository orderRepository;
    private final WxPayService wxPayService;
    private final MenuItemService menuItemService;

    // 创建统一下单
    public WxPayUnifiedOrderResult createUnifiedOrder(Order order) throws WxPayException {
        WxPayUnifiedOrderRequest request = WxPayUnifiedOrderRequest.newBuilder()
            .body("智慧食堂-订单支付")
            .outTradeNo(order.getOrderNumber())
            .totalFee(order.getTotalAmount().multiply(new BigDecimal("100")).intValue())
            .spbillCreateIp("127.0.0.1")
            .notifyUrl(wxPayService.getConfig().getNotifyUrl())
            .tradeType(WxPayConstants.TradeType.JSAPI)
            .openid(order.getUser().getOpenId())
            .build();
            
        return wxPayService.unifiedOrder(request);
    }

    // 生成支付签名
    public String createPaySign(Map<String, String> payParams) throws WxPayException {
        // 移除 appId，因为它已经包含在配置中
        String appId = payParams.remove("appId");
        
        // 构建签名参数
        Map<String, String> signParams = new HashMap<>(payParams);
        signParams.put("appId", wxPayService.getConfig().getAppId());
        
        // 按字典序排序并拼接参数
        String signContent = signParams.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));
        
        // 拼接商户密钥
        signContent += "&key=" + wxPayService.getConfig().getMchKey();
        
        // MD5加密并转大写
        String sign = DigestUtils.md5DigestAsHex(signContent.getBytes()).toUpperCase();
        
        // 恢复 appId
        payParams.put("appId", appId);
        
        return sign;
    }

    // 处理支付回调
    @Transactional
    public String handlePayNotify(String xmlData) {
        try {
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
            
            // 查找订单
            Order order = orderRepository.findByOrderNumber(notifyResult.getOutTradeNo())
                .orElseThrow(() -> new RuntimeException("订单不存在"));
            
            // 验证支付金额
            if (notifyResult.getTotalFee() != order.getTotalAmount()
                    .multiply(new BigDecimal("100")).intValue()) {
                return WxPayNotifyResponse.fail("支付金额不匹配");
            }
            
            // 检查订单状态
            if (!Order.STATUS_PENDING.equals(order.getStatus())) {
                return WxPayNotifyResponse.success("订单已处理");
            }
            
            // 更新订单状态
            if (WxPayConstants.ResultCode.SUCCESS.equals(notifyResult.getResultCode())) {
                order.setStatus(Order.STATUS_PAID);
                orderRepository.save(order);
                
                // 更新菜品销量
                order.getOrderItems().forEach(item -> 
                    menuItemService.updateSalesCount(item.getMenuItem().getId(), item.getQuantity())
                );
                
                return WxPayNotifyResponse.success("处理成功");
            } else {
                log.error("支付失败：" + notifyResult.getErrCodeDes());
                return WxPayNotifyResponse.fail(notifyResult.getErrCodeDes());
            }
        } catch (WxPayException e) {
            log.error("微信支付回调处理异常", e);
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }
} 