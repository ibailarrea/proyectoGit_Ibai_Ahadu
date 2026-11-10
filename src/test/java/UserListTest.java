import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserListTest {
    private UserList userList;
    private User ibai;

    @BeforeEach
    void setUp() {
        userList = new UserList();
        ibai = new User(1, "ibai", "1234");
    }

    @Test
    void newListIsEmpty() {
        assertTrue(userList.getUser().isEmpty());
    }

    @Test
    void addUserAddsNewUser() {
        assertTrue(userList.addUser(ibai));
        assertEquals(1, userList.getUser().size());
    }

    @Test
    void addUserRejectsRepeatedId() {
        userList.addUser(ibai);
        assertFalse(userList.addUser(new User(1, "otro", "abcd")));
        assertEquals(1, userList.getUser().size());
    }

    @Test
    void getUserByIdFindsUser() {
        userList.addUser(new User(2, "ana", "abcd"));
        userList.addUser(ibai);
        assertSame(ibai, userList.getUserById(1));
    }

    @Test
    void getUserByIdReturnsNullWhenMissing() {
        userList.addUser(ibai);
        assertNull(userList.getUserById(99));
    }

    @Test
    void deleteUserRemovesExistingUser() {
        userList.addUser(ibai);
        assertTrue(userList.deleteUser(ibai));
        assertTrue(userList.getUser().isEmpty());
    }

    @Test
    void deleteUserRemovesByIdEvenWithAnotherObject() {
        userList.addUser(ibai);
        assertTrue(userList.deleteUser(new User(1, "otro", "abcd")));
        assertNull(userList.getUserById(1));
    }

    @Test
    void deleteUserReturnsFalseWhenMissing() {
        assertFalse(userList.deleteUser(ibai));
    }

    @Test
    void setUserReplacesTheList() {
        ArrayList<User> other = new ArrayList<>();
        other.add(ibai);
        userList.setUser(other);
        assertSame(other, userList.getUser());
        assertSame(ibai, userList.getUserById(1));
    }
}