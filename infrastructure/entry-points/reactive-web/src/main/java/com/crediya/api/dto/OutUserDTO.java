package com.crediya.api.dto;

import java.time.LocalDate;

public record OutUserDTO(String firstName,
                         String secondName,
                         String surName,
                         String secondSurName,
                         LocalDate birthDate,
                         String address,
                         String phoneNumber,
                         String email,
                         Double baseSalary) {
}
