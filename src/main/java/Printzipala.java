import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

public class Printzipala {

    private static final String ID_NOT_FOUND =
            "This id doesn't exist";

    private static final String VAULT_FILE = "vault.dat";
    private static final String DELETED_USERS_FILE = "deleted-users.dat";
    private static final String CONFIG_FILE = "config.properties";

    private final Scanner scanner;
    private final UserList userList;
    private final CategoryList categoryList;

    private String serverHost;
    private int serverPort;

    public Printzipala() {

        this.scanner = new Scanner(System.in);
        this.userList = new UserList();
        this.categoryList = new CategoryList();

        // Valores por defecto del servidor.
        serverHost = "localhost";
        serverPort = 5000;

        loadConfig();

        try {
            userList.loadFromFile(VAULT_FILE);
            System.out.println("Users loaded successfully.");

        } catch (FileNotFoundException e) {
            System.out.println(
                    "No vault file found. Starting with an empty list.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "Error loading users: " + e.getMessage());
        }

        try {
            userList.loadDeletedUsers(DELETED_USERS_FILE);

        } catch (FileNotFoundException e) {
            System.out.println("No deleted users file found.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "Error loading deleted users: " + e.getMessage());
        }
    }

    private void loadConfig() {

        Properties properties = new Properties();

        try (FileInputStream input =
                new FileInputStream(CONFIG_FILE)) {

            properties.load(input);

            serverHost = properties.getProperty(
                    "server.host", "localhost");

            serverPort = Integer.parseInt(
                    properties.getProperty("server.port", "5000"));

        } catch (FileNotFoundException e) {
            System.out.println(
                    "Configuration file not found. Using default settings.");

        } catch (IOException | NumberFormatException e) {
            System.out.println(
                    "Error loading configuration: " + e.getMessage());
        }
    }

    public void hasieratu() {

        String aukera = "";

        while (!aukera.equalsIgnoreCase("e")) {

            aukera = menua();

            if (!aukera.equalsIgnoreCase("e")) {
                exekutatu(aukera);
            }
        }
    }

    private void exekutatu(String aukera) {

        switch (aukera) {

            case "1":
                listLoginID();
                break;

            case "2":
                queryByID();
                break;

            case "3":
                addLogin();
                break;

            case "4":
                deleteLogin();
                break;

            case "5":
                viewCategories();
                break;

            case "6":
                addIDToCategory();
                break;

            case "7":
                removeIDFromCategory();
                break;

            case "8":
                addCategory();
                break;

            case "9":
                deleteCategory();
                break;

            case "s":
            case "S":
                syncWithServer();
                break;

            default:
                System.out.println(
                        "Is not a possible option, put another one");
        }
    }

    private void deleteLogin() {

        System.out.println("What ID do you want to delete: ");

        User person = userList.getUserById(scanner.nextInt());
        scanner.nextLine();

        if (person == null) {

            System.out.println(ID_NOT_FOUND);

        } else {

            System.out.println("Deleting: " + person);

            if (userList.deleteUser(person)) {

                for (Category category :
                        categoryList.getCategories()) {

                    category.removeUserId(person.getId());
                }

                saveUsers();

                System.out.println("User deleted successfully.");
            }
        }
    }

    private void addLogin() {

        System.out.println("Id: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Username: ");
        String username = readNonEmptyLine();

        System.out.println("Password: ");
        String password = readNonEmptyLine();

        if (userList.addUser(new User(id, username, password))) {

            saveUsers();

            System.out.println(
                    " Sortuta :" + id + "\n"
                    + username + "\n"
                    + password);

        } else {

            System.out.println("This id already exists");
        }
    }

    private void queryByID() {

        System.out.println("What ID do you want to search: ");

        User person = userList.getUserById(scanner.nextInt());
        scanner.nextLine();

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

    private void viewCategories() {

        if (categoryList.getCategories().isEmpty()) {

            System.out.println("There are no categories.");

        } else {

            for (Category category :
                    categoryList.getCategories()) {

                System.out.println(category);
            }
        }
    }

    private void addIDToCategory() {

        System.out.println("Category name: ");
        String name = readNonEmptyLine();

        System.out.println("User ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        if (userList.getUserById(id) == null) {

            System.out.println(ID_NOT_FOUND);

        } else if (categoryList.addUserToCategory(name, id)) {

            System.out.println(
                    "User added to category successfully.");

        } else {

            System.out.println(
                    "Category doesn't exist or user is already in it.");
        }
    }

    private void removeIDFromCategory() {

        System.out.println("Category name: ");
        String name = readNonEmptyLine();

        System.out.println("User ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        if (categoryList.removeUserFromCategory(name, id)) {

            System.out.println(
                    "User removed from category successfully.");

        } else {

            System.out.println(
                    "Category doesn't exist or user isn't in it.");
        }
    }

    private void addCategory() {

        System.out.println("Category name: ");
        String name = readNonEmptyLine();

        if (categoryList.addCategory(new Category(name))) {

            System.out.println("Category created successfully.");

        } else {

            System.out.println("This category already exists.");
        }
    }

    private void deleteCategory() {

        System.out.println("Category name: ");
        String name = readNonEmptyLine();

        if (categoryList.deleteCategory(name)) {

            System.out.println("Category deleted successfully.");

        } else {

            System.out.println("This category doesn't exist.");
        }
    }

    private void syncWithServer() {

        try {

            System.out.println("Connecting to server...");

            SyncData localData = userList.createSyncData();

            SyncData updatedData = SyncClient.synchronize(
                    serverHost,
                    serverPort,
                    localData);

            userList.updateFromSyncData(updatedData);

            saveUsers();

            System.out.println(
                    "Synchronization completed successfully.");

        } catch (IOException | ClassNotFoundException e) {

            System.out.println(
                    "Synchronization failed: " + e.getMessage());
        }
    }

    private void saveUsers() {

        try {

            userList.saveToFile(VAULT_FILE);

            userList.saveDeletedUsers(DELETED_USERS_FILE);

            System.out.println("Users saved successfully.");

        } catch (IOException e) {

            System.out.println(
                    "Error saving users: " + e.getMessage());
        }
    }

    private String menua() {

        System.out.println("\nChoose one option: ");

        System.out.println("1-List Login IDs");
        System.out.println("2-Query by ID");
        System.out.println("3-Add Login");
        System.out.println("4-Delete Login");
        System.out.println("5-View Categories and Login IDs");
        System.out.println("6-Add ID to Category");
        System.out.println("7-Remove ID from Category");
        System.out.println("8-Add Category");
        System.out.println("9-Delete Category");
        System.out.println("s-Synchronize with server");
        System.out.println("e-Exit");

        return readNonEmptyLine();
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