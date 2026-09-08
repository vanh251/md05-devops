package com.example.user;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired MockMvc mvc;
    @Test void shouldReturnUsers() throws Exception {
        mvc.perform(get("/api/users")).andExpect(status().isOk())
           .andExpect(jsonPath("$[0].email").value("an@example.com"));
    }
}
