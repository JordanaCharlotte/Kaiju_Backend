package cl.duoc.msusuarios.service;

import cl.duoc.msusuarios.dto.LoginRequest;
import cl.duoc.msusuarios.dto.RegistroRequest;
import cl.duoc.msusuarios.dto.UsuarioResponse;
import cl.duoc.msusuarios.exception.CredencialesInvalidasException;
import cl.duoc.msusuarios.exception.ReglaNegocioException;
import cl.duoc.msusuarios.model.Rol;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas del login y registro: el repositorio es falso, BCrypt es el real
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioService service;
    private Usuario admin;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(repository, passwordEncoder);
        admin = new Usuario("Administrador Kaiju", "admin@kaiju.cl", passwordEncoder.encode("admin123"), Rol.ADMINISTRADOR);
        admin.setId(1L);
    }

    @Test
    void login_conDatosCorrectos_devuelveElUsuario() {
        when(repository.findByEmail("admin@kaiju.cl")).thenReturn(Optional.of(admin));

        UsuarioResponse respuesta = service.login(new LoginRequest("admin@kaiju.cl", "admin123"));

        assertEquals("Administrador Kaiju", respuesta.nombre());
        assertEquals(Rol.ADMINISTRADOR, respuesta.rol());
    }

    @Test
    void login_ignoraMayusculasYEspaciosEnElCorreo() {
        when(repository.findByEmail("admin@kaiju.cl")).thenReturn(Optional.of(admin));

        UsuarioResponse respuesta = service.login(new LoginRequest("  ADMIN@Kaiju.cl ", "admin123"));

        assertEquals(1L, respuesta.id());
    }

    @Test
    void login_conContrasenaIncorrecta_lanzaError() {
        when(repository.findByEmail("admin@kaiju.cl")).thenReturn(Optional.of(admin));

        CredencialesInvalidasException error = assertThrows(CredencialesInvalidasException.class,
                () -> service.login(new LoginRequest("admin@kaiju.cl", "clave-mala")));

        assertEquals("Correo o contraseña incorrectos", error.getMessage());
    }

    @Test
    void login_conCorreoInexistente_lanzaElMismoError() {
        when(repository.findByEmail("nadie@kaiju.cl")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> service.login(new LoginRequest("nadie@kaiju.cl", "admin123")));
    }

    @Test
    void registrar_guardaLaContrasenaCifrada() {
        when(repository.existsByEmail("nuevo@kaiju.cl")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        service.registrar(new RegistroRequest("Nuevo", "nuevo@kaiju.cl", "secreta1", Rol.VENDEDOR));

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(guardado.capture());
        assertNotEquals("secreta1", guardado.getValue().getPasswordHash());
        assertTrue(passwordEncoder.matches("secreta1", guardado.getValue().getPasswordHash()));
    }

    @Test
    void registrar_conCorreoRepetido_lanzaErrorYNoGuarda() {
        when(repository.existsByEmail("admin@kaiju.cl")).thenReturn(true);

        assertThrows(ReglaNegocioException.class,
                () -> service.registrar(new RegistroRequest("Otro", "admin@kaiju.cl", "secreta1", Rol.VENDEDOR)));
        verify(repository, never()).save(any());
    }
}
