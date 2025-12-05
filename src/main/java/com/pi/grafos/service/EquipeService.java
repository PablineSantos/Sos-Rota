package com.pi.grafos.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Equipe;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.AmbulanciaStatus; // Importante!
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.model.enums.Turno;
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.EquipeRepository;
import com.pi.grafos.repository.FuncionarioRepository;

@Service
public class EquipeService {

    private final EquipeRepository repository;
    private final FuncionarioRepository funcRepository;
    private final AmbulanciaRepository ambulanciaRepository; // Nova dependência

    // Injeção de dependência atualizada
    public EquipeService(EquipeRepository repository,
                         FuncionarioRepository funcRepository,
                         AmbulanciaRepository ambulanciaRepository){
        this.repository = repository;
        this.funcRepository = funcRepository;
        this.ambulanciaRepository = ambulanciaRepository;
    }

    public List<Equipe> listarTodas() {
        return repository.findAll();
    }

    public List<Funcionario> buscarDisponiveisParaTurno(Turno turno, Equipe equipeAtual) {
        List<Funcionario> disponiveis = funcRepository.findDisponiveisPorTurno(turno);

        if (equipeAtual != null && equipeAtual.getIdEquipe() != null) {
            Equipe equipeBanco = repository.findById(equipeAtual.getIdEquipe()).orElse(equipeAtual);
            for (Funcionario f : equipeBanco.getMembros()) {
                if (!disponiveis.contains(f)) {
                    disponiveis.add(f);
                }
            }
        }
        return disponiveis;
    }

    @Transactional
    public void salvarEquipe(Equipe equipe, List<Funcionario> membrosSelecionados, Ambulancia ambulancia, Turno turno) {

        // 1. Validações Básicas
        if (equipe.getNomeEquipe() == null || equipe.getNomeEquipe().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da equipe é obrigatório.");
        }
        if (turno == null) {
            throw new IllegalArgumentException("O turno de trabalho é obrigatório.");
        }
        if (membrosSelecionados == null || membrosSelecionados.isEmpty()) {
            throw new IllegalArgumentException("A equipe não pode estar vazia.");
        }

        // Validação de Limite Máximo
        if (membrosSelecionados.size() > equipe.getMaxMembros()) {
            throw new IllegalArgumentException(
                    "A equipe excedeu o limite máximo de " + equipe.getMaxMembros() + " profissionais."
            );
        }

        // 2. Validação de Composição Mínima
        boolean temMedico = membrosSelecionados.stream().anyMatch(f -> f.getCargo() == Cargos.MEDICO);
        boolean temEnfermeiro = membrosSelecionados.stream().anyMatch(f -> f.getCargo() == Cargos.ENFERMEIRO);
        boolean temCondutor = membrosSelecionados.stream().anyMatch(f -> f.getCargo() == Cargos.CONDUTOR);

        if (!temMedico || !temEnfermeiro || !temCondutor) {
            throw new IllegalArgumentException("Equipe incompleta! Obrigatório: 1 Médico, 1 Enfermeiro e 1 Condutor.");
        }

        // 3. Validação de Conflito de Horário
        for (Funcionario f : membrosSelecionados) {
            Funcionario fBanco = funcRepository.findById(f.getIdFuncionario()).orElse(f);

            for (Equipe eqDoFuncionario : fBanco.getEquipes()) {
                if (eqDoFuncionario.getTurno().equals(turno) && !eqDoFuncionario.getIdEquipe().equals(equipe.getIdEquipe())) {
                    throw new IllegalArgumentException("Conflito de Escala: O profissional " + f.getNomeFuncionario() +
                            " já está alocado na equipe '" + eqDoFuncionario.getNomeEquipe() + "' no turno da " + turno + ".");
                }
            }
        }

        // 4. Persistência da Equipe
        equipe.setTurno(turno);
        equipe.setAmbulancia(ambulancia);
        equipe.setMembros(membrosSelecionados);

        repository.save(equipe);

        if (ambulancia != null) {
            // Recarrega do banco para ter o status mais atual
            Ambulancia ambBanco = ambulanciaRepository.findById(ambulancia.getIdAmbulancia())
                    .orElse(ambulancia);

            // Se ela estava parada (INDISPONIVEL) e agora ganhou equipe -> Vira DISPONIVEL
            // OBS: Não altera se estiver em MANUTENCAO ou EM_ATENDIMENTO por segurança
            if (ambBanco.getStatusAmbulancia() == AmbulanciaStatus.INDISPONIVEL) {
                ambBanco.setStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);
                ambulanciaRepository.save(ambBanco);
            }
        }
    }

    @Transactional
    public void excluirEquipe(Long id) {
        Equipe equipe = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipe não encontrada."));

        equipe.getMembros().clear();
        repository.delete(equipe);
    }
}