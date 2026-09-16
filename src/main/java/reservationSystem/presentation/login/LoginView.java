package reservationSystem.presentation.login;

import javax.swing.*;

public class LoginView extends JFrame {
    private JPanel panel;
    private JTextField idField;
    private JPasswordField claveField;
    private JButton ingresarButton;
    private JLabel mensajeErrorLabel;
    private JButton cambiarClaveButton;

    private Controller controller;
    private Model model;

    public LoginView() {
        super("Ingreso al Sistema");
        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);

        ingresarButton.addActionListener(e -> {
            String id = idField.getText();
            String clave = new String(claveField.getPassword());
            controller.ingresar(id, clave);
        });

        cambiarClaveButton.addActionListener(e -> controller.abrirCambioClave(idField.getText()));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(Model.MENSAJE_ERROR)) {
                mensajeErrorLabel.setText(model.getMensajeError());
            }
        });
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de ingreso", JOptionPane.ERROR_MESSAGE);
    }
}