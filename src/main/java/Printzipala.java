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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteLogin'");
    }
    private void addLogin() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addLogin'");
    }
    private void queryByID() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByID'");
    }
    private void listLoginID() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listLoginID'");
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
