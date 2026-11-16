package br.com.portfolio.taskmanager.dto;

import br.com.portfolio.taskmanager.entity.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarTarefaRequest(
        @NotBlank
        @Size(max = 150)
        String titulo,

        @Size(max = 1000)
        String descricao,

        @NotNull
        StatusTarefa status
) {
}