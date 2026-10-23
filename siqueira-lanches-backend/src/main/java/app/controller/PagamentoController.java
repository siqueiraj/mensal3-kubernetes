package app.controller;

import java.util.List;

import app.dto.PagamentoDTO;
import app.entity.Pagamento;
import app.entity.Pedido;
import app.service.PagamentoService;
import app.service.PedidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PedidoService pedidoService;

    @PreAuthorize("hasAnyAuthority('USUARIO', 'ADMIN')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<Pagamento> findById(@PathVariable("id") long id) {
        System.out.println("✅ PagamentoController: Authentication no controller: " + SecurityContextHolder.getContext().getAuthentication());

        return pagamentoService.findById(id)
                .map(pagamento -> new ResponseEntity<>(pagamento, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('USUARIO', 'ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody PagamentoDTO dto) {
        System.out.println("✅ PagamentoController: Authentication no controller: " + SecurityContextHolder.getContext().getAuthentication());

        Pedido pedido = pedidoService.findById(dto.getPedidoId()).orElse(null);
        if (pedido == null) {
            return new ResponseEntity<>("Pedido não encontrado", HttpStatus.BAD_REQUEST);
        }

        if (pagamentoService.findByPedidoId(dto.getPedidoId()).isPresent()) {
            return new ResponseEntity<>("Pagamento já existe para este pedido", HttpStatus.CONFLICT);
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValor(dto.getValor() != null ? new java.math.BigDecimal(dto.getValor()) : null);

        Pagamento salvo = pagamentoService.save(pagamento);
        return new ResponseEntity<>(salvo, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Pagamento> update(@PathVariable("id") long id, @RequestBody PagamentoDTO dto) {
        System.out.println("✅ PagamentoController: Authentication no controller: " + SecurityContextHolder.getContext().getAuthentication());

        Pedido pedido = pedidoService.findById(dto.getPedidoId()).orElse(null);
        if (pedido == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setId(id);
        pagamento.setPedido(pedido);
        pagamento.setValor(dto.getValor() != null ? new java.math.BigDecimal(dto.getValor()) : null);

        Pagamento atualizado = pagamentoService.save(pagamento);
        return new ResponseEntity<>(atualizado, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/listAll")
    public ResponseEntity<List<Pagamento>> listAll() {
        System.out.println("✅ PagamentoController: Authentication no controller: " + SecurityContextHolder.getContext().getAuthentication());

        List<Pagamento> pagamentos = pagamentoService.listAll();
        return new ResponseEntity<>(pagamentos, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        System.out.println("✅ PagamentoController: Authentication no controller: " + SecurityContextHolder.getContext().getAuthentication());

        pagamentoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
