package com.pi.grafos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.TipoOcorrencia;
import com.pi.grafos.model.enums.OcorrenciaStatus;
import com.pi.grafos.repository.OcorrenciaRepository;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;

    public OcorrenciaService(OcorrenciaRepository repository){
        this.repository = repository;
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
        .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        c.setDescricao(desc);
        c.setLocal(local);
        c.setTipoOcorrencia(tipo);
        c.setGravidade(gravidade);

        repository.save(c);
    }
}
