package cl.duoc.msmovimientos.exception;

// Los datos rompen una regla del inventario (responde 409)
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
