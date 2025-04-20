package com.example.myorder.controller;

import com.example.myorder.dto.OrderVO;
import com.example.myorder.model.Order;
import com.example.myorder.service.OrderService;
import com.example.myorder.service.WxPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.example.myorder.dto.OrderDTO;
import org.springframework.data.domain.Page;

@Tag(name = "订单管理", description = "订单相关的所有接口")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final WxPaymentService wxPaymentService;

    @Operation(
        summary = "创建订单",
        description = "从用户购物车创建新订单"
    )
    @ApiResponse(
        responseCode = "200",
        description = "订单创建成功"
    )
    @PostMapping("/create/{userId}")
    public Order createOrder(
        @Parameter(description = "用户ID") 
        @PathVariable Long userId
    ) {
        return orderService.createOrderFromCart(userId);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public OrderDTO getOrderDetails(
        @Parameter(description = "订单ID") 
        @PathVariable Long orderId
    ) {
        return orderService.getOrderDetailsWithItems(orderId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderVO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    @Operation(
        summary = "支付订单",
        description = "支付指定订单"
    )
    @PostMapping("/{orderId}/pay")
    public Order payOrder(
        @Parameter(description = "订单ID") 
        @PathVariable Long orderId
    ) {
        return orderService.payOrder(orderId);
    }
    
    @Operation(
        summary = "获取用户订单",
        description = "获取指定用户的所有订单"
    )
    @GetMapping("/user/{userId}")
    public Page<OrderDTO> getUserOrders(
        @Parameter(description = "用户ID") 
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status
    ) {
        return orderService.getUserOrders(userId, page, size, status);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId){
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("删除成功");
        } catch (Exception e) {
            log.error("删除订单失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("删除订单失败: " + e.getMessage());
        }
    }

    // 获取微信支付参数
    @GetMapping("/{orderId}/wx-pay-params")
    public ResponseEntity<?> getWxPayParams(@PathVariable Long orderId) {
        try {
            Map<String, String> payParams = orderService.createWxPayParams(orderId);
            return ResponseEntity.ok(payParams);
        } catch (Exception e) {
            log.error("获取支付参数失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取支付参数失败: " + e.getMessage());
        }
    }

    @PostMapping("/wx-pay-notify")
    public String handleWxPayNotify(@RequestBody String xmlData) {
        log.info("接收到微信支付回调");
        try {
            return wxPaymentService.handlePayNotify(xmlData);
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            return WxPayNotifyResponse.fail("处理失败");
        }
    }

    @PostMapping("/{orderId}/handle")
    public ResponseEntity<String> handleOrder(
        @Parameter(description = "订单ID")
        @PathVariable Long orderId
    ) {
        try {
            orderService.handleOrder(orderId);
            return ResponseEntity.ok("订单完成");
        } catch (Exception e) {
            log.error("订单完成失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("订单完成失败: " + e.getMessage());
        }
    }
} 