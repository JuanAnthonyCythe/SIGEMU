package pe.edu.unsch.sigemu.domain.rules;

public class LoteNoEncontradoException extends ReglaNegocioException {

    public LoteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
