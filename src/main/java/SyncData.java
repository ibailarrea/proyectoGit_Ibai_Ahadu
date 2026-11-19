import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncData implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<User> users;
    private HashMap<Integer, Long> deletedUsers;

    public SyncData() {
        users = new ArrayList<>();
        deletedUsers = new HashMap<>();
    }

    public SyncData(ArrayList<User> users,
                    HashMap<Integer, Long> deletedUsers) {

        this.users = new ArrayList<>(users);
        this.deletedUsers = new HashMap<>(deletedUsers);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public HashMap<Integer, Long> getDeletedUsers() {
        return deletedUsers;
    }

    public void merge(SyncData other) {

        Map<Integer, User> allUsers = new HashMap<>();

        for (User user : users) {
            allUsers.put(user.getId(), user);
        }

        for (User incomingUser : other.getUsers()) {

            int id = incomingUser.getId();
            User currentUser = allUsers.get(id);

            Long deletionTime = deletedUsers.get(id);

            long currentTime = -1;

            if (currentUser != null) {
                currentTime = currentUser.getLastModified();
            }

            if (deletionTime != null && deletionTime > currentTime) {
                currentTime = deletionTime;
            }

            if (incomingUser.getLastModified() > currentTime) {
                allUsers.put(id, incomingUser);
            }
        }

        for (Map.Entry<Integer, Long> entry :
                other.getDeletedUsers().entrySet()) {

            int id = entry.getKey();
            long incomingDeletionTime = entry.getValue();

            Long currentDeletionTime = deletedUsers.get(id);

            if (currentDeletionTime == null
                    || incomingDeletionTime > currentDeletionTime) {

                deletedUsers.put(id, incomingDeletionTime);
            }
        }

        for (Map.Entry<Integer, Long> entry : deletedUsers.entrySet()) {

            int id = entry.getKey();
            long deletionTime = entry.getValue();

            User user = allUsers.get(id);

            if (user != null
                    && deletionTime >= user.getLastModified()) {

                allUsers.remove(id);
            }
        }

        users = new ArrayList<>(allUsers.values());
    }
}