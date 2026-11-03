import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class PrintzipalaTest {

    
    private String run(String entrada) {
        PrintStream original = System.out;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        System.setOut(new PrintStream(os));
        System.setIn(new ByteArrayInputStream(entrada.getBytes()));
        try {
            new Printzipala();   
        } finally {
            System.setOut(original);
        }
        return os.toString();
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

    
    
}
