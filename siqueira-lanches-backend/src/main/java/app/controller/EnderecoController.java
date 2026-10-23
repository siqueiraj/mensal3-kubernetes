package app.controller;

import java.util.List;

import app.entity.Endereco;
import app.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enderecos")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @PostMapping("/save")
    public ResponseEntity<Endereco> save(@RequestBody Endereco endereco) {
        Endereco novoEndereco = enderecoService.save(endereco);
        return new ResponseEntity<>(novoEndereco, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Endereco> update(@PathVariable("id") long id, @RequestBody Endereco endereco) {
        endereco.setId(id);
        Endereco enderecoAtualizado = enderecoService.save(endereco);
        return new ResponseEntity<>(enderecoAtualizado, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @GetMapping("/listAll")
    public ResponseEntity<List<Endereco>> listAll() {
        List<Endereco> enderecos = enderecoService.listAll();
        return new ResponseEntity<>(enderecos, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<Endereco> findById(@PathVariable("id") long id) {
        return enderecoService.findById(id)
                .map(endereco -> new ResponseEntity<>(endereco, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        enderecoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
