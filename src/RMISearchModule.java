// RMI Search Module – Este é o componente visível pelos clientes. Comunica com
//os Storage Barrels usando RMI (note que uma opção é usar RMI callbacks para
//cada Barrel). Este RMI Search Module não tem de armazenar quaisquer dados,
//dependendo inteiramente dos Storage Barrels para satisfazer os pedidos dos clientes.
// Uma possibilidade será escolher aleatoriamente um Storage Barrel para cada
//pesquisa, fazendo-se assim balanceamento de carga.

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RMISearchModule extends UnicastRemoteObject implements ServerActions, Runnable {
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
        // Adds the URL to the start of the queue
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
        System.out.println("User logged out");
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
    private int barrelIndex;
    public ArrayList<SearchResult> search(String searchWords) throws RemoteException{
        ArrayList<SearchResult> results = null;
        while(results == null) {
            try {
                Search srch = (Search) LocateRegistry.getRegistry(8000 + barrelIndex).lookup("search");
                results = srch.search(searchWords);
            } catch (NotBoundException e) {
                System.out.println("Couldn't get info from barrel " + barrelIndex);
            }
            barrelIndex++;
            if(barrelIndex == 3){
                barrelIndex = 0;
            }
        }
        return results;
    }
    public HashMap<String, SystemElements> elements = new HashMap<>();
    public ArrayList<String> topSearches = new ArrayList<>();

    public void updateDownloaderStatus(boolean active, int id, int port) throws java.rmi.RemoteException {
        // Insert / Update the downloaderElement
        elements.put("D" + id, new DownloaderElement(id, port, active));
    }
    public void updateBarrelStatus(boolean active, int id, int port) throws java.rmi.RemoteException {
        // Insert / Update the barrelElement
        elements.put("B" + id, new BarrelElement(id, port, active));
    }
    public void updateTopSearches(ArrayList<String> topSearches) throws java.rmi.RemoteException {
        // Update the top searches
        this.topSearches = topSearches;
    }
    public void printSystemDetails() throws java.rmi.RemoteException{
        try {
            // Gets the registry for the client print
            ClientPrint cp = (ClientPrint) LocateRegistry.getRegistry(7001).lookup("client");
            // System status (Barrels and Downloaders)
            cp.print("Estado do sistema:");
            for(SystemElements se : elements.values()){
                cp.print(se.toString());
            }
            // Top 10 Searches
            cp.print("\nTop 10 pesquisas:");
            for(String s : topSearches){
                cp.print("- " + s);
            }
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        barrelIndex = 0;
        // Ligar o server
        try {
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("server", this);
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
