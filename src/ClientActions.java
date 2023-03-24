import java.rmi.*;
import java.util.ArrayList;

public interface ClientActions extends Remote {
    public User register(String username, String password, String name) throws java.rmi.RemoteException;
    public User login(String username, String password) throws java.rmi.RemoteException;
    public void logout() throws java.rmi.RemoteException;
    public ArrayList<SearchResult> search(String searchWords) throws java.rmi.RemoteException;
    public void indexURL(String url) throws java.rmi.RemoteException;
}
