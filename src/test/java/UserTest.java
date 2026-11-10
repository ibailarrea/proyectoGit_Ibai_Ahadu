
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "ibai", "1234");
    }

    @Test
    void constructorSetsAllFields() {
        assertEquals(1, user.getId());
        assertEquals("ibai", user.getUsername());
        assertEquals("1234", user.getPassword());
    }

    @Test
    void setIdChangesId() {
        user.setId(5);
        assertEquals(5, user.getId());
    }

    @Test
    void setUsernameChangesUsername() {
        user.setUsername("nuevo");
        assertEquals("nuevo", user.getUsername());
    }

    @Test
    void setPasswordChangesPassword() {
        user.setPassword("abcd");
        assertEquals("abcd", user.getPassword());
    }

    @Test
    void toStringShowsAllFields() {
        assertEquals("User [id=1, username=ibai, password=1234]", user.toString());
    }
}