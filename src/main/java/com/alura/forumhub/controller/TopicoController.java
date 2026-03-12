package com.alura.forumhub.controller;

import com.alura.forumhub.domain.curso.Curso;
import com.alura.forumhub.domain.curso.CursoRepository;
import com.alura.forumhub.domain.topico.*;
import com.alura.forumhub.domain.usuario.Usuario;
import com.alura.forumhub.domain.usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/topicos")
public class TopicoController {
    private final TopicoRepository topicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    public TopicoController(TopicoRepository topicoRepository,
                            UsuarioRepository usuarioRepository,
                            CursoRepository cursoRepository) {
        this.topicoRepository = topicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoTopico> cadastrar(
            @RequestBody @Valid DadosCadastroTopico dados,
            UriComponentsBuilder uriBuilder) {

        if (topicoRepository.existsByTituloAndMensagem(dados.titulo(), dados.mensagem())) {
            throw new RuntimeException("Já existe um tópico com este titulo e mensagem");
        }

        Usuario autor = usuarioRepository.findById(dados.autorId())
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com id: " + dados.autorId()));

        Curso curso = cursoRepository.findById(dados.cursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com  id: " + dados.cursoId()));

        var topico = new Topico(dados, autor, curso);
        topicoRepository.save(topico);

        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoTopico(topico));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemTopico>> listar(
            @RequestParam(required = false) String nomeCurso,
            @RequestParam(required = false) Integer ano,
            @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC)
            Pageable pageable) {

        var page = topicoRepository
                .findAlllWithFilters(nomeCurso, ano, pageable)
                .map(DadosListagemTopico::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> detalhar(@PathVariable Long id) {

        var optional = topicoRepository.findById(id);

        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new DadosDetalhamentoTopico(optional.get()));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoTopico> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoTopico dados) {

        var optional = topicoRepository.findById(id);

        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        var topico = optional.get();

        String novoTitulo   = dados.titulo()   != null ? dados.titulo()   : topico.getTitulo();
        String novaMensagem = dados.mensagem()  != null ? dados.mensagem() : topico.getMensagem();

        boolean tituloOuMensagemMudou =
                !novoTitulo.equals(topico.getTitulo()) || !novaMensagem.equals(topico.getMensagem());

        if (tituloOuMensagemMudou && topicoRepository.existsByTituloAndMensagem(novoTitulo, novaMensagem)) {
            throw new RuntimeException("Já existe um tópico com este titulo e mensagem");
        }

        topico.atualizar(dados);
        return ResponseEntity.ok(new DadosDetalhamentoTopico(topico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        var optional = topicoRepository.findById(id);

        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        topicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

