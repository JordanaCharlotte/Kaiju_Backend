package cl.duoc.msproductos.dto;

import jakarta.validation.constraints.NotNull;

// Cuerpo del PATCH /api/productos/{id}/stock
// cantidad positiva suma stock (entrada), negativa lo resta (salida o venta)
public record AjusteStockRequest(
        @NotNull(message = "La cantidad es obligatoria")
        Integer cantidad
) {
}
