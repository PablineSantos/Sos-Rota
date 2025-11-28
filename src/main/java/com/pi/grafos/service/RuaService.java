package com.pi.grafos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Rua;
import com.pi.grafos.repository.RuaRepository;

@Service
public class RuaService {

    private RuaRepository repository;

    public RuaService(RuaRepository repository){
        this.repository = repository;
    }

    public List<Rua> findAll(){
        return repository.findAll();
    }

    public Rua cadastrar(String nome, Double d, Cidade c, Localizacao origin, Localizacao fim){

        Rua r = new Rua();

        r.setNomeRua(nome);
        r.setDistancia(d);
        r.setCidade(c);
        r.setOrigem(origin);
        r.setDestino(fim);

        return repository.save(r);

    }
    
    public Rua edit(Long id, String nome, Double d, Cidade c, Localizacao origin, Localizacao fim){
        if(id == null){
            throw new IllegalArgumentException("ID do tipo Ocorrencia não pode ser null");
        }

        Rua r = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rua não encontrada"));
        
        r.setNomeRua(nome);
        r.setDistancia(d);
        r.setCidade(c);
        r.setOrigem(origin);
        r.setDestino(fim);

        return repository.save(r);
    }
}
