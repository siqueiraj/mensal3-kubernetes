package app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import app.entity.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}