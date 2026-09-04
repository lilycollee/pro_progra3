import logic.Service;
import presentation.login.Controller;
import presentation.login.LoginView;
import presentation.login.Model;

public class Application {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Service service = new Service();
            LoginView loginView = new LoginView();
            Model model = new Model();
            Controller controller = new Controller(loginView, model, service);
            loginView.setVisible(true);
        });
    }
}