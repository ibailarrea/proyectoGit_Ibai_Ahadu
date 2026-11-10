import java.util.ArrayList;
import java.util.List;

public class UserList {
    private List<User> userList;

    public UserList() {
        userList = new ArrayList<>();
    }

    public List<User> getUser() {
        return userList;
    }

    public void setUser(List<User> user) {
        this.userList = user;
    }

    public User getUserById(int id) {
        for (User user : userList) {
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

        userList.add(user);
        return true;
    }

    public boolean deleteUser(User user) {
        User existing = getUserById(user.getId());
        if (existing == null) {
            return false;
        }

        return userList.remove(existing);
    }
}