import java.rmi.*;
import java.util.ArrayList;

public interface ServerActions extends Remote {
    public User register(String username, String password, String name) throws java.rmi.RemoteException;
    public User login(String username, String password) throws java.rmi.RemoteException;
    public void logout() throws java.rmi.RemoteException;
    public ArrayList<SearchResult> search(String searchWords) throws java.rmi.RemoteException;
    public void indexURL(String url) throws java.rmi.RemoteException;
    public void updateDownloaderStatus(boolean active, int id, int port) throws java.rmi.RemoteException;
    public void updateBarrelStatus(boolean active, int id, int port) throws java.rmi.RemoteException;
    public void updateTopSearches(ArrayList<String> topSearches) throws java.rmi.RemoteException;
    public void printSystemDetails() throws java.rmi.RemoteException;
}
