// Main class que vai iniciar todas as outras classes incluindo os indexStorageBarrels, RMISearchModule e os Downloaders

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Googol  {
    public static void main(String[] args) throws InterruptedException {
        // Criar uma lista sincronizada
        List<String> urlsQueue = Collections.synchronizedList(new LinkedList<String>());
        urlsQueue.add("https://www.uc.pt");
        // Criar e iniciar as threads dos downloaders
        ArrayList<Thread> downloaders = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            downloaders.add(new Thread(new Downloader(urlsQueue)));
            downloaders.get(i).start();
        }


        // Esperar que os downloaders acabem
        for(int i = 0; i < 3; i++){
            downloaders.get(i).join();
        }
    }
}