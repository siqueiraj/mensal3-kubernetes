package app.dto;

public class PagamentoDTO {
    private Long pedidoId;
    private Double valor;

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Double getValor() {	
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
