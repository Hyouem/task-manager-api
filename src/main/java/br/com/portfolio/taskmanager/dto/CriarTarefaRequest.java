package br.com.portfolio.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarTarefaRequest(
        @NotBlank
        @Size(max = 150)
        String titulo,

        @Size(max = 1000)
        String descricao
) {
}