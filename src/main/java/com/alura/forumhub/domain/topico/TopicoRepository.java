package com.alura.forumhub.domain.topico;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensagem(@NotBlank(message = "Titulo é obrigatorio") String titulo, @NotBlank(message = "Mensagem é obrigatória") String mensagem);
    @Query("""
            SELECT t FROM Topico t
            JOIN FETCH t.autor
            JOIN FETCH t.curso
            WHERE (:nomeCurso IS NULL OR t.curso.nome = :nomeCurso)
              AND  (:ano IS NULL OR FUNCTION('YEAR', t.dataCriacao) = :ano)
            ORDER BY t.dataCriacao ASC                                        
          """)
    Page<Topico> findAlllWithFilters(
            @Param("nomeCurso") String nomeCurso,
            @Param("ano") Integer ano,
            Pageable  pageable
    );
}
