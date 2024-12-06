package com.dekapx.apps.controller;

import com.dekapx.apps.model.UserModel;
import com.dekapx.apps.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dekapx.apps.common.ResourceUrls.BASE_URL;
import static com.dekapx.apps.common.ResourceUrls.INFO_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(INFO_URL)
    public String getInfo() {
        log.info("Spring Boot GraphQL v1.0");
        return "Spring Boot GraphQL v1.0";
    }

    @QueryMapping
    public List<UserModel> findAll() {
        return this.userService.findAll();
    }

    @QueryMapping
    public UserModel findById(@Argument Long id) {
        return this.userService.findById(id);
    }
}
