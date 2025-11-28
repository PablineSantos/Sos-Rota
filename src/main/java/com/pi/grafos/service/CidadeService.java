package com.pi.grafos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Rua;
import com.pi.grafos.repository.CidadeRepository;

@Service
public class CidadeService {
    
    private CidadeRepository repository;

    public CidadeService(CidadeRepository repository){
        this.repository = repository;
    }

    public Cidade cadastrar(String nome){
        Cidade c = new Cidade();

        c.setNomeCidade(nome);

        return repository.save(c);
    }

    public Cidade editar(Long id, String nome, List<Localizacao> locais, List<Rua> ruas){
        if(id == null){
            throw new IllegalArgumentException("O ID da Cidade é necessário para edição");
        }

        Cidade cidade = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        if(nome != null && !nome.trim().isEmpty()){
            cidade.setNomeCidade(nome);
        }

        cidade.setLocais(locais);
        cidade.setRuas(ruas);

        return repository.save(cidade);
    }

    public void deleteCidade(Cidade e){
        repository.delete(e);
    }

    public List<Cidade> findAll(){
        return repository.findAll();
    }
}