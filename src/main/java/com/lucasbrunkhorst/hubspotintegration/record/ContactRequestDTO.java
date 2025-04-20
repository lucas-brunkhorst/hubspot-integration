package com.lucasbrunkhorst.hubspotintegration.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactRequestDTO(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,
        String firstname,
        String lastname,
        String phone,
        String company
) { }