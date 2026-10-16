package reservationSystem.presentation.login;

import reservationSystem.presentation.AbstractModel;
import java.beans.PropertyChangeListener;

public class Model extends AbstractModel {
    private String mensajeError;

    public static final String MENSAJE_ERROR = "mensajeError";

    public Model() {
        mensajeError = "";
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(MENSAJE_ERROR);
    }

    public String getMensajeError() {
        return mensajeError;
    }
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
        firePropertyChange(MENSAJE_ERROR);
    }
}