package app.service.mock;

import com.parcel.app.dto.req.CreateOrderReq;
import com.parcel.app.dto.resp.OrderResp;
import com.parcel.app.entity.OrderEntity;
import com.parcel.app.entity.UserEntity;
import com.parcel.app.enums.Status;
import com.parcel.app.security.UserDetailsImp;
import java.time.LocalDateTime;
import java.util.UUID;

public class MockData {

    public static LocalDateTime now = LocalDateTime.now();
    public static String orderId = UUID.randomUUID().toString();
    public static String userId = UUID.randomUUID().toString();
    public static String courierId = UUID.randomUUID().toString();

    public static UserDetailsImp userDetailsImp() {
        return new UserDetailsImp(UserEntity.builder().id(userId).role("USER").build());
    }

    public static CreateOrderReq createOrderReq() {
        return CreateOrderReq.builder().description("some order").destination("home").build();
    }

    public static OrderResp createOrderResp() {
        return OrderResp.builder().createdDate(now).id(orderId).description("some order").destination("home")
                .userId(userId).status(Status.NEW.name()).courierId(null).build();
    }

    public static OrderEntity orderEntity() {
        return OrderEntity.builder().id(orderId).createdDate(now).description("some order").destination("home")
                .status(Status.NEW.name()).userId(userId).build();
    }
}
