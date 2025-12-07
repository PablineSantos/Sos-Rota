package com.pi.grafos.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.pi.grafos.model.Equipe;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;
import com.pi.grafos.repository.AmbulanciaRepository;

@Service
public class AmbulanciaService {

    private final AmbulanciaRepository repository;

    // Regex para Placa (Padrão Antigo AAA-1234 ou Mercosul AAA1B23)
    private static final Pattern PADRAO_PLACA = Pattern.compile("^[A-Z]{3}-?[0-9][0-9A-Z][0-9]{2}$");

    public AmbulanciaService(AmbulanciaRepository repository) {
        this.repository = repository;
    }

    public List<Ambulancia> listarTodas() {
        return repository.findAll();
    }

    @Transactional
    public void salvarOuAtualizar(Long id, String placa, TipoAmbulancia tipo,
                                  Localizacao base, AmbulanciaStatus statusDesejado) {

        // 1. Validação de Campos Obrigatórios
        if (placa == null || placa.trim().isEmpty()) throw new IllegalArgumentException("A placa é obrigatória.");
        if (tipo == null) throw new IllegalArgumentException("O tipo da ambulância é obrigatório.");
        if (base == null) throw new IllegalArgumentException("A base de lotação é obrigatória.");

        // 2. Normalização e Validação de Formato (Regex)
        String placaFormatada = placa.toUpperCase().trim();
        if (!PADRAO_PLACA.matcher(placaFormatada).matches()) {
            throw new IllegalArgumentException("Formato de placa inválido. Use o padrão ABC-1234 ou ABC1D23.");
        }

        // 3. Validação de Duplicidade (Regra de Negócio)
        Optional<Ambulancia> existente = repository.findByPlacaIgnoreCase(placaFormatada);
        if (existente.isPresent()) {
            // Se for novo cadastro (id null) e já existe -> Erro
            // Se for edição (id não null) e o id encontrado é diferente do atual -> Erro (tentando usar placa de outro)
            if (id == null || !existente.get().getIdAmbulancia().equals(id)) {
                throw new IllegalArgumentException("Já existe uma ambulância cadastrada com a placa " + placaFormatada);
            }
        }

        Ambulancia ambulancia;

        if (id == null) {
            // --- CENÁRIO 1: NOVO CADASTRO ---
            ambulancia = new Ambulancia();
            // REGRA DE OURO: Ambulância nova nasce INDISPONÍVEL pois não tem equipe ainda.
            // Ignoramos o status que veio da tela se for "Disponível".
            ambulancia.setStatusAmbulancia(AmbulanciaStatus.INDISPONIVEL);
            ambulancia.setIsAtivo(true);
        } else {
            // --- CENÁRIO 2: EDIÇÃO ---
            ambulancia = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ambulância não encontrada."));

            // Se o usuário tentou mudar para DISPONÍVEL, precisamos validar a equipe
            if (statusDesejado == AmbulanciaStatus.DISPONIVEL) {
                validarRegrasParaDisponibilidade(ambulancia, tipo);
            }

            // Se passou na validação (ou se é outro status como Manutenção), aplica.
            if (statusDesejado != null) {
                ambulancia.setStatusAmbulancia(statusDesejado);
            }
        }

        // Atualiza dados comuns
        ambulancia.setPlaca(placaFormatada);
        ambulancia.setTipoAmbulancia(tipo);
        ambulancia.setUnidade(base);

        repository.save(ambulancia);
    }

    /**
     * Método Especialista: Verifica se a ambulância TEM CONDIÇÕES de trabalhar.
     */
    private void validarRegrasParaDisponibilidade(Ambulancia amb, TipoAmbulancia tipoAtual) {
        // 1. Tem alguma equipe vinculada?
        List<Equipe> equipes = amb.getEquipes();
        if (equipes == null || equipes.isEmpty()) {
            throw new IllegalStateException("Não é possível marcar como DISPONÍVEL: Nenhuma equipe vinculada a este veículo.");
        }

        // 2. Pega a primeira equipe (Lógica simplificada: assume que a equipe ativa é a da lista)
        // Num sistema real com turnos, você filtraria pela hora atual.
        Equipe equipeAtiva = equipes.get(0);
        List<Funcionario> membros = equipeAtiva.getMembros();

        if (membros == null || membros.isEmpty()) {
            throw new IllegalStateException("A equipe vinculada '" + equipeAtiva.getNomeEquipe() + "' não possui membros cadastrados.");
        }

        // 3. Contagem de Cargos
        boolean temMedico = membros.stream().anyMatch(f -> f.getCargo() == Cargos.MEDICO);
        boolean temEnfermeiro = membros.stream().anyMatch(f -> f.getCargo() == Cargos.ENFERMEIRO);
        boolean temCondutor = membros.stream().anyMatch(f -> f.getCargo() == Cargos.CONDUTOR);

        // 4. Regra Específica por Tipo (Conforme PDF)
        if (tipoAtual == TipoAmbulancia.UTI) {
            if (!temMedico || !temEnfermeiro || !temCondutor) {
                throw new IllegalStateException("Ambulância UTI exige equipe completa (Médico + Enfermeiro + Condutor) para ficar Disponível.");
            }
        } else {
            // Regra para Básica (Geralmente Condutor + Enfermeiro/Tecnico)
            if (!temCondutor || !temEnfermeiro) {
                throw new IllegalStateException("Ambulância Básica exige no mínimo Condutor e Enfermeiro para ficar Disponível.");
            }
        }
    }

    @Transactional
    public void deletar(Long id) {
        Ambulancia ambulancia = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ambulância não encontrada."));

        // VALIDAÇÃO DE INTEGRIDADE (Não deixa apagar se tiver equipes usando)
        if (ambulancia.getEquipes() != null && !ambulancia.getEquipes().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir esta ambulância pois existem equipes vinculadas a ela. Desvincule as equipes primeiro.");
        }

        repository.delete(ambulancia);
    }

    public int contarAmbulancias() {
        return repository.countByStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);
    }
}