package presentation.funcionarios;

import logic.Service;
import logic.entities.Funcionario;

import java.security.Provider;

public class Controller {
    FuncionarioView view;
    Model model;

    public Controller(FuncionarioView view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
    }

    /*No puedo avanzar hasta que decidamos si hacemos los service
    por separado o uno solo*/
}
