package reservationSystem;

import reservationSystem.logic.IService;
import reservationSystem.logic.Service;
import reservationSystem.presentation.login.Controller;
import reservationSystem.presentation.login.LoginView;
import reservationSystem.presentation.login.Model;

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
            IService service = Service.instance();
            LoginView loginView = new LoginView();
            Model model = new Model();
            Controller controller = new Controller(loginView, model, service);
            loginView.setVisible(true);
        });
    }
}