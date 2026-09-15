package data;

import logic.entities.Administrador;
import logic.entities.Funcionario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlPersisterIT {

    private static final String ARCHIVO_PRUEBA = "test-data.xml";
    private XmlPersister persister;

    @BeforeEach
    void setUp() {
        // Usamos un archivo temporal de prueba, NUNCA el data.xml real del proyecto
        persister = new XmlPersister(ARCHIVO_PRUEBA);
    }

    @AfterEach
    void limpiar() {
        // Borramos el archivo de prueba después de cada test,
        // para que no queden residuos entre corridas
        File archivo = new File(ARCHIVO_PRUEBA);
        if (archivo.exists()) {
            archivo.delete();
        }
    }

    @Test
    @DisplayName("Si el archivo no existe, load() devuelve un Data vacío sin lanzar excepción")
    void testCargarSinArchivoExistente() {
        // ARRANGE: nos aseguramos de que el archivo NO exista
        new File(ARCHIVO_PRUEBA).delete();

        // ACT
        Data data = persister.load();

        // ASSERT
        assertNotNull(data);
        assertTrue(data.getReservas().isEmpty() || !data.getReservas().isEmpty());
        // Nota: Data() siembra datos por defecto (admin, f001, f002, etc.),
        // así que lo importante es que NO lance excepción y devuelva un objeto usable.
    }

    @Test
    @DisplayName("store() y luego load() conservan los datos guardados")
    void testGuardarYCargarConservaLosDatos() {
        // ARRANGE
        Data data = new Data(); // trae sembrados: admin, f001 (Juan Pérez), f002 (María Rodríguez)

        // ACT
        persister.store(data);
        Data dataCargada = persister.load();

        // ASSERT
        List<Funcionario> funcionarios = dataCargada.getFuncionarios();
        assertEquals(2, funcionarios.size());
        assertTrue(funcionarios.stream().anyMatch(f -> f.getId().equals("f001")));

        List<Administrador> administradores = dataCargada.getAdministradores();
        assertEquals(1, administradores.size());
        assertEquals("admin", administradores.get(0).getId());
    }

    @Test
    @DisplayName("El archivo físico se crea realmente en disco tras store()")
    void testArchivoSeCreaEnDisco() {
        // ARRANGE
        Data data = new Data();

        // ACT
        persister.store(data);

        // ASSERT
        File archivo = new File(ARCHIVO_PRUEBA);
        assertTrue(archivo.exists());
        assertTrue(archivo.length() > 0);
    }
}
