package reservationSystem.protocol;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String operacion;
    private final Object[] argumentos;

    public Request(String operacion, Object... argumentos) {
        this.operacion = operacion;
        this.argumentos = argumentos;
    }

    public String getOperacion() {
        return operacion;
    }

    public Object[] getArgumentos() {
        return argumentos;
    }
}