package cl.duoc.msmovimientos.controller;

import cl.duoc.msmovimientos.dto.MovimientoRequest;
import cl.duoc.msmovimientos.model.Movimiento;
import cl.duoc.msmovimientos.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService service;

    public MovimientoController(MovimientoService service) {
        this.service = service;
    }

    // GET /api/movimientos  (historial completo, el más reciente primero)
    @GetMapping
    public List<Movimiento> listar() {
        return service.listar();
    }

    // GET /api/movimientos/3
    @GetMapping("/{id}")
    public Movimiento buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // GET /api/movimientos/producto/5  (historial de un producto)
    @GetMapping("/producto/{productoId}")
    public List<Movimiento> porProducto(@PathVariable Long productoId) {
        return service.listarPorProducto(productoId);
    }

    // POST /api/movimientos   body: {"productoId": 3, "tipo": "VENTA", "cantidad": 2}
    @PostMapping
    public ResponseEntity<Movimiento> registrar(@Valid @RequestBody MovimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }
}
