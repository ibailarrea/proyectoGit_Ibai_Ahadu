import java.util.List;
import java.util.Scanner;

public class Printzipala{
    Scanner scanner;
    UserList userList;
    

    public Printzipala(){
        scanner= new Scanner(System.in);
        userList = new UserList();
        hasieratu();
        
    }
    private void hasieratu() {
        int aukera= -1;
        

        while (aukera!=0) {
            aukera = menua();
            switch (aukera) {
                case 1: listLoginID();
                    
                    break;
                case 2: queryByID();
                    
                    break;
                case 3: addLogin();
                    
                    break;
                case 4: deleteLogin();
                    break;
                case 0: 
                break;
                default:
                    System.out.println("Is not a possible option, put an other one ");
                    break;
            }
        }

    }
    private void deleteLogin() {
        int id;
        List<User> list = userList.getUser();
        boolean found = false;
        User user = new User(0,null,null);

        System.out.println("What ID do you want to delete: ");
        id= scanner.nextInt();

        for ( User person : list){

            if(person.getId() == id){
                System.out.println("Deleting: " + person.toString());
                user= person;
                found = true;
            }

        }
        if (found == false){
            System.out.println("This id doesn't exist");
        }else{
            userList.deleteUser(user);
        }


        
        
    }
    private void addLogin() {
        int id;
        String password;
        String username;
        String tab;

        System.out.println("Id: ");
        id= scanner.nextInt();
        tab= scanner.nextLine();
        System.out.println("Username: ");
        username= scanner.nextLine();
        
        System.out.println("Password: ");
        password = scanner.nextLine();
        

        userList.addUser( new User(id,username,password));
        System.out.println(" Sortuta :" + id + "\n" + username + "\n" + password);
    }
    private void queryByID() {
        int id;
        List<User> list = userList.getUser();
        boolean found = false;

        System.out.println("What ID do you want to search: ");
        id= scanner.nextInt();
        
        for ( User person : list){

            if(person.getId() == id){
                System.out.println("The user is: " + person.toString());
                found = true;
            }

        }
        if (found == false){
            System.out.println("This id doesn't exist");
        }

    }
    private void listLoginID() {
        List<User> list = userList.getUser();
        for ( User person : list){

            System.out.println(person.toString());
        }

        
    }
    private int menua() {
        int aukera;
        System.out.println("Choose one option: ");
        System.out.println("1-List Login IDs ");
        System.out.println("2-Query by ID ");
        System.out.println("3-Add Login ");
        System.out.println("4-Delete Login ");
        System.out.println("0-exit ");
        aukera= scanner.nextInt();
        return aukera;
    }
    public static void main(String[] args) {
        Printzipala prin = new Printzipala();

    }

}
