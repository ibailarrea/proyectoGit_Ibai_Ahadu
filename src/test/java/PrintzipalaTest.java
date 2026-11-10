import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class PrintzipalaTest {

    private static final String MENU = "Choose one option";
    private static final String NOT_FOUND = "This id doesn't exist";
    private static final String VAULT_FILE = "vault.dat";

    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void guardarEstandar() throws IOException {
        originalOut = System.out;
        originalIn = System.in;
        // Every test starts without a saved vault, so tests do not affect each other
        Files.deleteIfExists(Paths.get(VAULT_FILE));
    }

    @AfterEach
    void restaurarEstandar() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);
        Files.deleteIfExists(Paths.get(VAULT_FILE));
    }

    private String run(String entrada) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(os)) {
            System.setOut(ps);
            System.setIn(new ByteArrayInputStream(entrada.getBytes()));
            new Printzipala().hasieratu();
        } finally {
            System.setOut(originalOut);
        }
        return os.toString();
    }

    private int contar(String texto, String patron) {
        int veces = 0;
        int pos = 0;
        while ((pos = texto.indexOf(patron, pos)) != -1) {
            veces++;
            pos += patron.length();
        }
        return veces;
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testMain() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setIn(new ByteArrayInputStream("0\n".getBytes()));
        assertDoesNotThrow(() -> Printzipala.main(null));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testExit() {
        String out = run("0\n");
        assertTrue(out.contains(MENU));
        assertEquals(1, contar(out, MENU));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testInvalidOption() {
        String out = run("7\n0\n");
        assertTrue(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testEntradaNoNumerica() {
        assertThrows(InputMismatchException.class, () -> run("abc\n"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testListLoginID() {
        String out = assertDoesNotThrow(() -> run("1\n0\n"));
        assertEquals(2, contar(out, MENU));
        assertFalse(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testListLoginIDConUsuarios() {
        String out = run("3\n1\nuser\npass\n3\n2\nana\nabc\n1\n0\n");
        assertTrue(out.contains("User [id=1, username=user, password=pass]"));
        assertTrue(out.contains("User [id=2, username=ana, password=abc]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testQueryByID() {
        String out = run("2\n5\n0\n");
        assertTrue(out.contains("What ID do you want to search"));
        assertEquals(2, contar(out, MENU));
        assertTrue(out.contains(NOT_FOUND));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testQueryByIDEncontrado() {
        String out = run("3\n1\nuser\npass\n2\n1\n0\n");
        assertTrue(out.contains("The user is: User [id=1, username=user, password=pass]"));
        assertFalse(out.contains(NOT_FOUND));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testQueryByIDNoNumerico() {
        assertThrows(InputMismatchException.class, () -> run("2\nabc\n0\n"));
    }



    @Test
    void testAddDuplicateLogin() {
        run("3\n1\nibai\n1234\n0\n");

        String output = run("3\n1\notro\n5678\n0\n");

        assertTrue(output.contains("This id already exists"));
    }

    @Test
    void testDeleteLoginNotFound() {
        String output = run("4\n99\n0\n");

        assertTrue(output.contains("This id doesn't exist"));
    }

    @Test
    void testQueryByIDNotFound() {
        String output = run("2\n99\n0\n");

        assertTrue(output.contains("This id doesn't exist"));
    }


    @Test
    void testQueryByIDFound() {
        run("3\n10\nana\n1234\n0\n");

        String output = run("2\n10\n0\n");

        assertTrue(output.contains("The user is:"));
        assertTrue(output.contains("username=ana"));
    }

    @Test
    void testDeleteLoginFound() {
        run("3\n10\nana\n1234\n0\n");

        String output = run("4\n10\n0\n");

        assertTrue(output.contains("User deleted successfully."));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginPideDatos() {
        String out = run("3\n1\nuser\n\npass\n\n0\n");
        assertTrue(out.contains("Id: "));
        assertTrue(out.contains("Username: "));
        assertTrue(out.contains("Password: "));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginMuestraDatosCreados() {
        String out = run("3\n1\nuser\n\npass\n\n0\n");
        assertTrue(out.contains("Sortuta :1"));
        assertTrue(out.contains("user"));
        assertTrue(out.contains("pass"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginSinLineasEnBlanco() {
        String out = run("3\n1\nuser\npass\n0\n");
        assertTrue(out.contains("Sortuta :1"));
        assertEquals(2, contar(out, MENU));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginConEspacios() {
        String out = run("3\n1\nJohn Smith\nmy secret\n0\n");
        assertTrue(out.contains("John Smith"));
        assertTrue(out.contains("my secret"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginIdRepetido() {
        String out = run("3\n1\nuser\npass\n3\n1\notro\nxyz\n0\n");
        assertEquals(1, contar(out, "Sortuta"));
        assertTrue(out.contains("This id already exists"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginIdNoNumerico() {
        assertThrows(InputMismatchException.class, () -> run("3\nabc\n0\n"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLogin() {
        String out = run("3\n1\nuser\npass\n4\n1\n0\n");
        assertTrue(out.contains("What ID do you want to delete"));
        assertTrue(out.contains("Deleting: User [id=1, username=user, password=pass]"));
        assertFalse(out.contains(NOT_FOUND));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLoginElimina() {

        String out = run("3\n1\nuser\npass\n4\n1\n3\n1\nnuevo\nclave\n0\n");
        assertEquals(2, contar(out, "Sortuta"));
        assertFalse(out.contains("This id already exists"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLoginNoExiste() {
        String out = run("4\n9\n0\n");
        assertTrue(out.contains(NOT_FOUND));
        assertFalse(out.contains("Deleting"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLoginNoNumerico() {
        assertThrows(InputMismatchException.class, () -> run("4\nabc\n0\n"));
    }

}