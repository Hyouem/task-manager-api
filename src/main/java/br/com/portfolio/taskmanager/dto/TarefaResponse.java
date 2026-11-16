package br.com.portfolio.taskmanager.dto;

import br.com.portfolio.taskmanager.entity.StatusTarefa;
import br.com.portfolio.taskmanager.entity.Tarefa;

import java.time.OffsetDateTime;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        StatusTarefa status,
        OffsetDateTime criadaEm,
        OffsetDateTime atualizadaEm
) {
    public static TarefaResponse from(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getCriadaEm(),
                tarefa.getAtualizadaEm()
        );
    }
}