package com.pi.grafos.service;

import org.springframework.stereotype.Service;

import com.pi.grafos.repository.EquipeRepository;

@Service
public class EquipeService {
    private final EquipeRepository repository;

    public EquipeService(EquipeRepository repository){
        this.repository = repository;
    }

    public void cadastrarEquipe(String nome){

    }
}