package com.pi.grafos.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.TipoOcorrencia;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.OcorrenciaStatus;
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.OcorrenciaRepository;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final AmbulanciaRepository ambulanciaRepository; 

    // Construtor com injeção das dependências necessárias
    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository, AmbulanciaRepository ambulanciaRepository){
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.ambulanciaRepository = ambulanciaRepository;
    }

    public List<Ocorrencia> findAll(){
        return ocorrenciaRepository.findAll();
    }

    public List<Ocorrencia> findByGravidade(OcorrenciaStatus c){
        return ocorrenciaRepository.findByGravidade(c);
    }

    public void cadastrarOcorrencia(String desc, Localizacao local, TipoOcorrencia tipo, OcorrenciaStatus gravidade){
        Ocorrencia o = new Ocorrencia();

        o.setDescricao(desc);
        o.setLocal(local);
        o.setTipoOcorrencia(tipo);
        o.setGravidade(gravidade);
        
        // A dataHoraChamado já é definida no construtor da Ocorrencia (conforme alteramos no passo anterior),
        // mas se quiser garantir aqui: o.setDataHoraChamado(LocalDateTime.now());

        ocorrenciaRepository.save(o);
    }

    public void deleteOcorrencia(long id){
        Optional<Ocorrencia> c = ocorrenciaRepository.findById(id);
        if(c.isPresent()){
            Ocorrencia o = c.get();
            ocorrenciaRepository.delete(o);
        }
    }

    public void editOcorrencia(long id, String desc, Localizacao local, TipoOcorrencia tipo, OcorrenciaStatus gravidade){
        Ocorrencia c = ocorrenciaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        c.setDescricao(desc);
        c.setLocal(local);
        c.setTipoOcorrencia(tipo);
        c.setGravidade(gravidade);

        ocorrenciaRepository.save(c);
    }

    /**
     * Realiza o despacho de uma ambulância para uma ocorrência.
     * Atualiza o status da ambulância para EM_ATENDIMENTO e vincula ela à ocorrência.
     */
    @Transactional
    public void despacharAmbulancia(Long idOcorrencia, Long idAmbulancia) {
        // 1. Busca a Ocorrência
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(idOcorrencia)
            .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        // 2. Busca a Ambulância
        Ambulancia ambulancia = ambulanciaRepository.findById(idAmbulancia)
            .orElseThrow(() -> new RuntimeException("Ambulância não encontrada"));

        // 3. Verifica se já não está ocupada (segurança extra)
        if (ambulancia.getStatusAmbulancia() == AmbulanciaStatus.EM_ATENDIMENTO) {
            throw new IllegalStateException("Esta ambulância já está em atendimento!");
        }

        // 4. Realiza o vínculo e troca o status
        ocorrencia.setAmbulancia(ambulancia);
        ambulancia.setStatusAmbulancia(AmbulanciaStatus.EM_ATENDIMENTO); // Torna indisponível para novos chamados

        // 5. Salva ambos (Graças ao @Transactional, se um falhar, o outro não é salvo)
        ambulanciaRepository.save(ambulancia);
        ocorrenciaRepository.save(ocorrencia);
    }

    // =================================================================================
    // MÉTODOS DE CÁLCULO (KPIs)
    // =================================================================================

    // 1. Método genérico para calcular média de minutos
    public String calcularTempoMedioGeral() {
        List<Ocorrencia> todas = ocorrenciaRepository.findAll();
        return calcularMediaDeLista(todas);
    }

    // 2. Método para calcular por Gravidade (Alta, Média, Baixa)
    public String calcularTempoMedioPorGravidade(OcorrenciaStatus gravidade) {
        List<Ocorrencia> filtradas = ocorrenciaRepository.findByGravidade(gravidade);
        return calcularMediaDeLista(filtradas);
    }

    // Lógica Matemática Auxiliar
    private String calcularMediaDeLista(List<Ocorrencia> lista) {
        if (lista == null || lista.isEmpty()) {
            return "0 min"; // Sem dados
        }

        long totalMinutos = 0;
        int quantidadeConsiderada = 0;

        for (Ocorrencia o : lista) {
            // Só calculamos se já tiver data de chegada registradas
            if (o.getDataHoraChamado() != null && o.getDataHoraChegada() != null) {
                // Calcula a duração entre Chamado e Chegada
                Duration duracao = Duration.between(o.getDataHoraChamado(), o.getDataHoraChegada());
                totalMinutos += duracao.toMinutes();
                quantidadeConsiderada++;
            }
        }

        if (quantidadeConsiderada == 0) return "0 min";

        long media = totalMinutos / quantidadeConsiderada;
        return media + " min";
    }
}