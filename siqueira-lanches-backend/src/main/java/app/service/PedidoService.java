package app.service;

import app.entity.Pedido;
import app.entity.Produto;
import app.entity.Usuario;
import app.dto.PedidoDTO;
import app.entity.ItemPedido;
import app.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoService produtoService;

    public Pedido save(Pedido pedido) {
        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);

                Produto produto = produtoService.findById(item.getProduto().getId()).orElse(null);
                if (produto != null) {
                    item.setProduto(produto);
                }
            }

            BigDecimal total = pedido.getItens().stream()
                .filter(i -> i.getProduto() != null && i.getProduto().getPreco() != null && i.getQuantidade() != null)
                .map(i -> i.getProduto().getPreco().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            pedido.setValorTotal(total.doubleValue());
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido createPedidoFromDTO(PedidoDTO pedidoDTO, Usuario comprador) {
        Pedido pedido = new Pedido();
        pedido.setComprador(comprador);

        List<ItemPedido> itens = new ArrayList<>();

        for (PedidoDTO.ItemDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoService.findById(itemDTO.getProdutoId()).orElse(null);
            if (produto == null) continue;

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPedido(pedido);

            itens.add(item);
        }

        pedido.setItens(itens);
        return pedido;
    }

    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listAll() {
        return pedidoRepository.findAll();
    }
}
