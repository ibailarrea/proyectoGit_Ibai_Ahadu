
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

class SyncDataTest {

    @Test
    void constructorCreatesEmptyData() {
        SyncData data = new SyncData();

        assertTrue(data.getUsers().isEmpty());
        assertTrue(data.getDeletedUsers().isEmpty());
    }

    @Test
    void constructorCopiesUsersAndDeletions() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1, "ibai", "1234"));

        HashMap<Integer, Long> deleted = new HashMap<>();
        deleted.put(2, 100L);

        SyncData data = new SyncData(users, deleted);

        assertEquals(1, data.getUsers().size());
        assertEquals(100L, data.getDeletedUsers().get(2));
    }

    @Test
    void mergeAddsNewUser() {
        SyncData local = new SyncData();

        ArrayList<User> remoteUsers = new ArrayList<>();
        User remoteUser = new User(1, "ana", "1234");
        remoteUser.setLastModified(200L);
        remoteUsers.add(remoteUser);

        SyncData remote = new SyncData(
                remoteUsers, new HashMap<>());

        local.merge(remote);

        assertEquals(1, local.getUsers().size());
        assertEquals("ana",
                local.getUsers().get(0).getUsername());
    }

    @Test
    void mergeKeepsNewerUser() {
        User oldUser = new User(1, "old", "1111");
        oldUser.setLastModified(100L);

        User newUser = new User(1, "new", "2222");
        newUser.setLastModified(200L);

        ArrayList<User> localUsers = new ArrayList<>();
        localUsers.add(oldUser);

        ArrayList<User> remoteUsers = new ArrayList<>();
        remoteUsers.add(newUser);

        SyncData local = new SyncData(
                localUsers, new HashMap<>());

        SyncData remote = new SyncData(
                remoteUsers, new HashMap<>());

        local.merge(remote);

        assertEquals(1, local.getUsers().size());
        assertEquals("new",
                local.getUsers().get(0).getUsername());
    }

    @Test
    void mergeKeepsLocalUserWhenItIsNewer() {
        User localUser = new User(1, "local", "1111");
        localUser.setLastModified(300L);

        User remoteUser = new User(1, "remote", "2222");
        remoteUser.setLastModified(200L);

        ArrayList<User> localUsers = new ArrayList<>();
        localUsers.add(localUser);

        ArrayList<User> remoteUsers = new ArrayList<>();
        remoteUsers.add(remoteUser);

        SyncData local = new SyncData(
                localUsers, new HashMap<>());

        SyncData remote = new SyncData(
                remoteUsers, new HashMap<>());

        local.merge(remote);

        assertEquals("local",
                local.getUsers().get(0).getUsername());
    }

    @Test
    void mergeAddsDeletion() {
        HashMap<Integer, Long> remoteDeleted = new HashMap<>();
        remoteDeleted.put(1, 200L);

        SyncData local = new SyncData();
        SyncData remote = new SyncData(
                new ArrayList<>(), remoteDeleted);

        local.merge(remote);

        assertTrue(local.getDeletedUsers().containsKey(1));
        assertEquals(200L, local.getDeletedUsers().get(1));
    }

    @Test
    void mergeRemovesUserWhenDeletionIsNewer() {
        User user = new User(1, "ibai", "1234");
        user.setLastModified(100L);

        ArrayList<User> users = new ArrayList<>();
        users.add(user);

        HashMap<Integer, Long> deleted = new HashMap<>();
        deleted.put(1, 200L);

        SyncData local = new SyncData(users, deleted);

        local.merge(new SyncData());

        assertTrue(local.getUsers().isEmpty());
    }

    @Test
    void mergeKeepsUserWhenUserIsNewerThanDeletion() {
        User user = new User(1, "ibai", "1234");
        user.setLastModified(300L);

        ArrayList<User> users = new ArrayList<>();
        users.add(user);

        HashMap<Integer, Long> deleted = new HashMap<>();
        deleted.put(1, 200L);

        SyncData local = new SyncData(users, deleted);

        local.merge(new SyncData());

        assertEquals(1, local.getUsers().size());
    }

    @Test
    void mergeDoesNotRestoreDeletedOldUser() {
        HashMap<Integer, Long> deleted = new HashMap<>();
        deleted.put(1, 300L);

        SyncData local = new SyncData(
                new ArrayList<>(), deleted);

        User oldUser = new User(1, "ibai", "1234");
        oldUser.setLastModified(100L);

        ArrayList<User> remoteUsers = new ArrayList<>();
        remoteUsers.add(oldUser);

        SyncData remote = new SyncData(
                remoteUsers, new HashMap<>());

        local.merge(remote);

        assertTrue(local.getUsers().isEmpty());
    }

    @Test
    void mergeKeepsNewestDeletionTimestamp() {
        HashMap<Integer, Long> localDeleted = new HashMap<>();
        localDeleted.put(1, 100L);

        HashMap<Integer, Long> remoteDeleted = new HashMap<>();
        remoteDeleted.put(1, 200L);

        SyncData local = new SyncData(
                new ArrayList<>(), localDeleted);

        SyncData remote = new SyncData(
                new ArrayList<>(), remoteDeleted);

        local.merge(remote);

        assertEquals(200L, local.getDeletedUsers().get(1));
    }

    @Test
    void mergeKeepsDifferentUsers() {
        ArrayList<User> localUsers = new ArrayList<>();
        localUsers.add(new User(1, "ibai", "1234"));

        ArrayList<User> remoteUsers = new ArrayList<>();
        remoteUsers.add(new User(2, "ana", "5678"));

        SyncData local = new SyncData(
                localUsers, new HashMap<>());

        SyncData remote = new SyncData(
                remoteUsers, new HashMap<>());

        local.merge(remote);

        assertEquals(2, local.getUsers().size());
    }
}
