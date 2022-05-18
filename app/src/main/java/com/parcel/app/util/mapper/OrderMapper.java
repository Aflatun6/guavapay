package com.parcel.app.util.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.parcel.app.dto.req.CreateOrderReq;
import com.parcel.app.dto.req.UpdateOrderReq;
import com.parcel.app.dto.resp.OrderResp;
import com.parcel.app.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        injectionStrategy = CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = IGNORE)
public interface OrderMapper {

    OrderResp entityToResp(OrderEntity orderEntity);

    OrderEntity reqToEntity(CreateOrderReq createOrderReq);

    void updateEntity(UpdateOrderReq updateOrderReq, @MappingTarget OrderEntity orderEntity);
}
