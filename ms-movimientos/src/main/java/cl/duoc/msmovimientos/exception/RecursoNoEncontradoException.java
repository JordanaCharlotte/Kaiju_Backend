package cl.duoc.msmovimientos.exception;

// No existe lo que se buscó (responde 404)
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
