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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Api(tags = "Admin API")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<Object> updateOrderStatus(@RequestBody UpdateOrderReq updateOrderReq, @PathVariable String orderId) {
        orderService.updateStatus(updateOrderReq, orderId);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResp>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{orderId}/couriers/{courierId}")
    public ResponseEntity<Object> assignOrderToCourier(@PathVariable String orderId, @PathVariable String courierId) {
        orderService.assignOrderToCourier(orderId, courierId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/couriers")
    public ResponseEntity<UserResp> createCourier(@RequestBody UserReq userReq) {
        return new ResponseEntity<>(userService.createCourier(userReq), HttpStatus.CREATED);
    }

    @GetMapping("/orders/coordinates")
    public ResponseEntity<List<OrderResp>> getOrdersByCoordinates(@RequestParam String coordinate) {
        return ResponseEntity.ok(orderService.getAllOrdersByCoordinates(coordinate));
    }

    @GetMapping("/couriers")
    public ResponseEntity<List<UserResp>> getAllCouriers() {
        return ResponseEntity.ok(userService.getAllCouriers());
    }
}
