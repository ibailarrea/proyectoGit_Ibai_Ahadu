

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
    void deleteUserStoresDeletionTimestamp() {
        userList.addUser(ibai);

        assertTrue(userList.deleteUser(ibai));

        SyncData data = userList.createSyncData();

        assertTrue(data.getDeletedUsers().containsKey(1));
        assertTrue(data.getDeletedUsers().get(1) > 0L);
    }

    @Test
    void createSyncDataContainsUsers() {
        userList.addUser(ibai);

        SyncData data = userList.createSyncData();

        assertEquals(1, data.getUsers().size());
        assertEquals("ibai", data.getUsers().get(0).getUsername());
    }

    @Test
    void createSyncDataContainsDeletedUsers() {
        userList.addUser(ibai);
        userList.deleteUser(ibai);

        SyncData data = userList.createSyncData();

        assertTrue(data.getDeletedUsers().containsKey(1));
    }

    @Test
    void updateFromSyncDataUpdatesUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(2, "ana", "1234"));

        SyncData data = new SyncData(users, new HashMap<>());

        userList.updateFromSyncData(data);

        assertEquals(1, userList.getUser().size());
        assertEquals("ana", userList.getUserById(2).getUsername());
    }

    @Test
    void updateFromSyncDataUpdatesDeletedUsers() {
        HashMap<Integer, Long> deleted = new HashMap<>();
        deleted.put(5, 100L);

        SyncData data = new SyncData(new ArrayList<>(), deleted);

        userList.updateFromSyncData(data);

        SyncData result = userList.createSyncData();

        assertEquals(100L, result.getDeletedUsers().get(5));
    }

    @Test
    void saveAndLoadDeletedUsers(@TempDir Path dir)
            throws IOException, ClassNotFoundException {

        userList.addUser(ibai);
        userList.deleteUser(ibai);

        String file = dir.resolve("deleted-users.dat").toString();

        userList.saveDeletedUsers(file);

        UserList loaded = new UserList();
        loaded.loadDeletedUsers(file);

        SyncData data = loaded.createSyncData();

        assertTrue(data.getDeletedUsers().containsKey(1));
    }

    @Test
    void loadDeletedUsersMissingFile(@TempDir Path dir) {
        String file = dir.resolve("missing.dat").toString();

        assertThrows(
                IOException.class,
                () -> userList.loadDeletedUsers(file));
    }
}
