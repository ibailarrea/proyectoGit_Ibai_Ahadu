
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {


    private String name;
    private List<Integer> userIds;

    public Category(String name) {
        this.name = name;
        this.userIds = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public boolean addUserId(int userId) {
        if (userIds.contains(userId)) {
            return false;
        }

        userIds.add(userId);
        return true;
    }

    public boolean removeUserId(int userId) {
        return userIds.remove(Integer.valueOf(userId));
    }

    @Override
    public String toString() {
        return "Category [name=" + name + ", userIds=" + userIds + "]";
    }
}