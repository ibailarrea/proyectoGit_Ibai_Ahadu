import java.util.ArrayList;

public class UserList {
    ArrayList<User> userList;

    public UserList() {
        userList = new ArrayList<>();
    }

    public ArrayList<User> getUser() {
        return userList;
    }

    public void setUser(ArrayList<User> user) {
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
        
        if (getUserById(user.getId()) == null) {
            return false;
        }

        userList.remove(getUserById(user.getId()));
        return true;

    }
}
