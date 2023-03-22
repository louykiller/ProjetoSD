// RMI Client – É o cliente RMI usado pelos utilizadores para aceder às funcionalidades do Googol.
// Pretende-se que este cliente tenha uma UI bastante simples e que
//se limite a invocar os métodos remotos no servidor RMI.

import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Scanner;

public class RMIClient {
    public static boolean logged;
    public static void main(String args[]) {
        try {
            ClientActions ca = (ClientActions) LocateRegistry.getRegistry(7000).lookup("search");
            Scanner sc = new Scanner(System.in);
            logged = false;
            while (true){
                System.out.println("_____________________________________________________________");
                System.out.println("|                   GOOGOL SEARCH ENGINE                    |");
                System.out.println("| 1 - Search                                                |");
                System.out.println("| 2 - Index URL                                             |");
                if(!logged){
                    System.out.println("| 3 - Log In                                                |");
                    System.out.println("| 4 - Register                                              |");
                }
                else
                    System.out.println("| 3 - Log Out                                               |");
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

                            ca.indexURL(url);
                            System.out.println("You URL was sent to be indexed");
                        }
                        // Log In
                        case 3 -> {
                            if(logged) {
                                System.out.println("Logged out successfully");
                                logged = false;
                            }
                            else {
                                System.out.println("Username: ");
                                String username = sc.nextLine();
                                System.out.println("Password: ");
                                String password = sc.nextLine();

                                if (ca.login(username, password)) {
                                    logged = true;
                                    System.out.println("Logged In Successfully");
                                } else {
                                    System.out.println("Couldn't log in. Please try again.");
                                }
                            }
                        }
                        // Register
                        case 4 -> {
                            System.out.println("Username: ");
                            String username = sc.nextLine();
                            System.out.println("Password: ");
                            String password = sc.nextLine();

                            if (ca.register(username, password)) {
                                logged = true;
                                System.out.println("Registered Successfully");
                            } else {
                                System.out.println("Couldn't register. Please try again with other credentials.");
                            }
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
        }

    }
}
