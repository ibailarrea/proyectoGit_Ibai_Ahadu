

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CategoryListTest {

    private CategoryList list;

    @BeforeEach
    void setUp() {
        list = new CategoryList();
    }

    @Test
    void testConstructorEmpty() {
        assertNotNull(list.getCategories());
        assertTrue(list.getCategories().isEmpty());
    }

    @Test
    void testAddCategory() {
        assertTrue(list.addCategory(new Category("work")));
        assertEquals(1, list.getCategories().size());
    }

    @Test
    void testAddCategoryDuplicate() {
        assertTrue(list.addCategory(new Category("work")));
        assertFalse(list.addCategory(new Category("work")));
        assertEquals(1, list.getCategories().size());
    }

    @Test
    void testAddCategoriesDifferentNames() {
        assertTrue(list.addCategory(new Category("work")));
        assertTrue(list.addCategory(new Category("personal")));
        assertEquals(2, list.getCategories().size());
    }

    @Test
    void testNamesAreCaseSensitive() {
        assertTrue(list.addCategory(new Category("work")));
        assertTrue(list.addCategory(new Category("Work")));
        assertEquals(2, list.getCategories().size());
    }

    @Test
    void testGetCategoryByName() {
        Category work = new Category("work");
        list.addCategory(work);

        assertSame(work, list.getCategoryByName("work"));
    }

    @Test
    void testGetCategoryByNameNotFound() {
        assertNull(list.getCategoryByName("nothing"));
    }

    @Test
    void testDeleteCategory() {
        list.addCategory(new Category("work"));

        assertTrue(list.deleteCategory("work"));
        assertNull(list.getCategoryByName("work"));
        assertTrue(list.getCategories().isEmpty());
    }

    @Test
    void testDeleteCategoryNotFound() {
        assertFalse(list.deleteCategory("nothing"));
    }

    @Test
    void testDeleteCategoryKeepsOthers() {
        list.addCategory(new Category("work"));
        list.addCategory(new Category("personal"));

        list.deleteCategory("work");

        assertNull(list.getCategoryByName("work"));
        assertNotNull(list.getCategoryByName("personal"));
    }

    @Test
    void testDeleteCategoryTwice() {
        list.addCategory(new Category("work"));

        assertTrue(list.deleteCategory("work"));
        assertFalse(list.deleteCategory("work"));
    }

    @Test
    void testAddUserToCategory() {
        list.addCategory(new Category("work"));

        assertTrue(list.addUserToCategory("work", 1));
        assertTrue(list.getCategoryByName("work").getUserIds().contains(1));
    }

    @Test
    void testAddUserToCategoryNotFound() {
        assertFalse(list.addUserToCategory("nothing", 1));
    }

    @Test
    void testAddUserToCategoryDuplicate() {
        list.addCategory(new Category("work"));

        assertTrue(list.addUserToCategory("work", 1));
        assertFalse(list.addUserToCategory("work", 1));
        assertEquals(1, list.getCategoryByName("work").getUserIds().size());
    }

    @Test
    void testSameUserInSeveralCategories() {
        list.addCategory(new Category("work"));
        list.addCategory(new Category("personal"));

        assertTrue(list.addUserToCategory("work", 1));
        assertTrue(list.addUserToCategory("personal", 1));
        assertTrue(list.getCategoryByName("work").getUserIds().contains(1));
        assertTrue(list.getCategoryByName("personal").getUserIds().contains(1));
    }

    @Test
    void testRemoveUserFromCategory() {
        list.addCategory(new Category("work"));
        list.addUserToCategory("work", 1);

        assertTrue(list.removeUserFromCategory("work", 1));
        assertTrue(list.getCategoryByName("work").getUserIds().isEmpty());
    }

    @Test
    void testRemoveUserFromCategoryNotFound() {
        assertFalse(list.removeUserFromCategory("nothing", 1));
    }

    @Test
    void testRemoveUserFromCategoryUserNotIn() {
        list.addCategory(new Category("work"));

        assertFalse(list.removeUserFromCategory("work", 1));
    }

    @Test
    void testRemoveUserOnlyFromThatCategory() {
        list.addCategory(new Category("work"));
        list.addCategory(new Category("personal"));

        list.addUserToCategory("work", 1);
        list.addUserToCategory("personal", 1);

        list.removeUserFromCategory("work", 1);

        assertFalse(list.getCategoryByName("work").getUserIds().contains(1));
        assertTrue(list.getCategoryByName("personal").getUserIds().contains(1));
    }

    @Test
    void testSetCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("work"));

        list.setCategories(categories);

        assertSame(categories, list.getCategories());
        assertNotNull(list.getCategoryByName("work"));
    }

    @Test
    void testSaveAndLoadFile(@TempDir Path dir)
            throws IOException, ClassNotFoundException {

        String file = dir.resolve("categories.dat").toString();

        list.addCategory(new Category("work"));
        list.addCategory(new Category("personal"));
        list.addUserToCategory("work", 1);
        list.addUserToCategory("work", 2);
        list.saveToFile(file);

        CategoryList loaded = new CategoryList();
        loaded.loadFromFile(file);

        assertEquals(2, loaded.getCategories().size());
        assertNotNull(loaded.getCategoryByName("personal"));
        assertEquals(2, loaded.getCategoryByName("work").getUserIds().size());
        assertTrue(loaded.getCategoryByName("work").getUserIds().contains(1));
        assertTrue(loaded.getCategoryByName("work").getUserIds().contains(2));
    }

    @Test
    void testSaveAndLoadEmptyList(@TempDir Path dir)
            throws IOException, ClassNotFoundException {

        String file = dir.resolve("empty.dat").toString();

        list.saveToFile(file);

        CategoryList loaded = new CategoryList();
        loaded.addCategory(new Category("old"));
        loaded.loadFromFile(file);

        assertTrue(loaded.getCategories().isEmpty());
    }

    @Test
    void testLoadFromMissingFile(@TempDir Path dir) {
        String file = dir.resolve("missing.dat").toString();

        assertThrows(
                FileNotFoundException.class,
                () -> list.loadFromFile(file));
    }

    @Test
    void testSaveToInvalidPath(@TempDir Path dir) {
        String file = dir.resolve("no_such_folder")
                .resolve("categories.dat").toString();

        assertThrows(IOException.class, () -> list.saveToFile(file));
    }
}
