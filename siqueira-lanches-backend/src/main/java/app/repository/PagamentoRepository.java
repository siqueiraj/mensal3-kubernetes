package app.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import app.entity.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
	Optional<Pagamento> findByPedidoId(Long pedidoId);
}
