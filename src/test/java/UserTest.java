import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {
    private User user;
 
    @BeforeEach
    void setUp() {
        user = new User(1, "ibai", "1234");
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
}
