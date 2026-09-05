package logic;

import data.Data;
import data.XmlPersister;

import logic.entities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Service {
    private Data data;

    public Service() {
        data = XmlPersister.instance().load();

    }
    private void guardarCambios(){
        XmlPersister.instance().store(data);
    }
    public Usuario autenticar(String id, String clave) {
        for (Administrador administrador : data.getAdministradores()) {
            if (administrador.getId().equals(id)) {
                if (administrador.getClave().equals(clave)) {
                    return administrador;
                }
            }
        }
        for (Funcionario funcionario : data.getFuncionarios()) {
            if (funcionario.getId().equals(clave)) {
                if (funcionario.getClave().equals(clave)) {
                    return funcionario;
                }
            }
        }
        return null;
    }

    //-------- Funcionarios --------
    public Funcionario guardarFuncionario(Funcionario funcionario) {
        eliminarFuncionario(funcionario.getId());
        data.getFuncionarios().add(funcionario);
        guardarCambios();
        return funcionario;
    }
    public void eliminarFuncionario(String id) {
        data.getFuncionarios().removeIf(funcionario -> funcionario.getId().equals(id));
        guardarCambios();
    }
    public Optional<Funcionario> obtenerFuncionario(String id) {
        return data.getFuncionarios().stream().filter(f -> f.getId().equals(id)).findFirst();
    }
    public List<Funcionario> obtenerFuncionarios() {
        return data.getFuncionarios();
    }

    //-------- Administradores --------
    public Administrador guardarAdministrador(Administrador administrador) {
        eliminarAdministrador(administrador.getId());
        data.getAdministradores().add(administrador);
        guardarCambios();
        return administrador;
    }
    public void eliminarAdministrador(String id) {
        data.getAdministradores().removeIf(a -> a.getId().equals(id));
        guardarCambios();
    }
    public Optional<Administrador> obtenerAdministrador(String id) {
        return data.getAdministradores().stream().filter(a -> a.getId().equals(id)).findFirst();
    }
    public List<Administrador> obtenerAdministradores() {
        return data.getAdministradores();
    }
}
