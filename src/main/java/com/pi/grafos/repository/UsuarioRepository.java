package com.pi.grafos.repository;

import com.pi.grafos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Login: Encontrar pelo Usuário
    Optional<Usuario> findByUsuario(String usuario);

    // Registro: Encontra se o nome de usuário já existe
    boolean existsByUsuario(String usuario);
}