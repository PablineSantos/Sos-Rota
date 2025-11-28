package com.pi.grafos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.TipoOcorrencia;
import com.pi.grafos.repository.TipoOcorrenciaRepository;

@Service
public class TipoOcorrenciaService {
    
    private TipoOcorrenciaRepository repository;

    public TipoOcorrenciaService(TipoOcorrenciaRepository repository){
        this.repository = repository;
    }

    public List<TipoOcorrencia> findAll(){
        return repository.findAll();
    }

    public TipoOcorrencia cadastrar(String nome){
        TipoOcorrencia t = new TipoOcorrencia();

        t.setNomeTipoOcorrencia(nome);

        return repository.save(t);
    }

    public TipoOcorrencia editar(Long id, String nome, List<Ocorrencia> lista){
        if(id == null){
            throw new IllegalArgumentException("ID do tipo Ocorrencia não pode ser null");
        }
        
        TipoOcorrencia e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo Ocorrencia Não Encontrada"));
        
        e.setNomeTipoOcorrencia(nome);
        e.setOcorrencias(lista);

        return repository.save(e);
    }

    public void delete(Long id){
        if(id == null){
            throw new IllegalArgumentException("ID do tipo Ocorrencia não pode ser null");
        }

        TipoOcorrencia e = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("Tipo Ocorrencia não encontrada"));

        repository.delete(e);
    }
}
