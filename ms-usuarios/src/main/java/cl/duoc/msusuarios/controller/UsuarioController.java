package cl.duoc.msusuarios.controller;

import cl.duoc.msusuarios.dto.LoginRequest;
import cl.duoc.msusuarios.dto.RegistroRequest;
import cl.duoc.msusuarios.dto.UsuarioResponse;
import cl.duoc.msusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // POST /api/auth/login   body: {"email": "admin@kaiju.cl", "password": "admin123"}
    @PostMapping("/auth/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    // GET /api/usuarios
    @GetMapping("/usuarios")
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    // POST /api/usuarios  -> 201 Created
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }
}
