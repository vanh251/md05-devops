package com.example.user;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    public record User(Long id, String fullName, String email) {}
    @GetMapping("/api/users")
    public List<User> users() {
        return List.of(new User(1L, "Nguyen Van An", "an@example.com"),
                       new User(2L, "Tran Thi Binh", "binh@example.com"));
    }
}
