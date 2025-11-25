package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;

@Repository
public interface AmbulanciaRepository extends JpaRepository<Ambulancia, Long> {

    Optional<Ambulancia> findByPlaca(String placa);

    Optional<Ambulancia> findByIdAmbulancia(Long id);

    Optional<Ambulancia> findByPlacaIgnoreCase(String placa);

    List<Ambulancia> findByStatusAmbulancia(AmbulanciaStatus status);

    List<Ambulancia> findByTipoAmbulancia(TipoAmbulancia tipo);

    List<Ambulancia> findByUnidade(Localizacao local);
}
