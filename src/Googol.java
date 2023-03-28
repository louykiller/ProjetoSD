// Main class que vai iniciar todas as outras classes incluindo os indexStorageBarrels, RMISearchModule e os Downloaders

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Googol  {
    public static void main(String[] args) throws InterruptedException {
        // Criar uma lista sincronizada
        List<String> urlsQueue = Collections.synchronizedList(new LinkedList<String>());
        // Ligar o server
        try {
            RMISearchModule rsm = new RMISearchModule(urlsQueue);
        } catch (RemoteException e){
            System.out.println("Error initiating server");
        }

        System.out.println("A iniciar os downloaders");
        // Criar e iniciar as threads dos downloaders
        ArrayList<Thread> downloaders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            downloaders.add(new Thread(new Downloader(urlsQueue)));
            downloaders.get(i).start();
        }

        System.out.println("A iniciar os barrels");
        // Criar e inicar os barrels
        ArrayList<Thread> barrels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            barrels.add(new IndexStorageBarrel());
            barrels.get(i).start();
        }

        // Esperar que os downloaders acabem
        for (int i = 0; i < 3; i++) {
            downloaders.get(i).join();
        }


    }
}