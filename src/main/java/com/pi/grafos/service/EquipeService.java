package com.pi.grafos.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Equipe;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.Turno;
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.EquipeRepository;
import com.pi.grafos.repository.FuncionarioRepository;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final AmbulanciaRepository ambulanciaRepository;
    private final FuncionarioRepository funcionarioRepository; // Adicionado para buscar funcionários

    public EquipeService(EquipeRepository equipeRepository, 
                         AmbulanciaRepository ambulanciaRepository,
                         FuncionarioRepository funcionarioRepository) {
        this.equipeRepository = equipeRepository;
        this.ambulanciaRepository = ambulanciaRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Equipe> listarTodas() {
        return equipeRepository.findAll();
    }

    /**
     * Retorna apenas os funcionários que NÃO estão em nenhuma equipe do turno especificado.
     * Se estivermos editando uma equipe (equipeAtual != null), os membros dela são considerados disponíveis.
     */
    public List<Funcionario> buscarDisponiveisParaTurno(Turno turno, Equipe equipeAtual) {
        // 1. Pega todos os funcionários do banco
        List<Funcionario> todos = funcionarioRepository.findAll();
        
        if (turno == null) return todos; // Se não escolheu turno, mostra todo mundo (ou retorna vazio, dependendo da regra)

        // 2. Pega todas as equipes do banco
        List<Equipe> todasEquipes = equipeRepository.findAll();

        // 3. Cria uma lista de IDs de quem já está ocupado nesse turno
        Set<Long> idsOcupados = new HashSet<>();
        
        for (Equipe eq : todasEquipes) {
            // Verifica se é do mesmo turno
            if (eq.getTurno() == turno) {
                 // Se for a equipe que estamos editando, IGNORE (os membros dela continuam disponíveis para ela mesma)
                 if (equipeAtual != null && eq.getIdEquipe().equals(equipeAtual.getIdEquipe())) {
                     continue; 
                 }
                 
                 // Adiciona os membros dessa equipe na lista de ocupados
                 if (eq.getMembros() != null) {
                     for (Funcionario membro : eq.getMembros()) {
                         idsOcupados.add(membro.getIdFuncionario());
                     }
                 }
            }
        }

        // 4. Filtra e retorna apenas quem não está na lista de ocupados
        return todos.stream()
                .filter(f -> !idsOcupados.contains(f.getIdFuncionario()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Equipe salvarEquipe(Equipe equipe, List<Funcionario> membros, Ambulancia ambulancia, Turno turno) {
        // Atualiza os dados da entidade
        equipe.setMembros(membros);
        equipe.setAmbulancia(ambulancia);
        equipe.setTurno(turno);

        // 1. Salva a equipe
        Equipe equipeSalva = equipeRepository.save(equipe);

        // 2. Lógica de liberação da Ambulância
        if (ambulancia != null) {
            // Se a ambulância não estiver em manutenção, marca como DISPONÍVEL
            if (ambulancia.getStatusAmbulancia() != AmbulanciaStatus.EM_MANUTENCAO) {
                ambulancia.setStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);
                ambulancia.setIsAtivo(true);
                ambulanciaRepository.save(ambulancia);
            }
        }

        return equipeSalva;
    }

    @Transactional
    public void excluirEquipe(Long id) { // Nome ajustado para casar com a View (antes era deletarEquipe)
        Optional<Equipe> equipeOpt = equipeRepository.findById(id);
        if (equipeOpt.isPresent()) {
            Equipe equipe = equipeOpt.get();
            
            // Se deletar a equipe, a ambulância volta a ficar INDISPONÍVEL (sem tripulação)
            if (equipe.getAmbulancia() != null) {
                Ambulancia amb = equipe.getAmbulancia();
                amb.setStatusAmbulancia(AmbulanciaStatus.INDISPONIVEL);
                ambulanciaRepository.save(amb);
            }
            equipeRepository.deleteById(id);
        }
    }
    
    public Optional<Equipe> buscarPorId(Long id) {
        return equipeRepository.findById(id);
    }
}