import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username;
    private String password;
    private long lastModified;

    private static final long serialVersionUID = 1L;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.lastModified = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.lastModified = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.lastModified = System.currentTimeMillis();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.lastModified = System.currentTimeMillis();
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "User [id=" + id
                + ", username=" + username
                + ", password=" + password + "]";
    }
}