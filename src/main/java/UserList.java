import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class UserList {
    ArrayList<User> users;

    public UserList() {
        users = new ArrayList<>();
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
    }
}