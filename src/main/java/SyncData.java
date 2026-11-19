
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncData implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("users", ArrayList.class),
        new ObjectStreamField("deletedUsers", HashMap.class)
    };

    private List<User> users;
    private Map<Integer, Long> deletedUsers;

    public SyncData() {
        users = new ArrayList<>();
        deletedUsers = new HashMap<>();
    }

    public SyncData(List<User> users, Map<Integer, Long> deletedUsers) {
        this.users = new ArrayList<>(users);
        this.deletedUsers = new HashMap<>(deletedUsers);
    }

    public List<User> getUsers() {
        return users;
    }

    public Map<Integer, Long> getDeletedUsers() {
        return deletedUsers;
    }

    private void writeObject(ObjectOutputStream output) throws IOException {
        ObjectOutputStream.PutField fields = output.putFields();
        fields.put("users", new ArrayList<>(users));
        fields.put("deletedUsers", new HashMap<>(deletedUsers));
        output.writeFields();
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {

        ObjectInputStream.GetField fields = input.readFields();

        users = new ArrayList<>(
                (ArrayList<User>) fields.get("users", new ArrayList<User>()));

        deletedUsers = new HashMap<>(
                (HashMap<Integer, Long>) fields.get(
                        "deletedUsers", new HashMap<Integer, Long>()));
    }

    public void merge(SyncData other) {
        Map<Integer, User> allUsers = createUserMap();

        mergeIncomingUsers(allUsers, other.getUsers());
        mergeIncomingDeletions(other.getDeletedUsers());
        removeDeletedUsers(allUsers);

        users = new ArrayList<>(allUsers.values());
    }

    private Map<Integer, User> createUserMap() {
        Map<Integer, User> allUsers = new HashMap<>();

        for (User user : users) {
            allUsers.put(user.getId(), user);
        }

        return allUsers;
    }

    private void mergeIncomingUsers(
            Map<Integer, User> allUsers, List<User> incomingUsers) {

        for (User incomingUser : incomingUsers) {
            int id = incomingUser.getId();
            User currentUser = allUsers.get(id);

            long currentTime = getLatestTimestamp(
                    currentUser, deletedUsers.get(id));

            if (incomingUser.getLastModified() > currentTime) {
                allUsers.put(id, incomingUser);
            }
        }
    }

    private long getLatestTimestamp(
            User currentUser, Long deletionTime) {

        long latestTime = -1;

        if (currentUser != null) {
            latestTime = currentUser.getLastModified();
        }

        if (deletionTime != null && deletionTime > latestTime) {
            latestTime = deletionTime;
        }

        return latestTime;
    }

    private void mergeIncomingDeletions(
            Map<Integer, Long> incomingDeletions) {

        for (Map.Entry<Integer, Long> entry
                : incomingDeletions.entrySet()) {

            int id = entry.getKey();
            long incomingTime = entry.getValue();
            Long currentTime = deletedUsers.get(id);

            if (currentTime == null || incomingTime > currentTime) {
                deletedUsers.put(id, incomingTime);
            }
        }
    }

    private void removeDeletedUsers(Map<Integer, User> allUsers) {
        for (Map.Entry<Integer, Long> entry : deletedUsers.entrySet()) {
            int id = entry.getKey();
            long deletionTime = entry.getValue();

            User user = allUsers.get(id);

            if (user != null
                    && deletionTime >= user.getLastModified()) {
                allUsers.remove(id);
            }
        }
    }
}