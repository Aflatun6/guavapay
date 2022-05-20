package com.parcel.app.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "courier_id")
    private String courierId;
    private String description;
    private String destination;
    private String status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
