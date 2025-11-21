package com.pi.grafos.repository;

import com.pi.grafos.model.Ambulancia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmbulanciaRepository extends JpaRepository<Ambulancia, Long> {
    Optional<Ambulancia> findByPlaca(String placa);
}
