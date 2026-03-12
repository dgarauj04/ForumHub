package com.alura.forumhub.domain.usuario;

import com.alura.forumhub.domain.topico.DadosListagemTopico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByEmail(String email);
}
