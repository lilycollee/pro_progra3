package reservationSystem.protocol;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Object valor;
    private final String error;

    private Response(Object valor, String error) {
        this.valor = valor;
        this.error = error;
    }

    public static Response ok(Object valor) {
        return new Response(valor, null);
    }

    public static Response error(String mensaje) {
        return new Response(null, mensaje);
    }

    public boolean esError() {
        return error != null;
    }

    public Object getValor() {
        return valor;
    }

    public String getError() {
        return error;
    }
}