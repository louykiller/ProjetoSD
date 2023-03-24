// RMI Search Module – Este é o componente visível pelos clientes. Comunica com
//os Storage Barrels usando RMI (note que uma opção é usar RMI callbacks para
//cada Barrel). Este RMI Search Module não tem de armazenar quaisquer dados,
//dependendo inteiramente dos Storage Barrels para satisfazer os pedidos dos clientes.
// Uma possibilidade será escolher aleatoriamente um Storage Barrel para cada
//pesquisa, fazendo-se assim balanceamento de carga.

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RMISearchModule extends UnicastRemoteObject implements ClientActions, Runnable {
    private final List<String> urlsQueue;
    private final ArrayList<User> users;
    private final File f = new File("users.txt");
    public RMISearchModule(List<String> urlsQueue) throws RemoteException{
        super();
        this.urlsQueue = urlsQueue;
        this.users = new ArrayList<>();
        new Thread(this, "Server").start();
    }

    public void indexURL(String url) throws RemoteException{
        System.out.println("URL Recieved: " + url);
        urlsQueue.add(0, url);
    }

    public User login(String username, String password) throws RemoteException{
        // Check if the credentials are correct
        for(User u : users){
            // If it exists return the user
            if(u.hashCode() == Objects.hash(username, password)){
                System.out.println("User logged in: " + u.name);
                return u;
            }
        }
        // If not found return null
        return null;
    }

    public void logout() throws RemoteException{
        // Log out the user
    }

    public User register(String username, String password, String name) throws RemoteException{
        // Verify credentials to see if there are no duplicates
        for(User u : users){
            // If it exists return null
            if(u.hashCode() == Objects.hash(username, password)){
                return null;
            }
        }
        // If not, create user and return it
        User newUser = new User(username, password, name);
        System.out.println("User registeresd: " + newUser.name);
        users.add(newUser);
        // Add it to the file
        try {
            FileOutputStream fos = new FileOutputStream(f, true);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(newUser);
            oos.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e){
            System.out.println("Error writing to file");
        }
        return newUser;
    }
    public ArrayList<SearchResult> search(String searchWords) throws RemoteException{
        // TODO: Get information from barrels
        System.out.println("Client searched for: " + searchWords);
        return null;
    }

    public void run() {
        // Ligar o server
        try {
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("search", this);
            System.out.println("Search Module Server ready");
        } catch (RemoteException re) {
            System.out.println("Exception in RMISearchModule: " + re);
        }
        // Get all the info of users from text files
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            User u;
            while ((u = (User) ois.readObject()) != null) {
                users.add(u);
            }
            ois.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (ClassNotFoundException e){
            System.out.println("Class not found");
        } catch (EOFException e) {
            // EOF
        } catch (IOException e){
            System.out.println("Error reading file");
        }
    }

}
