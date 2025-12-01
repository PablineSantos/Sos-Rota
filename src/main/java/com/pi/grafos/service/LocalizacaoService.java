package com.pi.grafos.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Rua;
import com.pi.grafos.repository.LocalizacaoRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalizacaoService {
    
    private final LocalizacaoRepository repository;

    public LocalizacaoService(LocalizacaoRepository repository){
        this.repository = repository;
    }

    @Transactional
    public Localizacao cadastrar(String nome, Cidade cidade) {
        //
        if (cidade == null || cidade.getIdCidade() == null){
            throw new IllegalArgumentException("Uma cidade válida é obrigatória.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da localização não pode ser vazio.");
        }

        Localizacao local = new Localizacao();
        local.setNome(nome);
        local.setCidade(cidade);

        return repository.save(local);
    }

    @Transactional
    public Localizacao editar(Long idLocal, String novoNome, Cidade novaCidade) {
        // Validação
        if (idLocal == null) {
            throw new IllegalArgumentException("O ID da localização é necessário para edição.");
        }
        
        // Pega o Objeto do banco
        Localizacao localExistente = repository.findById(idLocal)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada para edição."));

        // Edita os Campos
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            localExistente.setNome(novoNome);
        }
        
        if (novaCidade != null && novaCidade.getIdCidade() != null) {
            localExistente.setCidade(novaCidade);
        }

        // Salva
        return repository.save(localExistente);
    }

    @Transactional
    public void deletar(Long idLocal) {
        // Checa se existe
        if (!repository.existsById(idLocal)) {
             throw new RuntimeException("Localização não encontrada para exclusão.");
        }

        // Deleta
        repository.deleteById(idLocal);
    }

    @Transactional(readOnly = true)
    public List<Rua> listarAdjacencias(Long idLocalizacao) {
        // Pega a Localização
        Localizacao local = repository.findById(idLocalizacao)
            .orElseThrow(() -> new RuntimeException("Localização não encontrada"));

        // Inicializa as Listas
        List<Rua> todas = new ArrayList<>();
        
        // We force Hibernate to fetch the data by accessing the list
        if (local.getRuasSaindo() != null) {
            todas.addAll(local.getRuasSaindo());
        }
        
        if (local.getRuasChegando() != null) {
            todas.addAll(local.getRuasChegando());
        }

        return todas;
    }

    @Transactional(readOnly = true)
    public Localizacao buscarCompleto(Long id) {
        Localizacao local = repository.findById(id).orElse(null);
        if(local != null) {
            // Force load the lists (Initialize)
            Hibernate.initialize(local.getRuasSaindo());
            Hibernate.initialize(local.getRuasChegando());
            Hibernate.initialize(local.getAmbulancias());
        }
        return local;
    }

}
