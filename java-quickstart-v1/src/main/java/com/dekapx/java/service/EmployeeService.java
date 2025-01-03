package com.dekapx.java.service;

import com.dekapx.java.model.Employee;

import java.util.List;

import static com.dekapx.java.model.Department.IT;

public class EmployeeService {
    public Employee findById(final Long id) {
        return createEmployee(id);
    }

    public List<Employee> findAll() {
        return List.of(
                createEmployee(1L),
                createEmployee(2L),
                createEmployee(3L));
    }

    private static Employee createEmployee(Long id) {
        return Employee.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email("")
                .department(IT)
                .build();
    }


}
