package com.parcel.app.controller;

import com.parcel.app.dto.req.CreateOrderReq;
import com.parcel.app.dto.req.UpdateOrderReq;
import com.parcel.app.dto.resp.OrderResp;
import com.parcel.app.service.OrderService;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courier")
@Api(tags = "Courier API")
public class CourierController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResp>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersByCourier());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResp> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderByCourier(orderId));
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<Object> updateOrder(@RequestBody UpdateOrderReq updateOrderReq, @PathVariable String orderId) {
        orderService.updateStatus(updateOrderReq, orderId);
        return ResponseEntity.ok(null);
    }
}
