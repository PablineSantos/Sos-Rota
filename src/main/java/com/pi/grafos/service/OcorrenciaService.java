package com.pi.grafos.service;

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

    public void cadastrarOcorrencia(String desc, Localizacao local, TipoOcorrencia tipo, OcorrenciaStatus gravidade){
        Ocorrencia o = new Ocorrencia();
        
        o.setDescricao(desc);
        o.setLocal(local);
        o.setTipoOcorrencia(tipo);
        o.setGravidade(gravidade);

        repository.save(o);
    }

}
