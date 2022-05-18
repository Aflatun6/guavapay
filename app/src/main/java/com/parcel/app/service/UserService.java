package com.parcel.app.service;

import com.parcel.app.dto.req.UserReq;
import com.parcel.app.dto.resp.UserResp;
import com.parcel.app.entity.UserEntity;
import com.parcel.app.enums.Role;
import com.parcel.app.repo.UserRepository;
import com.parcel.app.util.MainLogger;
import com.parcel.app.util.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MainLogger logger = MainLogger.getLogger(UserService.class);

    public UserResp createUser(UserReq userReq) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setUsername(userReq.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userReq.getPassword()));
        userEntity.setRole(Role.USER.name());
        userEntity.setCreatedDate(LocalDateTime.now());
        UserEntity saved = save(userEntity);
        return userMapper.entityToResp(saved);
    }

    public UserResp createCourier(UserReq userReq) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setUsername(userReq.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userReq.getPassword()));
        userEntity.setRole(Role.COURIER.name());
        userEntity.setCreatedDate(LocalDateTime.now());
        UserEntity saved = save(userEntity);
        return userMapper.entityToResp(saved);
    }

    private UserEntity save(UserEntity userEntity) {
        UserEntity saved = userRepository.save(userEntity);
        logger.info("saved user {}", saved);
        return saved;
    }

    public List<UserResp> getAllCouriers() {
        return userRepository.findAllByRole(Role.COURIER.name())
                .stream().map(userMapper::entityToResp).collect(Collectors.toList());
    }
}
