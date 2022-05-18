package com.parcel.app.service;

import com.parcel.app.config.PrincipalConfig;
import com.parcel.app.dto.req.CreateOrderReq;
import com.parcel.app.dto.req.UpdateOrderReq;
import com.parcel.app.dto.resp.OrderResp;
import com.parcel.app.entity.OrderEntity;
import com.parcel.app.enums.Status;
import com.parcel.app.exception.RecordNotFoundException;
import com.parcel.app.repo.OrderRepository;
import com.parcel.app.util.MainLogger;
import com.parcel.app.util.mapper.OrderMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final MainLogger logger = MainLogger.getLogger(OrderService.class);

    public OrderResp createOrder(CreateOrderReq createOrderReq) {
        OrderEntity entity = orderMapper.reqToEntity(createOrderReq);
        entity.setId(UUID.randomUUID().toString());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setUserId(PrincipalConfig.getUserId());
        entity.setStatus(Status.NEW.name());
        OrderEntity saved = save(entity);
        return orderMapper.entityToResp(saved);
    }

    private OrderEntity save(OrderEntity entity) {
        OrderEntity saved = orderRepository.save(entity);
        logger.info("saved order {}", entity);
        return saved;
    }

    public void updateDestination(UpdateOrderReq updateOrderReq, String orderId) {
        OrderEntity entity = findById(orderId);
        if (!entity.getStatus().equals(Status.DELIVERED.name())) {
            orderMapper.updateEntity(updateOrderReq, entity);
            entity.setStatus(Status.CHANGED.name());
            save(entity);
        }
    }

    public void updateStatus(UpdateOrderReq updateOrderReq, String orderId) {
        OrderEntity entity = findById(orderId);
        if (!Status.DELIVERED.name().equals(entity.getStatus())) {
            entity.setStatus(Status.valueOf(updateOrderReq.getStatus().toUpperCase()).name());
            save(entity);
        }
    }

    public void cancelOrder(String orderId) {
        OrderEntity entity = findById(orderId);
        entity.setStatus(Status.CANCELLED.name());
        save(entity);
    }

    public OrderResp getOrder(String orderId) {
        return orderMapper.entityToResp(findById(orderId));
    }

    public List<OrderResp> getAllOrdersByUserId() {
        return orderRepository.getAllByUserId(PrincipalConfig.getUserId()).stream().map(orderMapper::entityToResp)
                .collect(Collectors.toList());
    }

    public List<OrderResp> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::entityToResp).collect(Collectors.toList());
    }

    public void assignOrderToCourier(String orderId, String courierId) {
        OrderEntity entity = findById(orderId);
        entity.setCourierId(courierId);
        entity.setStatus(Status.ASSIGNED.name());
        save(entity);
    }

    public List<OrderResp> getAllOrdersByCoordinates(String coordinate) {
        return orderRepository.getAllByDestination(coordinate).stream().map(orderMapper::entityToResp)
                .collect(Collectors.toList());
    }

    public List<OrderResp> getAllOrdersByCourier() {
        return getAllOrders().stream()
                .filter(o -> PrincipalConfig.getUserId().equals(o.getCourierId()))
                .collect(Collectors.toList());
    }

    public OrderResp getOrderByCourier(String orderId) {
        OrderResp order = getOrder(orderId);
        return order.getCourierId().equals(PrincipalConfig.getUserId()) ? order : null;
    }

    private OrderEntity findById(String orderId) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RecordNotFoundException("Order with id " + orderId + " wasn't found"));
        logger.info("fetched order {}", entity);
        return entity;
    }
}
