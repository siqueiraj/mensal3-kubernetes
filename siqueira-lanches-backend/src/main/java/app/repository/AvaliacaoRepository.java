package app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import app.entity.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
}
