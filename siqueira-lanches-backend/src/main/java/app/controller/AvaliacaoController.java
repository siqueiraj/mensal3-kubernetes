package app.controller;

import java.util.List;

import app.entity.Avaliacao;
import app.service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @PostMapping("/save")
    public ResponseEntity<Avaliacao> save(@RequestBody Avaliacao avaliacao) {
        Avaliacao novaAvaliacao = avaliacaoService.save(avaliacao);
        return new ResponseEntity<>(novaAvaliacao, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Avaliacao> update(@PathVariable("id") long id, @RequestBody Avaliacao avaliacao) {
        avaliacao.setId(id);
        Avaliacao avaliacaoAtualizada = avaliacaoService.save(avaliacao);
        return new ResponseEntity<>(avaliacaoAtualizada, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @GetMapping("/listAll")
    public ResponseEntity<List<Avaliacao>> listAll() {
        List<Avaliacao> avaliacoes = avaliacaoService.listAll();
        return new ResponseEntity<>(avaliacoes, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<Avaliacao> findById(@PathVariable("id") long id) {
        return avaliacaoService.findById(id)
                .map(avaliacao -> new ResponseEntity<>(avaliacao, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        avaliacaoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
