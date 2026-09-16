package reservationSystem.presentation.login;

import javax.swing.*;

public class CambiarClaveView extends JDialog {
    private JPanel panel1;
    private JTextField idField;
    private JPasswordField claveActualField;
    private JPasswordField claveNuevaField;
    private JPasswordField claveConfirmarField;
    private JButton cambiarButton;
    private JButton cancelarButton;
    private Controller controller;

    public CambiarClaveView(JFrame parent, String idPrellenado) {
        super(parent, "Cambiar contraseña", true);
        this.add(panel1);
        this.pack();
        this.setLocationRelativeTo(parent);
        idField.setText(idPrellenado);

        cambiarButton.addActionListener(e -> {
            String id = idField.getText();
            String claveActual = new String(claveActualField.getPassword());
            String claveNueva = new String(claveNuevaField.getPassword());
            String claveConfirmar = new String(claveConfirmarField.getPassword());
            controller.cambiarClave(this, id, claveActual, claveNueva, claveConfirmar);
        });
        cancelarButton.addActionListener(e-> {
            dispose();
        });
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}