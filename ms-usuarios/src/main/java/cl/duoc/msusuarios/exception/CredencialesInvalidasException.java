package cl.duoc.msusuarios.exception;

// Correo o contraseña incorrectos (responde 401)
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
