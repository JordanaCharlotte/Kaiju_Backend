package cl.duoc.msusuarios.config;

import cl.duoc.msusuarios.model.Rol;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// Usuarios ficticios de prueba, uno por cada perfil del caso Kaiju (solo la primera vez)
@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public DatosIniciales(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Usuario("Administrador Kaiju", "admin@kaiju.cl", passwordEncoder.encode("admin123"), Rol.ADMINISTRADOR),
                new Usuario("Vendedor Kaiju", "vendedor@kaiju.cl", passwordEncoder.encode("vendedor123"), Rol.VENDEDOR),
                new Usuario("Encargado Inventario", "inventario@kaiju.cl", passwordEncoder.encode("inventario123"), Rol.ENCARGADO_INVENTARIO)
        ));
    }
}
