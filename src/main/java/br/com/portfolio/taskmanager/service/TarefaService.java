package br.com.portfolio.taskmanager.service;

import br.com.portfolio.taskmanager.dto.AtualizarTarefaRequest;
import br.com.portfolio.taskmanager.dto.CriarTarefaRequest;
import br.com.portfolio.taskmanager.dto.TarefaResponse;
import br.com.portfolio.taskmanager.entity.StatusTarefa;
import br.com.portfolio.taskmanager.entity.Tarefa;
import br.com.portfolio.taskmanager.exception.TarefaNaoEncontradaException;
import br.com.portfolio.taskmanager.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TarefaResponse criar(CriarTarefaRequest request) {
        Tarefa tarefa = new Tarefa(
                request.titulo().trim(),
                request.descricao()
        );

        return TarefaResponse.from(repository.save(tarefa));
    }

    @Transactional(readOnly = true)
    public List<TarefaResponse> listar() {
        return repository.findAll()
                .stream()
                .map(TarefaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TarefaResponse buscar(Long id) {
        return TarefaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public TarefaResponse atualizar(Long id, AtualizarTarefaRequest request) {
        Tarefa tarefa = buscarEntidade(id);

        tarefa.setTitulo(request.titulo().trim());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(request.status());

        repository.flush();

        return TarefaResponse.from(tarefa);
    }

    @Transactional
    public TarefaResponse concluir(Long id) {
        Tarefa tarefa = buscarEntidade(id);

        if (tarefa.getStatus() != StatusTarefa.CONCLUIDA) {
            tarefa.setStatus(StatusTarefa.CONCLUIDA);
            repository.flush();
        }

        return TarefaResponse.from(tarefa);
    }

    @Transactional
    public void excluir(Long id) {
        Tarefa tarefa = buscarEntidade(id);
        repository.delete(tarefa);
    }

    private Tarefa buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }
}