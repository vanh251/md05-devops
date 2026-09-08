package com.example.hr;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {
    @Autowired EmployeeRepository repository;
    @Autowired EntityManager entityManager;
    @Autowired JdbcTemplate jdbc;

    @Test void shouldUsePostgres() {
        assertThat(jdbc.queryForObject("select version()", String.class)).contains("PostgreSQL");
    }
    @Test void shouldPersistAndReloadEmployee() {
        Employee saved = repository.saveAndFlush(new Employee("Nguyen Van An", "an@example.com", "IT"));
        Long id = saved.getId();
        entityManager.clear(); // Buộc lần đọc tiếp theo truy vấn PostgreSQL.
        Employee loaded = repository.findById(id).orElseThrow();
        assertThat(loaded.getFullName()).isEqualTo("Nguyen Van An");
        assertThat(loaded.getEmail()).isEqualTo("an@example.com");
    }
    @Test void shouldFilterByDepartment() {
        repository.saveAndFlush(new Employee("An", "it@example.com", "IT"));
        repository.saveAndFlush(new Employee("Binh", "hr@example.com", "HR"));
        entityManager.clear();
        assertThat(repository.findByDepartmentOrderByIdAsc("IT"))
                .extracting(Employee::getEmail).containsExactly("it@example.com");
    }
    @Test void shouldRejectDuplicateEmail() {
        repository.saveAndFlush(new Employee("An", "same@example.com", "IT"));
        assertThatThrownBy(() -> repository.saveAndFlush(new Employee("Binh", "same@example.com", "HR")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
