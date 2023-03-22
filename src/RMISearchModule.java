// RMI Search Module – Este é o componente visível pelos clientes. Comunica com
//os Storage Barrels usando RMI (note que uma opção é usar RMI callbacks para
//cada Barrel). Este RMI Search Module não tem de armazenar quaisquer dados,
//dependendo inteiramente dos Storage Barrels para satisfazer os pedidos dos clientes.
// Uma possibilidade será escolher aleatoriamente um Storage Barrel para cada
//pesquisa, fazendo-se assim balanceamento de carga.

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMISearchModule extends UnicastRemoteObject implements ClientActions {
    public RMISearchModule() throws RemoteException{
        super();
    }

    public void indexURL(String url) throws RemoteException{
        // Check if URL was visited before
        // If not, add it to the start of the URLs queue
        System.out.println("URL Recieved: " + url);

    }

    public boolean login(String username, String password) throws RemoteException{
        // Check if the credentials are correct
        // If so, return true else false
        System.out.println("username: " + username + ", password: " + password);

        return true;
    }

    public void logout() throws RemoteException{
        // Log out the user
    }

    public boolean register(String username, String password) throws RemoteException{
        // Verify credentials to see if there are no duplicates
        // If not, create user
        return false;
    }
    public ArrayList<SearchResult> search(String searchWords) throws RemoteException{
        System.out.println("Client searched for: " + searchWords);
        return null;
    }


    public static void main(String args[]) {
        try {
            RMISearchModule sm = new RMISearchModule();
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("search", sm);
            System.out.println("Search Module Server ready");

        } catch (RemoteException re) {
            System.out.println("Exception in RMISearchModule: " + re);
        }
    }

}
