package com.dekapx.apps.controller;

import com.dekapx.apps.model.OrderAgentRequest;
import com.dekapx.apps.model.OrderAgentResponse;
import com.dekapx.apps.service.OrderAgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class OrderAgentController {
    private final OrderAgentService orderAgentService;

    @PostMapping("/chat")
    public ResponseEntity<OrderAgentResponse> chat(@Valid @RequestBody OrderAgentRequest request) {
        String reply = this.orderAgentService.ask(request.message());
        return ResponseEntity.ok(new OrderAgentResponse(reply));
    }
}
