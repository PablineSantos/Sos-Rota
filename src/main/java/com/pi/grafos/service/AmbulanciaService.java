package com.pi.grafos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;
import com.pi.grafos.repository.AmbulanciaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AmbulanciaService {

    private final AmbulanciaRepository repository;

    public AmbulanciaService(AmbulanciaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Ambulancia cadastrar(
        String placa, 
        TipoAmbulancia tipo, 
        Localizacao unidade) {
        // 1. Validate inputs
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("A placa da ambulância é obrigatória.");
        }
        if (unidade == null || unidade.getIdLocal() == null) {
            throw new IllegalArgumentException("A ambulância deve estar vinculada a uma unidade (Localização) válida.");
        }

        // 2. Check for duplicate License Plate (Business Rule)
        Optional<Ambulancia> existente = repository.findByPlacaIgnoreCase(placa);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma ambulância cadastrada com a placa: " + placa);
        }

        // 3. Create and Save
        Ambulancia ambulancia = new Ambulancia();
        ambulancia.setPlaca(placa.toUpperCase());
        ambulancia.setTipoAmbulancia(tipo);
        ambulancia.setUnidade(unidade);

        // Default status when created (Assuming you have a DISPONIVEL or similar status)
        ambulancia.setStatusAmbulancia(AmbulanciaStatus.DISPONIVEL); 

        return repository.save(ambulancia);
    }


    @Transactional
    public Ambulancia editar(Long idAmbulancia, String novaPlaca, TipoAmbulancia novoTipo, AmbulanciaStatus novoStatus, Localizacao novaUnidade) {
        // 1. Find existing
        Ambulancia ambulancia = repository.findById(idAmbulancia)
                .orElseThrow(() -> new RuntimeException("Ambulância não encontrada."));

        // 2. Update Placa (with Unique Check)
        if (novaPlaca != null && !novaPlaca.isBlank() && !novaPlaca.equalsIgnoreCase(ambulancia.getPlaca())) {
            // If the plate is changing, check if the NEW plate is already taken by someone else
            Optional<Ambulancia> placaConflict = repository.findByPlacaIgnoreCase(novaPlaca);
            if (placaConflict.isPresent()) {
                throw new IllegalArgumentException("A placa " + novaPlaca + " já está em uso por outra ambulância.");
            }
            ambulancia.setPlaca(novaPlaca.toUpperCase());
        }

        // 3. Update other fields if provided
        if (novoTipo != null) {
            ambulancia.setTipoAmbulancia(novoTipo);
        }
        if (novoStatus != null) {
            ambulancia.setStatusAmbulancia(novoStatus);
        }
        if (novaUnidade != null && novaUnidade.getIdLocal() != null) {
            ambulancia.setUnidade(novaUnidade);
        }

        return repository.save(ambulancia);
    }

    /**
     * DELETE
     */
    @Transactional
    public void deletar(Long idAmbulancia) {
        if (!repository.existsById(idAmbulancia)) {
            throw new RuntimeException("Ambulância não encontrada para exclusão.");
        }
        repository.deleteById(idAmbulancia);
    }

    // --- FINDERS (Read Only) ---

    @Transactional(readOnly = true)
    public List<Ambulancia> listarTodas() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Ambulancia> listarPorStatus(AmbulanciaStatus status) {
        return repository.findByStatusAmbulancia(status);
    }

    @Transactional(readOnly = true)
    public List<Ambulancia> listarPorUnidade(Localizacao unidade) {
        return repository.findByUnidade(unidade);
    }
}