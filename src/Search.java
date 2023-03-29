import java.util.ArrayList;

public interface Search {
    public ArrayList<SearchResult> search(String searchWords) throws java.rmi.RemoteException;
}
