package cl.duoc.msproductos.service;

import cl.duoc.msproductos.exception.RecursoNoEncontradoException;
import cl.duoc.msproductos.exception.ReglaNegocioException;
import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Lógica de negocio de los productos. El controlador solo recibe y responde.
@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }

    public List<Producto> listarStockBajo() {
        return repository.buscarStockBajo();
    }

    public Producto crear(Producto producto) {
        producto.setId(null); // el id lo genera la base de datos
        producto.setCodigoBarra(limpiarCodigo(producto.getCodigoBarra()));
        validarCodigoNoRepetido(producto.getCodigoBarra(), null);
        return repository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto existente = buscarPorId(id);
        String codigo = limpiarCodigo(datos.getCodigoBarra());
        validarCodigoNoRepetido(codigo, id);

        existente.setNombre(datos.getNombre());
        existente.setCategoria(datos.getCategoria());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setStockMinimo(datos.getStockMinimo());
        existente.setCodigoBarra(codigo);
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        Producto existente = buscarPorId(id);
        repository.delete(existente);
    }

    // Suma (entrada) o resta (salida/venta) stock. Lo usa el microservicio de movimientos.
    public Producto ajustarStock(Long id, int cantidad) {
        Producto producto = buscarPorId(id);
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new ReglaNegocioException("Stock insuficiente (disponible: " + producto.getStock() + ")");
        }
        producto.setStock(nuevoStock);
        return repository.save(producto);
    }

    // Un código vacío se guarda como null para que la restricción "unique" no choque
    private String limpiarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        return codigo.trim();
    }

    private void validarCodigoNoRepetido(String codigo, Long idActual) {
        if (codigo == null) {
            return;
        }
        Optional<Producto> otro = repository.findByCodigoBarra(codigo);
        if (otro.isPresent() && !otro.get().getId().equals(idActual)) {
            throw new ReglaNegocioException("El código " + codigo + " ya pertenece a otro producto");
        }
    }
}
