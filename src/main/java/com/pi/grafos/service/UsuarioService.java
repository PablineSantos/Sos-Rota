package com.pi.grafos.service;

import com.pi.grafos.model.Usuario;
import com.pi.grafos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder; // Classe para lidar com a criptografia da senha

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registro de novo usuário
    public void cadastrarUsuario(String usuario, String rawSenha) {
        Usuario user = new Usuario();
        user.setUsuario(usuario);
        
        // CRIPTOGRAFIA DA SENHA
        String hash = passwordEncoder.encode(rawSenha); 
        user.setSenha(hash);

        repository.save(user);
    }

    // Login de usuário
    public boolean autenticar(String usuario, String rawSenha) {
        Optional<Usuario> userOpt = repository.findByUsuario(usuario);

        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            // Verificação se a senha é a mesma que a senha criptografada
            return passwordEncoder.matches(rawSenha, user.getSenha());
        }
        return false;
    }
}