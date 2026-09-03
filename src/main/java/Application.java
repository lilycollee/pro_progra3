import presentation.MainView;

public class Application {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            String rol = "Funcionarios"; //por mientras se hace el del login que va primero
            MainView mainView = new MainView(rol);
            mainView.setVisible(true);
        });
    }
}
