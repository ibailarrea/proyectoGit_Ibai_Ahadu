

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
    private static final String DELETED_USERS_FILE = "deleted-users.dat";

    private static final String CATEGORY_CREATED =
            "Category created successfully.";
    private static final String CATEGORY_EXISTS =
            "This category already exists.";
    private static final String CATEGORY_DELETED =
            "Category deleted successfully.";
    private static final String CATEGORY_NOT_FOUND =
            "This category doesn't exist.";
    private static final String NO_CATEGORIES =
            "There are no categories.";
    private static final String USER_ADDED =
            "User added to category successfully.";
    private static final String ADD_FAILED =
            "Category doesn't exist or user is already in it.";
    private static final String USER_REMOVED =
            "User removed from category successfully.";
    private static final String REMOVE_FAILED =
            "Category doesn't exist or user isn't in it.";

    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void guardarEstandar() throws IOException {
        originalOut = System.out;
        originalIn = System.in;

        Files.deleteIfExists(Paths.get(VAULT_FILE));
        Files.deleteIfExists(Paths.get(DELETED_USERS_FILE));
    }

    @AfterEach
    void restaurarEstandar() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);

        Files.deleteIfExists(Paths.get(VAULT_FILE));
        Files.deleteIfExists(Paths.get(DELETED_USERS_FILE));
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
        System.setIn(new ByteArrayInputStream("e\n".getBytes()));

        assertDoesNotThrow(() -> Printzipala.main(null));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testExit() {
        String out = run("e\n");

        assertTrue(out.contains(MENU));
        assertEquals(1, contar(out, MENU));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testInvalidOption() {
        String out = run("10\ne\n");

        assertTrue(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testEntradaNoNumerica() {
        String out = run("abc\ne\n");

        assertTrue(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testListLoginID() {
        String out = assertDoesNotThrow(() -> run("1\ne\n"));

        assertEquals(2, contar(out, MENU));
        assertFalse(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testListLoginIDConUsuarios() {
        String out = run("3\n1\nuser\npass\n3\n2\nana\nabc\n1\ne\n");

        assertTrue(out.contains(
                "User [id=1, username=user, password=pass]"));
        assertTrue(out.contains(
                "User [id=2, username=ana, password=abc]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testQueryByID() {
        String out = run("2\n5\ne\n");

        assertTrue(out.contains("What ID do you want to search"));
        assertEquals(2, contar(out, MENU));
        assertTrue(out.contains(NOT_FOUND));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testQueryByIDEncontrado() {
        String out = run("3\n1\nuser\npass\n2\n1\ne\n");

        assertTrue(out.contains(
                "The user is: User [id=1, username=user, password=pass]"));
        assertFalse(out.contains(NOT_FOUND));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testQueryByIDNoNumerico() {
        assertThrows(
                InputMismatchException.class,
                () -> run("2\nabc\ne\n"));
    }

    @Test
    void testAddDuplicateLogin() {
        run("3\n1\nibai\n1234\ne\n");

        String output = run("3\n1\notro\n5678\ne\n");

        assertTrue(output.contains("This id already exists"));
    }

    @Test
    void testDeleteLoginNotFound() {
        String output = run("4\n99\ne\n");

        assertTrue(output.contains(NOT_FOUND));
    }

    @Test
    void testQueryByIDNotFound() {
        String output = run("2\n99\ne\n");

        assertTrue(output.contains(NOT_FOUND));
    }

    @Test
    void testQueryByIDFound() {
        run("3\n10\nana\n1234\ne\n");

        String output = run("2\n10\ne\n");

        assertTrue(output.contains("The user is:"));
        assertTrue(output.contains("username=ana"));
    }

    @Test
    void testDeleteLoginFound() {
        run("3\n10\nana\n1234\ne\n");

        String output = run("4\n10\ne\n");

        assertTrue(output.contains("User deleted successfully."));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginPideDatos() {
        String out = run("3\n1\nuser\n\npass\n\ne\n");

        assertTrue(out.contains("Id: "));
        assertTrue(out.contains("Username: "));
        assertTrue(out.contains("Password: "));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginMuestraDatosCreados() {
        String out = run("3\n1\nuser\n\npass\n\ne\n");

        assertTrue(out.contains("Sortuta :1"));
        assertTrue(out.contains("user"));
        assertTrue(out.contains("pass"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginSinLineasEnBlanco() {
        String out = run("3\n1\nuser\npass\ne\n");

        assertTrue(out.contains("Sortuta :1"));
        assertEquals(2, contar(out, MENU));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginConEspacios() {
        String out = run("3\n1\nJohn Smith\nmy secret\ne\n");

        assertTrue(out.contains("John Smith"));
        assertTrue(out.contains("my secret"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginIdRepetido() {
        String out = run("3\n1\nuser\npass\n3\n1\notro\nxyz\ne\n");

        assertEquals(1, contar(out, "Sortuta"));
        assertTrue(out.contains("This id already exists"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginIdNoNumerico() {
        assertThrows(
                InputMismatchException.class,
                () -> run("3\nabc\ne\n"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLogin() {
        String out = run("3\n1\nuser\npass\n4\n1\ne\n");

        assertTrue(out.contains("What ID do you want to delete"));
        assertTrue(out.contains(
                "Deleting: User [id=1, username=user, password=pass]"));
        assertFalse(out.contains(NOT_FOUND));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLoginElimina() {
        String out = run(
                "3\n1\nuser\npass\n4\n1\n3\n1\nnuevo\nclave\ne\n");

        assertEquals(2, contar(out, "Sortuta"));
        assertFalse(out.contains("This id already exists"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLoginNoExiste() {
        String out = run("4\n9\ne\n");

        assertTrue(out.contains(NOT_FOUND));
        assertFalse(out.contains("Deleting"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLoginNoNumerico() {
        assertThrows(
                InputMismatchException.class,
                () -> run("4\nabc\ne\n"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testExitWithE() {
        String out = run("e\n");

        assertEquals(1, contar(out, MENU));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testExitWithUpperCaseE() {
        String out = run("E\n");

        assertEquals(1, contar(out, MENU));
        assertFalse(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testMenuShowsCategoryOptions() {
        String out = run("e\n");

        assertTrue(out.contains("5-View Categories and Login IDs"));
        assertTrue(out.contains("6-Add ID to Category"));
        assertTrue(out.contains("7-Remove ID from Category"));
        assertTrue(out.contains("8-Add Category"));
        assertTrue(out.contains("9-Delete Category"));
        assertTrue(out.contains("e-Exit"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testInvalidOptionLetter() {
        String out = run("x\ne\n");

        assertTrue(out.contains("Is not a possible option"));
        assertEquals(2, contar(out, MENU));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testInvalidOptionZero() {
        String out = run("0\ne\n");

        assertTrue(out.contains("Is not a possible option"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testViewCategoriesEmpty() {
        String out = run("5\ne\n");

        assertTrue(out.contains(NO_CATEGORIES));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddCategory() {
        String out = run("8\nwork\ne\n");

        assertTrue(out.contains("Category name: "));
        assertTrue(out.contains(CATEGORY_CREATED));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddCategoryIgnoresBlankLines() {
        String out = run("8\n\n\nwork\ne\n");

        assertTrue(out.contains(CATEGORY_CREATED));
        assertEquals(2, contar(out, MENU));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddDuplicateCategory() {
        String out = run("8\nwork\n8\nwork\ne\n");

        assertEquals(1, contar(out, CATEGORY_CREATED));
        assertTrue(out.contains(CATEGORY_EXISTS));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddSeveralCategories() {
        String out = run("8\nwork\n8\npersonal\n5\ne\n");

        assertEquals(2, contar(out, CATEGORY_CREATED));
        assertTrue(out.contains("Category [name=work, userIds=[]]"));
        assertTrue(out.contains("Category [name=personal, userIds=[]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testViewCategoriesShowsCreated() {
        String out = run("8\nwork\n5\ne\n");

        assertTrue(out.contains("Category [name=work, userIds=[]]"));
        assertFalse(out.contains(NO_CATEGORIES));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteCategory() {
        String out = run("8\nwork\n9\nwork\n5\ne\n");

        assertTrue(out.contains(CATEGORY_DELETED));
        assertTrue(out.contains(NO_CATEGORIES));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteCategoryNotFound() {
        String out = run("9\nnothing\ne\n");

        assertTrue(out.contains(CATEGORY_NOT_FOUND));
        assertFalse(out.contains(CATEGORY_DELETED));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteCategoryKeepsOthers() {
        String out = run("8\nwork\n8\npersonal\n9\nwork\n5\ne\n");

        assertFalse(out.contains("Category [name=work"));
        assertTrue(out.contains("Category [name=personal, userIds=[]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRecreateDeletedCategory() {
        String out = run("8\nwork\n9\nwork\n8\nwork\ne\n");

        assertEquals(2, contar(out, CATEGORY_CREATED));
        assertFalse(out.contains(CATEGORY_EXISTS));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddIDToCategory() {
        String out = run("3\n1\nuser\npass\n8\nwork\n6\nwork\n1\n5\ne\n");

        assertTrue(out.contains("User ID: "));
        assertTrue(out.contains(USER_ADDED));
        assertTrue(out.contains("Category [name=work, userIds=[1]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddIDToCategoryUserNotFound() {
        String out = run("8\nwork\n6\nwork\n99\n5\ne\n");

        assertTrue(out.contains(NOT_FOUND));
        assertFalse(out.contains(USER_ADDED));
        assertTrue(out.contains("Category [name=work, userIds=[]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddIDToCategoryCategoryNotFound() {
        String out = run("3\n1\nuser\npass\n6\nnothing\n1\ne\n");

        assertTrue(out.contains(ADD_FAILED));
        assertFalse(out.contains(USER_ADDED));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddIDToCategoryTwice() {
        String out = run(
                "3\n1\nuser\npass\n8\nwork\n6\nwork\n1\n6\nwork\n1\n5\ne\n");

        assertEquals(1, contar(out, USER_ADDED));
        assertTrue(out.contains(ADD_FAILED));
        assertTrue(out.contains("Category [name=work, userIds=[1]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddIDToCategoryNonNumeric() {
        assertThrows(
                InputMismatchException.class,
                () -> run("8\nwork\n6\nwork\nabc\ne\n"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testSameUserInSeveralCategories() {
        String out = run(
                "3\n1\nuser\npass\n8\nwork\n8\npersonal\n"
                + "6\nwork\n1\n6\npersonal\n1\n5\ne\n");

        assertEquals(2, contar(out, USER_ADDED));
        assertTrue(out.contains("Category [name=work, userIds=[1]]"));
        assertTrue(out.contains("Category [name=personal, userIds=[1]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testSeveralUsersInSameCategory() {
        String out = run(
                "3\n1\nuser\npass\n3\n2\nana\nabc\n8\nwork\n"
                + "6\nwork\n1\n6\nwork\n2\n5\ne\n");

        assertTrue(out.contains("Category [name=work, userIds=[1, 2]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveIDFromCategory() {
        String out = run(
                "3\n1\nuser\npass\n8\nwork\n6\nwork\n1\n7\nwork\n1\n5\ne\n");

        assertTrue(out.contains(USER_REMOVED));
        assertTrue(out.contains("Category [name=work, userIds=[]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveIDFromCategoryUserNotIn() {
        String out = run("3\n1\nuser\npass\n8\nwork\n7\nwork\n1\ne\n");

        assertTrue(out.contains(REMOVE_FAILED));
        assertFalse(out.contains(USER_REMOVED));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveIDFromCategoryCategoryNotFound() {
        String out = run("7\nnothing\n1\ne\n");

        assertTrue(out.contains(REMOVE_FAILED));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveIDFromCategoryKeepsOtherCategories() {
        String out = run(
                "3\n1\nuser\npass\n8\nwork\n8\npersonal\n"
                + "6\nwork\n1\n6\npersonal\n1\n7\nwork\n1\n5\ne\n");

        assertTrue(out.contains("Category [name=work, userIds=[]]"));
        assertTrue(out.contains("Category [name=personal, userIds=[1]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveIDFromCategoryNonNumeric() {
        assertThrows(
                InputMismatchException.class,
                () -> run("8\nwork\n7\nwork\nabc\ne\n"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLoginRemovesItFromCategories() {
        String out = run(
                "3\n1\nuser\npass\n8\nwork\n6\nwork\n1\n4\n1\n5\ne\n");

        assertTrue(out.contains("User deleted successfully."));
        assertFalse(out.contains("userIds=[1]"));
        assertTrue(out.contains("Category [name=work, userIds=[]]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testFirstRunWithoutVaultFile() {
        String out = run("e\n");

        assertTrue(out.contains(
                "No vault file found. Starting with an empty list."));
        assertFalse(out.contains("Users loaded successfully."));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddLoginSavesUsers() {
        String out = run("3\n1\nuser\npass\ne\n");

        assertTrue(out.contains("Users saved successfully."));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDuplicateLoginDoesNotSave() {
        run("3\n1\nuser\npass\ne\n");

        String out = run("3\n1\nother\nxyz\ne\n");

        assertTrue(out.contains("This id already exists"));
        assertFalse(out.contains("Users saved successfully."));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testUsersPersistBetweenRuns() {
        run("3\n1\nana\nabc\ne\n");

        String out = run("1\ne\n");

        assertTrue(out.contains("Users loaded successfully."));
        assertTrue(out.contains(
                "User [id=1, username=ana, password=abc]"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeletedLoginStaysDeletedBetweenRuns() {
        run("3\n1\nana\nabc\n4\n1\ne\n");

        String out = run("1\ne\n");

        assertFalse(out.contains("username=ana"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDeleteLoginSavesUsers() {
        String out = run("3\n1\nana\nabc\n4\n1\ne\n");

        assertEquals(2, contar(out, "Users saved successfully."));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testCorruptVaultFileShowsError() throws IOException {
        Files.write(
                Paths.get(VAULT_FILE),
                "not a vault".getBytes());

        String out = run("e\n");

        assertTrue(out.contains("Error loading users: "));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testMenuShowsSyncOption() {
        String out = run("e\n");

        assertTrue(out.contains("s-Synchronize with server"));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testSyncWithoutServer() throws IOException {

        Files.write(
                Paths.get("config.properties"),
                "server.host=localhost\nserver.port=0\n".getBytes());

        String out = run("s\ne\n");

        assertTrue(out.contains("Synchronization failed:"));
    }
}
