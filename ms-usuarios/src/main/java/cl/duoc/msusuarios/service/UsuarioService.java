package cl.duoc.msusuarios.service;

import cl.duoc.msusuarios.dto.LoginRequest;
import cl.duoc.msusuarios.dto.RegistroRequest;
import cl.duoc.msusuarios.dto.UsuarioResponse;
import cl.duoc.msusuarios.exception.CredencialesInvalidasException;
import cl.duoc.msusuarios.exception.ReglaNegocioException;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    // Compara la contraseña escrita con el hash guardado.
    // Se usa el mismo mensaje para correo y contraseña, así no se revela qué correos existen.
    public UsuarioResponse login(LoginRequest request) {
        Usuario usuario = repository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }
        return UsuarioResponse.desde(usuario);
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        String email = request.email().trim().toLowerCase();
        if (repository.existsByEmail(email)) {
            throw new ReglaNegocioException("Ya existe un usuario con el correo " + email);
        }
        Usuario nuevo = new Usuario(
                request.nombre().trim(),
                email,
                passwordEncoder.encode(request.password()),
                request.rol()
        );
        return UsuarioResponse.desde(repository.save(nuevo));
    }
}
