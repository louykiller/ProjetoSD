import java.rmi.*;
import java.util.ArrayList;

public interface ServerActions extends Remote {
    User register(String username, String password, String name) throws java.rmi.RemoteException;
    User login(String username, String password) throws java.rmi.RemoteException;
    void logout() throws java.rmi.RemoteException;
    ArrayList<SearchResult> search(String searchWords) throws java.rmi.RemoteException;

    void indexURL(String url) throws java.rmi.RemoteException;
    void updateDownloaderStatus(boolean active, int id, int port) throws java.rmi.RemoteException;
    void updateBarrelStatus(boolean active, int id, int port) throws java.rmi.RemoteException;
    void printSystemDetails() throws java.rmi.RemoteException;
}
