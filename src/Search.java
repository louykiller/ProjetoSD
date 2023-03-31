import java.rmi.Remote;
import java.util.ArrayList;

public interface Search extends Remote {
    public ArrayList<ArrayList<SearchResult>> search(String searchWords) throws java.rmi.RemoteException;
    public ArrayList<ArrayList<SearchResult>> mostRelevantUrl() throws java.rmi.RemoteException;
}
