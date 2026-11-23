package cl.duoc.msmovimientos.service;

import cl.duoc.msmovimientos.client.ProductoClient;
import cl.duoc.msmovimientos.dto.MovimientoRequest;
import cl.duoc.msmovimientos.dto.ProductoDto;
import cl.duoc.msmovimientos.exception.RecursoNoEncontradoException;
import cl.duoc.msmovimientos.exception.ReglaNegocioException;
import cl.duoc.msmovimientos.model.Movimiento;
import cl.duoc.msmovimientos.model.TipoMovimiento;
import cl.duoc.msmovimientos.repository.MovimientoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    private final MovimientoRepository repository;
    private final ProductoClient productoClient;

    public MovimientoService(MovimientoRepository repository, ProductoClient productoClient) {
        this.repository = repository;
        this.productoClient = productoClient;
    }

    public List<Movimiento> listar() {
        return repository.findAllByOrderByFechaDesc();
    }

    public List<Movimiento> listarPorProducto(Long productoId) {
        return repository.findByProductoIdOrderByFechaDesc(productoId);
    }

    public Movimiento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el movimiento con id " + id));
    }

    // 1) consulta el producto, 2) valida el stock, 3) actualiza el stock en ms-productos, 4) guarda el movimiento
    public Movimiento registrar(MovimientoRequest request) {
        ProductoDto producto = productoClient.obtenerProducto(request.productoId());

        boolean esEntrada = request.tipo() == TipoMovimiento.ENTRADA;
        if (!esEntrada && request.cantidad() > producto.stock()) {
            throw new ReglaNegocioException("Stock insuficiente (disponible: " + producto.stock() + ")");
        }

        // Entrada suma, salida y venta restan
        int cambio = esEntrada ? request.cantidad() : -request.cantidad();
        productoClient.ajustarStock(producto.id(), cambio);

        Movimiento movimiento = new Movimiento(
                producto.id(),
                producto.nombre(),
                request.tipo(),
                request.cantidad(),
                LocalDateTime.now()
        );
        return repository.save(movimiento);
    }
}
