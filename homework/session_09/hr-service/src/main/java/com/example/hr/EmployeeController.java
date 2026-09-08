package com.example.hr;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeRepository repository;
    public EmployeeController(EmployeeRepository repository) { this.repository = repository; }
    public record EmployeeRequest(@NotBlank String fullName, @NotBlank @Email String email,
                                  @NotBlank String department) {}
    @GetMapping
    public List<Employee> list(@RequestParam(required = false) String department) {
        return department == null ? repository.findAll() : repository.findByDepartmentOrderByIdAsc(department);
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@Valid @RequestBody EmployeeRequest request) {
        try {
            return repository.saveAndFlush(new Employee(request.fullName(), request.email(), request.department()));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists", ex);
        }
    }
}
