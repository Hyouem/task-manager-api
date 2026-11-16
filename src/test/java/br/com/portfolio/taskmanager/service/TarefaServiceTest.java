package br.com.portfolio.taskmanager.service;

import br.com.portfolio.taskmanager.dto.AtualizarTarefaRequest;
import br.com.portfolio.taskmanager.dto.CriarTarefaRequest;
import br.com.portfolio.taskmanager.entity.StatusTarefa;
import br.com.portfolio.taskmanager.entity.Tarefa;
import br.com.portfolio.taskmanager.exception.TarefaNaoEncontradaException;
import br.com.portfolio.taskmanager.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository repository;

    @InjectMocks
    private TarefaService service;

    private Tarefa tarefa;

    @BeforeEach
    void preparar() {
        tarefa = new Tarefa("Tarefa original", "Descricao original");
    }

    @Test
    void deveCriarTarefaComTituloNormalizado() {
        when(repository.save(any(Tarefa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var resposta = service.criar(
                new CriarTarefaRequest("  Nova tarefa  ", "Descricao")
        );

        assertEquals("Nova tarefa", resposta.titulo());
        assertEquals("Descricao", resposta.descricao());
        assertEquals(StatusTarefa.PENDENTE, resposta.status());

        verify(repository).save(any(Tarefa.class));
    }

    @Test
    void deveListarTarefas() {
        when(repository.findAll()).thenReturn(List.of(tarefa));

        var resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("Tarefa original", resultado.getFirst().titulo());
    }

    @Test
    void deveRetornarListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.listar().isEmpty());
    }

    @Test
    void deveBuscarTarefaExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(tarefa));

        var resposta = service.buscar(1L);

        assertEquals("Tarefa original", resposta.titulo());
    }

    @Test
    void deveLancarExcecaoAoBuscarTarefaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                TarefaNaoEncontradaException.class,
                () -> service.buscar(99L)
        );
    }

    @Test
    void deveAtualizarTarefa() {
        when(repository.findById(1L)).thenReturn(Optional.of(tarefa));

        var request = new AtualizarTarefaRequest(
                "  Tarefa atualizada  ",
                "Nova descricao",
                StatusTarefa.EM_ANDAMENTO
        );

        var resposta = service.atualizar(1L, request);

        assertEquals("Tarefa atualizada", resposta.titulo());
        assertEquals("Nova descricao", resposta.descricao());
        assertEquals(StatusTarefa.EM_ANDAMENTO, resposta.status());

        verify(repository).flush();
    }

    @Test
    void deveConcluirTarefaPendente() {
        when(repository.findById(1L)).thenReturn(Optional.of(tarefa));

        var resposta = service.concluir(1L);

        assertEquals(StatusTarefa.CONCLUIDA, resposta.status());
        verify(repository).flush();
    }

    @Test
    void naoDeveAtualizarTarefaJaConcluida() {
        tarefa.setStatus(StatusTarefa.CONCLUIDA);

        when(repository.findById(1L)).thenReturn(Optional.of(tarefa));

        var resposta = service.concluir(1L);

        assertEquals(StatusTarefa.CONCLUIDA, resposta.status());
        verify(repository, never()).flush();
    }

    @Test
    void deveExcluirTarefaExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(tarefa));

        service.excluir(1L);

        verify(repository).delete(tarefa);
    }

    @Test
    void naoDeveExcluirTarefaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                TarefaNaoEncontradaException.class,
                () -> service.excluir(99L)
        );

        verify(repository, never()).delete(any(Tarefa.class));
    }
}