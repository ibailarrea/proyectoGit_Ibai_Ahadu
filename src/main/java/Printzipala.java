import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Printzipala {

    private static final String ID_NOT_FOUND = "This id doesn't exist";
    private static final String VAULT_FILE = "vault.dat";

    private final Scanner scanner;
    private final UserList userList;

    public Printzipala() {
        this.scanner = new Scanner(System.in);
        this.userList = new UserList();

        try {
            userList.loadFromFile(VAULT_FILE);
            System.out.println("Users loaded successfully.");

        } catch (FileNotFoundException e) {
            System.out.println("No vault file found. Starting with an empty list.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public void hasieratu() {
        int aukera = -1;

        while (aukera != 0) {
            aukera = menua();
            if (aukera != 0) {
                exekutatu(aukera);
            }
        }
    }

    private void exekutatu(int aukera) {
        switch (aukera) {
            case 1:
                listLoginID();
                break;
            case 2:
                queryByID();
                break;
            case 3:
                addLogin();
                break;
            case 4:
                deleteLogin();
                break;
            default:
                System.out.println("Is not a possible option, put an other one ");
        }
    }

    private void deleteLogin() {
        System.out.println("What ID do you want to delete: ");
        User person = userList.getUserById(scanner.nextInt());

        if (person == null) {
            System.out.println(ID_NOT_FOUND);
        } else {
            System.out.println("Deleting: " + person);

            if (userList.deleteUser(person)) {
                saveUsers();
                System.out.println("User deleted successfully.");
            }
        }
    }

    private void addLogin() {
        System.out.println("Id: ");
        int id = scanner.nextInt();

        System.out.println("Username: ");
        String username = readNonEmptyLine();

        System.out.println("Password: ");
        String password = readNonEmptyLine();

        if (userList.addUser(new User(id, username, password))) {
            saveUsers();
            System.out.println(" Sortuta :" + id + "\n" + username + "\n" + password);
        } else {
            System.out.println("This id already exists");
        }
    }

    private void queryByID() {
        System.out.println("What ID do you want to search: ");
        User person = userList.getUserById(scanner.nextInt());

        if (person == null) {
            System.out.println(ID_NOT_FOUND);
        } else {
            System.out.println("The user is: " + person);
        }
    }

    private void listLoginID() {
        for (User person : userList.getUser()) {
            System.out.println(person);
        }
    }

    private void saveUsers() {
        try {
            userList.saveToFile(VAULT_FILE);
            System.out.println("Users saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    private int menua() {
        System.out.println("Choose one option: ");
        System.out.println("1-List Login IDs ");
        System.out.println("2-Query by ID ");
        System.out.println("3-Add Login ");
        System.out.println("4-Delete Login ");
        System.out.println("0-exit ");
        return scanner.nextInt();
    }

    private String readNonEmptyLine() {
        String line = scanner.nextLine();
        while (line.trim().isEmpty()) {
            line = scanner.nextLine();
        }
        return line;
    }

    public static void main(String[] args) {
        Printzipala prin = new Printzipala();
        prin.hasieratu();
    }
}


