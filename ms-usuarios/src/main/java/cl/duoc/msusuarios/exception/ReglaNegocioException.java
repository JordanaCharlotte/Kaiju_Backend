package cl.duoc.msusuarios.exception;

// Los datos rompen una regla, por ejemplo correo repetido (responde 409)
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
