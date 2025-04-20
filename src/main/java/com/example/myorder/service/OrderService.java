package com.example.myorder.service;

import com.example.myorder.dto.OrderDTO;
import com.example.myorder.dto.OrderItemDTO;
import com.example.myorder.dto.OrderVO;
import com.example.myorder.dto.UserDTO;
import com.example.myorder.model.*;
import com.example.myorder.repository.*;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final WxPaymentService wxPaymentService;
    private final MenuItemRepository menuItemRepository;

    @Transactional
    public Order createOrderFromCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("num:"+System.currentTimeMillis());
        order.setStatus(Order.STATUS_PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getMenuItem().getPrice());
            orderItem.setItemName(cartItem.getMenuItem().getName());
            
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(cartItem.getMenuItem().getPrice()
                .multiply(new BigDecimal(cartItem.getQuantity())));
        }
        
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }
    
    @Transactional
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
            
        if (!Order.STATUS_PENDING.equals(order.getStatus())) {
            throw new RuntimeException("Order cannot be paid");
        }
        
        order.setStatus(Order.STATUS_PAID);

        // 更新销量
        order.getOrderItems().forEach(orderItem -> {
            MenuItem menuItem = orderItem.getMenuItem();
            menuItem.setSalesCount(menuItem.getSalesCount() + orderItem.getQuantity());
            menuItemRepository.save(menuItem);
        });

        return orderRepository.save(order);
    }
    
    public Page<OrderDTO> getUserOrders(Long userId, int page, int size, String status) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createTime").descending());
        
        // 获取分页数据
        Page<Order> orders;
        if (status != null && !status.equals("all")) {
            orders = orderRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, status, pageRequest);
        } else {
            orders = orderRepository.findByUserIdOrderByCreateTimeDesc(userId, pageRequest);
        }
        
        // 转换为DTO
        return orders.map(order -> {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setOrderNumber(order.getOrderNumber());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setTotalAmount(order.getTotalAmount());
            orderDTO.setCreateTime(order.getCreateTime());
            orderDTO.setUpdateTime(order.getUpdateTime());
            orderDTO.setRemark(order.getRemark());
            
            // 设置用户信息
            UserDTO userDTO = new UserDTO();
            userDTO.setId(order.getUser().getId());
            userDTO.setNickname(order.getUser().getNickName());
            orderDTO.setUser(userDTO);
            
            // 设置订单项信息
            List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setItemName(item.getItemName());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setQuantity(item.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toList());
            orderDTO.setOrderItems(orderItemDTOs);
            
            return orderDTO;
        });
    }
    
    // 创建微信支付参数
    public Map<String, String> createWxPayParams(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
                
            // 检查订单状态
            if (!Order.STATUS_PENDING.equals(order.getStatus())) {
                throw new RuntimeException("订单状态不正确");
            }
            
            // 创建统一下单
            WxPayUnifiedOrderResult result = wxPaymentService.createUnifiedOrder(order);
            
            // 生成支付参数
            Map<String, String> payParams = new HashMap<>();
            payParams.put("appId", result.getAppid());
            payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            payParams.put("nonceStr", result.getNonceStr());
            payParams.put("package", "prepay_id=" + result.getPrepayId());
            payParams.put("signType", WxPayConstants.SignType.MD5);
            String paySign = wxPaymentService.createPaySign(payParams);
            payParams.put("paySign", paySign);
            
            return payParams;
        } catch (Exception e) {
            log.error("创建支付参数失败", e);
            throw new RuntimeException("创建支付参数失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    /**
     * 获取订单详情（包含订单项和用户信息）
     * @param orderId 订单ID
     * @return 订单详情，包含订单项和用户信息
     */
    public OrderDTO getOrderDetailsWithItems(Long orderId) {
        Order order = getOrderDetails(orderId);
        
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderNumber(order.getOrderNumber());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setCreateTime(order.getCreateTime());
        orderDTO.setUpdateTime(order.getUpdateTime());
        orderDTO.setRemark(order.getRemark());
        
        // 设置用户信息
        User user = order.getUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNickname(user.getNickName());
        orderDTO.setUser(userDTO);
        
        // 设置订单项信息
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
            .map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setId(item.getId());
                itemDTO.setItemName(item.getItemName());
                itemDTO.setPrice(item.getPrice());
                itemDTO.setQuantity(item.getQuantity());
                return itemDTO;
            })
            .collect(Collectors.toList());
        orderDTO.setOrderItems(orderItemDTOs);
        
        return orderDTO;
    }

    public Boolean deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 检查订单状态
        if (!Order.STATUS_PENDING.equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        // 删除订单
        orderRepository.delete(order);
        return true;
    }

    public List<OrderVO> getAllOrders() {
        return orderRepository.findAll().stream().map(
                order -> {
                    OrderVO orderVo = new OrderVO();
                    orderVo.setId(order.getId());
                    orderVo.setOrderNumber(order.getOrderNumber());
                    orderVo.setStatus(order.getStatus());
                    orderVo.setTotalAmount(order.getTotalAmount());
                    orderVo.setCreateTime(order.getCreateTime());
                    orderVo.setUpdateTime(order.getUpdateTime());
                    orderVo.setRemark(order.getRemark());
                    orderVo.setUserOpenId(order.getUser().getOpenId());
                    orderVo.setUserName(order.getUser().getNickName());
                    orderVo.setOrderItems(order.getOrderItems());
                    return orderVo;
                }
        ).toList();
    }

    public void handleOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(Order.STATUS_PREPARING);
        orderRepository.save(order);
    }
}