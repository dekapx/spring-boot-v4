package com.dekapx.java.service;

import com.dekapx.java.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static com.dekapx.java.model.Department.IT;
import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeServiceTest {
    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeeService = new EmployeeService();
    }

    @Test
    public void shouldReturnEmployeeForGivenId() {
        var employee = employeeService.findById(1L);
        assertThat(employee)
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getId()).isEqualTo(1L);
                    assertThat(e.getFirstName()).isEqualTo("John");
                    assertThat(e.getLastName()).isEqualTo("Doe");
                    assertThat(e.getEmail()).isEqualTo("John.Doe@hotmail.com");
                    assertThat(e.getDepartment()).isEqualTo(IT);
                });
    }

    @Test
    public void shouldReturnAllEmployees1() {
        var employees = employeeService.findAll();
        assertThat(employees)
                .isNotNull()
                .isNotEmpty()
                .hasSize(3)
                .anySatisfy(e -> {
                    assertThat(e.getFirstName()).isEqualTo("John");
                    assertThat(e.getLastName()).isEqualTo("Doe");
                    assertThat(e.getEmail()).isEqualTo("John.Doe@hotmail.com");
                    assertThat(e.getDepartment()).isEqualTo(IT);
                });
    }

    @Test
    public void shouldReturnAllEmployees2() {
        var employees = employeeService.findAll();
        assertThat(employees)
                .isNotNull()
                .isNotEmpty()
                .hasSize(3)
                .allSatisfy(e -> {
                    assertThat(e.getDepartment()).isEqualTo(IT);
                });
    }
}
