package com.crediya.api.dto;

import java.time.LocalDate;

public record EditUserDTO(
                          Long id,
                          String firstName,
                          String secondName,
                          String surName,
                          String secondSurName,
                          LocalDate birthDate,
                          String address,
                          String phoneNumber,
                          String email,
                          Double baseSalary, Long dniNumber) {
}
