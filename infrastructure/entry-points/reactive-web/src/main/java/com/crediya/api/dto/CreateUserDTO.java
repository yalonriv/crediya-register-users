package com.crediya.api.dto;

import java.time.LocalDate;
import java.util.Date;

public record CreateUserDTO(String firstName,
         String secondName,
         String surName,
         String secondSurName,
         LocalDate birthDate,
         String address,
         String phoneNumber,
         String email,
         Double baseSalary, Long dniNumber) {
}
