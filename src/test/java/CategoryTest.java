import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("work");
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testConstructor() {
        assertEquals("work", category.getName());
        assertTrue(category.getUserIds().isEmpty());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testSetName() {
        category.setName("personal");
        assertEquals("personal", category.getName());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddUserId() {
        assertTrue(category.addUserId(1));
        assertEquals(1, category.getUserIds().size());
        assertTrue(category.getUserIds().contains(1));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddUserIdDuplicate() {
        assertTrue(category.addUserId(1));
        assertFalse(category.addUserId(1));
        assertEquals(1, category.getUserIds().size());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testAddSeveralUserIds() {
        assertTrue(category.addUserId(1));
        assertTrue(category.addUserId(2));
        assertTrue(category.addUserId(3));
        assertEquals(3, category.getUserIds().size());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveUserId() {
        category.addUserId(1);
        assertTrue(category.removeUserId(1));
        assertTrue(category.getUserIds().isEmpty());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveUserIdNotPresent() {
        assertFalse(category.removeUserId(1));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveUserIdRemovesValueNotIndex() {
        category.addUserId(10);
        category.addUserId(20);

        // 20 is a value, not a valid index (size is 2)
        assertTrue(category.removeUserId(20));
        assertEquals(1, category.getUserIds().size());
        assertTrue(category.getUserIds().contains(10));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testRemoveUserIdKeepsOthers() {
        category.addUserId(1);
        category.addUserId(2);
        category.removeUserId(1);
        assertFalse(category.getUserIds().contains(1));
        assertTrue(category.getUserIds().contains(2));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testSetUserIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(7);
        ids.add(8);

        category.setUserIds(ids);

        assertEquals(ids, category.getUserIds());
        assertFalse(category.addUserId(7));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testToStringEmpty() {
        assertEquals("Category [name=work, userIds=[]]", category.toString());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testToStringWithUsers() {
        category.addUserId(1);
        category.addUserId(2);
        assertEquals("Category [name=work, userIds=[1, 2]]", category.toString());
    }

}