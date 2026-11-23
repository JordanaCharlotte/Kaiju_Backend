package cl.duoc.msmovimientos.client;

import cl.duoc.msmovimientos.dto.AjusteStockRequest;
import cl.duoc.msmovimientos.dto.ProductoDto;
import cl.duoc.msmovimientos.exception.RecursoNoEncontradoException;
import cl.duoc.msmovimientos.exception.ReglaNegocioException;
import cl.duoc.msmovimientos.exception.ServicioNoDisponibleException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

// Comunicación entre microservicios: ms-movimientos llama por HTTP a ms-productos
@Component
public class ProductoClient {

    private final RestClient restClient;

    public ProductoClient(RestClient productosRestClient) {
        this.restClient = productosRestClient;
    }

    // GET http://localhost:8081/api/productos/{id}
    public ProductoDto obtenerProducto(Long id) {
        try {
            return restClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .body(ProductoDto.class);
        } catch (HttpClientErrorException e) {
            throw traducirError(e, id);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("El microservicio de productos no está disponible");
        }
    }

    // PATCH http://localhost:8081/api/productos/{id}/stock
    public ProductoDto ajustarStock(Long id, int cantidad) {
        try {
            return restClient.patch()
                    .uri("/{id}/stock", id)
                    .body(new AjusteStockRequest(cantidad))
                    .retrieve()
                    .body(ProductoDto.class);
        } catch (HttpClientErrorException e) {
            throw traducirError(e, id);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("El microservicio de productos no está disponible");
        }
    }

    private RuntimeException traducirError(HttpClientErrorException e, Long id) {
        if (e.getStatusCode().value() == 404) {
            return new RecursoNoEncontradoException("No existe el producto con id " + id);
        }
        return new ReglaNegocioException("ms-productos rechazó la operación: " + e.getResponseBodyAsString());
    }
}
