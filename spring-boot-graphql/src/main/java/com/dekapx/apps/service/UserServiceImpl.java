package com.dekapx.apps.service;

import com.dekapx.apps.entity.User;
import com.dekapx.apps.exception.ResourceNotFoundException;
import com.dekapx.apps.model.CreateUserRequest;
import com.dekapx.apps.model.UserModel;
import com.dekapx.apps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @SchemaMapping
    public List<UserModel> findAll() {
        return toUserModels(this.userRepository.findAll());
    }

    @Override
    public UserModel findById(Long Id) {
        return toUserModel(this.userRepository
                .findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id [" + Id + "] not found")));
    }

    @Override
    public UserModel create(CreateUserRequest request) {
        final User user = toUser(request);
        return toUserModel(this.userRepository.save(user));
    }

    private List<UserModel> toUserModels(List<User> users) {
        return users.stream().map(this::toUserModel).toList();
    }

    private UserModel toUserModel(User user) {
        return UserModel.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    private User toUser(CreateUserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();
    }
}
