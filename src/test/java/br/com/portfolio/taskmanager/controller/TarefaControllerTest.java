package br.com.portfolio.taskmanager.controller;

import br.com.portfolio.taskmanager.dto.TarefaResponse;
import br.com.portfolio.taskmanager.entity.StatusTarefa;
import br.com.portfolio.taskmanager.exception.GlobalExceptionHandler;
import br.com.portfolio.taskmanager.exception.TarefaNaoEncontradaException;
import br.com.portfolio.taskmanager.service.TarefaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TarefaControllerTest {

    private MockMvc mvc;
    private TarefaService service;

    private TarefaResponse resposta(StatusTarefa status) {
        return new TarefaResponse(
                1L, "Tarefa teste", "Descricao", status, null, null
        );
    }

    @BeforeEach
    void preparar() {
        service = mock(TarefaService.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mvc = MockMvcBuilders
                .standaloneSetup(new TarefaController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void deveCriarTarefaComHttp201ELocation() throws Exception {
        when(service.criar(any())).thenReturn(resposta(StatusTarefa.PENDENTE));

        mvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Tarefa teste\",\"descricao\":\"Descricao\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/tarefas/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void deveListarTarefas() throws Exception {
        when(service.listar()).thenReturn(List.of(resposta(StatusTarefa.PENDENTE)));

        mvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Tarefa teste"));
    }

    @Test
    void deveRetornarListaVazia() throws Exception {
        when(service.listar()).thenReturn(List.of());

        mvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deveBuscarTarefaExistente() throws Exception {
        when(service.buscar(1L)).thenReturn(resposta(StatusTarefa.PENDENTE));

        mvc.perform(get("/api/tarefas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar404ParaTarefaInexistente() throws Exception {
        when(service.buscar(99L))
                .thenThrow(new TarefaNaoEncontradaException(99L));

        mvc.perform(get("/api/tarefas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Tarefa nao encontrada"));
    }

    @Test
    void deveAtualizarTarefa() throws Exception {
        when(service.atualizar(eq(1L), any()))
                .thenReturn(resposta(StatusTarefa.EM_ANDAMENTO));

        mvc.perform(put("/api/tarefas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Atualizada\",\"descricao\":\"Descricao\",\"status\":\"EM_ANDAMENTO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    void deveConcluirTarefa() throws Exception {
        when(service.concluir(1L))
                .thenReturn(resposta(StatusTarefa.CONCLUIDA));

        mvc.perform(patch("/api/tarefas/1/concluir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));
    }

    @Test
    void deveExcluirTarefa() throws Exception {
        mvc.perform(delete("/api/tarefas/1"))
                .andExpect(status().isNoContent());

        verify(service).excluir(1L);
    }

    @Test
    void deveRejeitarTituloVazio() throws Exception {
        mvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Dados invalidos"));

        verifyNoInteractions(service);
    }

    @Test
    void deveRejeitarTituloAcimaDoLimite() throws Exception {
        String titulo = "A".repeat(151);

        mvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"" + titulo + "\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void deveRejeitarPutSemStatus() throws Exception {
        mvc.perform(put("/api/tarefas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Atualizada\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        org.hamcrest.Matchers.containsString("status")
                ));

        verifyNoInteractions(service);
    }

    @Test
    void deveRejeitarJsonMalformado() throws Exception {
        mvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisicao invalida"));

        verifyNoInteractions(service);
    }
}