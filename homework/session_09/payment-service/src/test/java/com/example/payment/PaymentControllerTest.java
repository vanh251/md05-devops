package com.example.payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {
    @Autowired MockMvc mvc;
    @Test void shouldCreateDemoPayment() throws Exception {
        mvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderId\":\"ORDER-1\",\"amount\":100000}"))
           .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("DEMO_CREATED"));
    }
    @Test void shouldRejectNegativeAmount() throws Exception {
        mvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderId\":\"ORDER-1\",\"amount\":-1}"))
           .andExpect(status().isBadRequest());
    }
}
