import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class PrintzipalaTest {

    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    public void guardarEstandar() {
        originalOut = System.out;
        originalIn = System.in;
    }

    @AfterEach
    public void restaurarEstandar() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private String run(String entrada) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        System.setOut(new PrintStream(os));
        System.setIn(new ByteArrayInputStream(entrada.getBytes()));
        try {
            new Printzipala();
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
    public void testMain() {
        System.setIn(new ByteArrayInputStream("0\n".getBytes()));
        Printzipala.main(null);
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testExit() {
        String out = run("0\n");
        assertTrue(out.contains("Choose one option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testInvalidOption() {
        String out = run("7\n0\n");
        assertTrue(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testEntradaNoNumerica() {
        assertThrows(InputMismatchException.class, () -> run("abc\n"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testListLoginID() {
        String out = assertDoesNotThrow(() -> run("1\n0\n"));
        assertEquals(2, contar(out, "Choose one option"));
        assertFalse(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testQueryByID() {
        String out = run("2\n5\n0\n");
        assertTrue(out.contains("What ID do you want to search"));
        assertEquals(2, contar(out, "Choose one option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    public void testQueryByIDNoNumerico() {
        assertThrows(InputMismatchException.class, () -> run("2\nabc\n0\n"));
    }

   

    

}