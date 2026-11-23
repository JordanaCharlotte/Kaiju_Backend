package cl.duoc.msproductos.controller;

import cl.duoc.msproductos.dto.AjusteStockRequest;
import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Endpoints REST del CRUD de productos
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // GET /api/productos
    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    // GET /api/productos/5
    @GetMapping("/{id}")
    public Producto buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // GET /api/productos/stock-bajo
    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo() {
        return service.listarStockBajo();
    }

    // POST /api/productos  -> 201 Created
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(producto));
    }

    // PUT /api/productos/5
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return service.actualizar(id, producto);
    }

    // DELETE /api/productos/5  -> 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/productos/5/stock   body: {"cantidad": -2}
    @PatchMapping("/{id}/stock")
    public Producto ajustarStock(@PathVariable Long id, @Valid @RequestBody AjusteStockRequest request) {
        return service.ajustarStock(id, request.cantidad());
    }
}
