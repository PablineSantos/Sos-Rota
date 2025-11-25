package com.pi.grafos.service;

import com.pi.grafos.model.Usuario;
import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.repository.UsuarioRepository;
import com.pi.grafos.repository.AmbulanciaRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AmbulanciaRepository ambulanciaRepository;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            AmbulanciaRepository ambulanciaRepository
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.ambulanciaRepository = ambulanciaRepository;
    }

    // Registro de novo usuário
    public void cadastrarUsuario(String usuario, String rawSenha) {
        Usuario user = new Usuario();
        user.setUsuario(usuario);
        String hash = passwordEncoder.encode(rawSenha);
        user.setSenha(hash);
        user.setTipoUsuario(1);
        repository.save(user);
    }

public boolean autenticar(String usuario, String rawSenha) {
    Optional<Usuario> userOpt = repository.findByUsuario(usuario);

    if (userOpt.isPresent()) {
        Usuario user = userOpt.get();
        return passwordEncoder.matches(rawSenha, user.getSenha());
    }
    return false;
}



}
