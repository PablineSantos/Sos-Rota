package com.pi.grafos.service;

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

    private final OcorrenciaRepository repository;
    private final AmbulanciaRepository ambulanciaRepository; 

    // Construtor com injeção das dependências necessárias
    public OcorrenciaService(OcorrenciaRepository repository, AmbulanciaRepository ambulanciaRepository){
        this.repository = repository;
        this.ambulanciaRepository = ambulanciaRepository;
    }

    public List<Ocorrencia> findAll(){
        return repository.findAll();
    }

    public List<Ocorrencia> findByGravidade(OcorrenciaStatus c){
        return repository.findByGravidade(c);
    }

    public void cadastrarOcorrencia(String desc, Localizacao local, TipoOcorrencia tipo, OcorrenciaStatus gravidade){
        Ocorrencia o = new Ocorrencia();

        o.setDescricao(desc);
        o.setLocal(local);
        o.setTipoOcorrencia(tipo);
        o.setGravidade(gravidade);

        repository.save(o);
    }

    public void deleteOcorrencia(long id){
        Optional<Ocorrencia> c = repository.findById(id);
        if(c.isPresent()){
            Ocorrencia o = c.get();
            repository.delete(o);
        }
    }

    public void editOcorrencia(long id, String desc, Localizacao local, TipoOcorrencia tipo, OcorrenciaStatus gravidade){
        Ocorrencia c = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada")); // Corrigido de "Funcionário" para "Ocorrência"

        c.setDescricao(desc);
        c.setLocal(local);
        c.setTipoOcorrencia(tipo);
        c.setGravidade(gravidade);

        repository.save(c);
    }

    /**
     * Realiza o despacho de uma ambulância para uma ocorrência.
     * Atualiza o status da ambulância para EM_ATENDIMENTO e vincula ela à ocorrência.
     */
    @Transactional
    public void despacharAmbulancia(Long idOcorrencia, Long idAmbulancia) {
        // 1. Busca a Ocorrência
        Ocorrencia ocorrencia = repository.findById(idOcorrencia)
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
        repository.save(ocorrencia);
    }
}