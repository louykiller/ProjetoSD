// RMI Client – É o cliente RMI usado pelos utilizadores para aceder às funcionalidades do Googol.
// Pretende-se que este cliente tenha uma UI bastante simples e que
//se limite a invocar os métodos remotos no servidor RMI.

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class RMIClient extends UnicastRemoteObject implements ClientPrint {
    public static User user;
    public RMIClient() throws RemoteException {
        super();
    }
    public void print(String s) throws java.rmi.RemoteException{
        System.out.println(s);
    }
    public static void main(String args[]) {
        user = null;
        try {
            Registry r = LocateRegistry.createRegistry(7001);
            r.rebind("client", new RMIClient());

            ServerActions ca = (ServerActions) LocateRegistry.getRegistry(7000).lookup("server");
            Scanner sc = new Scanner(System.in);
            while (true){
                System.out.println("_____________________________________________________________");
                System.out.println("|                   GOOGOL SEARCH ENGINE                    |");
                System.out.println("| 1 - Search                                                |");
                System.out.println("| 2 - Index URL                                             |");
                if(user == null){
                    System.out.println("| 3 - Log In                                                |");
                    System.out.println("| 4 - Register                                              |");
                }
                else
                    System.out.println("| 3 - Log Out                                               |");
                System.out.println("| 9 - Informações gerais sobre o sistema                    |");
                System.out.println("| 0 - Exit                                                  |");
                System.out.println("_____________________________________________________________");

                try {
                    int input = Integer.parseInt(sc.nextLine());

                    if (input == 0) break;
                    switch (input) {
                        // Search
                        case 1 -> {
                            System.out.print("Googol Search:\n");
                            String searchWords = sc.nextLine();
                            // Get the results for the search words
                            ArrayList<SearchResult> results = ca.search(searchWords);
                            if (results == null)
                                System.out.println("No results found!");
                            else {
                                for (SearchResult rp : results) {
                                    System.out.println(rp.toString());
                                }
                            }
                        }
                        // Index URL
                        case 2 -> {
                            System.out.print("URL: ");
                            String url = sc.nextLine();
                            // Index the URL to be
                            ca.indexURL(url);
                            System.out.println("You URL was sent to be indexed");
                        }
                        // Log In
                        case 3 -> {
                            if(user != null) {
                                user = null;
                                System.out.println("Logged out successfully");
                            }
                            else {
                                System.out.println("Username: ");
                                String username = sc.nextLine();
                                System.out.println("Password: ");
                                String password = sc.nextLine();

                                user = ca.login(username, password);
                                if (user != null) {
                                    System.out.println("Logged In Successfully. Welcome back " + user.name);
                                } else {
                                    System.out.println("Couldn't log in. Please try again.");
                                }
                            }
                        }
                        // Register
                        case 4 -> {
                            if(user != null)
                                break;
                            System.out.println("Name: ");
                            String name = sc.nextLine();
                            System.out.println("Username: ");
                            String username = sc.nextLine();
                            System.out.println("Password: ");
                            String password = sc.nextLine();

                            user = ca.register(username, password, name);
                            if (user != null) {
                                System.out.println("Registered Successfully. Welcome " + user.name);
                            } else {
                                System.out.println("Couldn't register. Please try again with other credentials.");
                            }
                        }
                        // Informações de sistema
                        case 9 -> {
                            ca.printSystemDetails();
                        }
                        default -> System.out.println("Invalid input please try again!");
                    }
                } catch (NumberFormatException e){
                    System.out.println("Invalid input please try again!");
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Server offline. Try again later");
            e.printStackTrace();
        }

    }
}
