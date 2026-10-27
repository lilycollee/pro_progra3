package reservationSystem;

import reservationSystem.logic.IService;
import reservationSystem.logic.ServiceProxy;
import reservationSystem.presentation.login.Controller;
import reservationSystem.presentation.login.LoginView;
import reservationSystem.presentation.login.Model;

import javax.swing.JOptionPane;

public class Application {
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }
        javax.swing.SwingUtilities.invokeLater(() -> {
            IService service;
            try {
                service = ServiceProxy.instance();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error de conexión", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
                return;
            }
            LoginView loginView = new LoginView();
            Model model = new Model();
            Controller controller = new Controller(loginView, model, service);
            loginView.setVisible(true);
        });
    }
}