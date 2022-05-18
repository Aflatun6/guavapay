package com.parcel.app.controller;

import com.parcel.app.dto.req.CreateOrderReq;
import com.parcel.app.dto.req.UpdateOrderReq;
import com.parcel.app.dto.req.UserReq;
import com.parcel.app.dto.resp.OrderResp;
import com.parcel.app.dto.resp.UserResp;
import com.parcel.app.service.OrderService;
import com.parcel.app.service.UserService;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = "User API")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<UserResp> createUser(@RequestBody UserReq userReq) {
        return new ResponseEntity<>(userService.createUser(userReq), HttpStatus.CREATED);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResp> createOrder(@RequestBody CreateOrderReq createOrderReq) {
        return new ResponseEntity<>(orderService.createOrder(createOrderReq), HttpStatus.CREATED);
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<Object> updateOrder(@RequestBody UpdateOrderReq updateOrderReq, @PathVariable String orderId) {
        orderService.updateDestination(updateOrderReq, orderId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Object> cancelOrder(@PathVariable String orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResp> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResp>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersByUserId());
    }
}
