package com.dekapx.apps.dto;

import com.dekapx.apps.model.Order;

public record ChatResponse(
        String reply,
        String intent,
        Order order
) {
}
