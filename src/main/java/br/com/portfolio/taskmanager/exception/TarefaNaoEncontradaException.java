package br.com.portfolio.taskmanager.exception;

public class TarefaNaoEncontradaException extends RuntimeException {

    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa nao encontrada: " + id);
    }
}