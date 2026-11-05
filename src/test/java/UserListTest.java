import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserListTest {

    private UserList userList;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userList = new UserList();
        user1 = new User(1, "ibai", "1234");
        user2 = new User(2, "ahadu", "abcd");
    }

    @Test
    void getUserByIdEmptyList() {
        assertNull(userList.getUserById(1));
    }

    @Test
    void getUserByIdTheFirst() {
        userList.addUser(user1);
        userList.addUser(user2);

        assertSame(user1, userList.getUserById(1));
    }

    @Test
    void getUserByIdNotTheFirst() {
        userList.addUser(user1);
        userList.addUser(user2);

        assertSame(user2, userList.getUserById(2));
    }

    @Test
    void getUserByIdInexistent() {
        userList.addUser(user1);
        assertNull(userList.getUserById(99));
    }


    @Test
    void addNewUserTrue() {
        assertTrue(userList.addUser(user1));
        assertEquals(1, userList.getUser().size());
        assertSame(user1, userList.getUser().get(0));
    }

    @Test
    void addRepeatUserFalse() {
        userList.addUser(user1);
        User repetido = new User(1, "otro", "xxxx");

        assertFalse(userList.addUser(repetido));
        assertEquals(1, userList.getUser().size());
    }


    @Test
    void deleteUserExistentTrue() {
        userList.addUser(user1);
        userList.addUser(user2);

        assertTrue(userList.deleteUser(user1));
        assertEquals(1, userList.getUser().size());
        assertNull(userList.getUserById(1));
    }

    @Test
    void deleteUserInexistentFalse() {
        userList.addUser(user1);

        assertFalse(userList.deleteUser(user2));
        assertEquals(1, userList.getUser().size());
    }
}