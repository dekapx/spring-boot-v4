package com.dekapx.java.service;

import com.dekapx.java.model.Department;
import com.dekapx.java.model.Employee;

import java.util.List;

import static com.dekapx.java.model.Department.*;

public class EmployeeService {
    public Employee findById(final Long id) {
        return createEmployee(id, "John", "Doe", "John.Doe@hotmail.com", IT);
    }

    public List<Employee> findAll() {
        return List.of(
                createEmployee(1L, "John", "Doe", "John.Doe@hotmail.com", IT),
                createEmployee(2L, "Jane", "Doe", "Jane.Doe@gmail.com", IT),
                createEmployee(3L, "Tom", "Smith", "Tom.Smith@live.com", IT));
    }

    private static Employee createEmployee(Long id,
                                           String firstName,
                                           String lastName,
                                           String email,
                                           Department department) {
        return Employee.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .department(IT)
                .build();
    }


}
