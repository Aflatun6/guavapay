package com.parcel.app.repo;

import com.parcel.app.entity.OrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    List<OrderEntity> getAllByUserId(String userId);
    List<OrderEntity> getAllByDestination(String coordinate);
}
