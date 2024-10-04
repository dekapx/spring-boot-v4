package com.dekapx.apps.user.controller;

import com.dekapx.apps.user.model.UserOnboardingRequest;
import com.dekapx.apps.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dekapx.apps.core.common.HttpStatusCodes.UNAUTHORIZED;
import static com.dekapx.apps.core.common.HttpStatusCodes.UNAUTHORIZED_MSG;
import static com.dekapx.apps.user.common.ResourceUrls.BASE_URL;
import static com.dekapx.apps.user.common.ResourceUrls.INFO_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "User Pre-Post Processors API Info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = UNAUTHORIZED,
                    description = UNAUTHORIZED_MSG,
                    content = @Content)})
    @GetMapping(INFO_URL)
    public String getInfo() {
        log.info("User Pre-Post Processors API v1.0");
        userOnboarding();
        return "User Pre-Post Processors API v1.0";
    }

    private void userOnboarding() {
        this.userService.userOnboarding(
                UserOnboardingRequest.builder()
                        .firstName("Test")
                        .lastNameName("User")
                        .build());
    }
}
