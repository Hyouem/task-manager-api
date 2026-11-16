package br.com.portfolio.taskmanager.repository;

import br.com.portfolio.taskmanager.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}