package com.example.bankingservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository repository;

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return repository.save(account);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }
}
