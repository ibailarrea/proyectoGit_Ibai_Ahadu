
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserList {

    ArrayList<User> users;
    private HashMap<Integer, Long> deletedUsers;

    public UserList() {
        users = new ArrayList<>();
        deletedUsers = new HashMap<>();
    }

    public ArrayList<User> getUser() {
        return users;
    }

    public void setUser(ArrayList<User> user) {
        this.users = user;
    }

    public User getUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        if (getUserById(user.getId()) != null) {
            return false;
        }

        users.add(user);
        return true;
    }

    public boolean deleteUser(User user) {
        User existingUser = getUserById(user.getId());

        if (existingUser == null) {
            return false;
        }

        deletedUsers.put(user.getId(), System.currentTimeMillis());
        users.remove(existingUser);

        return true;
    }

    public void saveToFile(String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream output = new ObjectOutputStream(fos)) {

            output.writeObject(users);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filePath)
            throws IOException, ClassNotFoundException {

        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream input = new ObjectInputStream(fis)) {

            users = (ArrayList<User>) input.readObject();
        }

        for (User user : users) {
            if (user.getLastModified() <= 0) {
                user.setLastModified(System.currentTimeMillis());
            }
        }
    }

    public void saveDeletedUsers(String filePath) throws IOException {
        try (ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(filePath))) {

            output.writeObject(deletedUsers);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadDeletedUsers(String filePath)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(filePath))) {

            deletedUsers = (HashMap<Integer, Long>) input.readObject();
        }
    }

    public SyncData createSyncData() {
        return new SyncData(users, deletedUsers);
    }

    public void updateFromSyncData(SyncData data) {
        users = new ArrayList<>(data.getUsers());
        deletedUsers = new HashMap<>(data.getDeletedUsers());
    }
}