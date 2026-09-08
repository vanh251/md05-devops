package com.example.payment;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    public record PaymentRequest(@NotBlank String orderId,
            @NotNull @DecimalMin("0.01") BigDecimal amount) {}
    public record PaymentResponse(String paymentId, String orderId,
            BigDecimal amount, String status) {}

    @GetMapping("/health")
    public Map<String, String> health() { return Map.of("status", "UP", "service", "payment-service"); }

    // Chỉ mô phỏng phản hồi; không thu tiền và không kết nối cổng thanh toán.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@Valid @RequestBody PaymentRequest request) {
        return new PaymentResponse(UUID.randomUUID().toString(), request.orderId(), request.amount(), "DEMO_CREATED");
    }
}
