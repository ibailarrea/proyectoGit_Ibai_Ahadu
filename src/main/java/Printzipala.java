import java.util.Scanner;

public class Printzipala{
    Scanner scanner;
    

    public Printzipala(){
        scanner= new Scanner(System.in);
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

        System.out.println("What ID do you want to delete: ");
        id= scanner.nextInt();

        // login.delete(id);
        
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
        tab= scanner.nextLine();
        System.out.println("Password: ");
        password = scanner.nextLine();
        tab= scanner.nextLine();

        // login.add( new Person(id,username,password));
        System.out.println(" Sortuta :" + id + "\n" + username + "\n" + password);
    }
    private void queryByID() {
        int id;

        System.out.println("What ID do you want to search: ");
        id= scanner.nextInt();
    }
    private void listLoginID() {
        //list<Person> list = login.getUserList();
        /*for ( Person person : list){
            System.out.println(person.toString());
        }

        */
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
