package com.dekapx.apps.aspect;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String errorMessage,
        String status,
        LocalDateTime timestamp
) {}
