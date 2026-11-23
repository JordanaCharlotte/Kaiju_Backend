package cl.duoc.msmovimientos.dto;

// Lo que enviamos a ms-productos para sumar o restar stock
public record AjusteStockRequest(Integer cantidad) {
}
