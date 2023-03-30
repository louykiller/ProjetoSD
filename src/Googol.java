// Main class que vai iniciar todas as outras classes incluindo os indexStorageBarrels, RMISearchModule e os Downloaders

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Googol {

    static class Shutdown extends Thread {
        public List<String> urlsQueue;
        public Shutdown(List<String> urlsQueue){
            this.urlsQueue = urlsQueue;
        }
        public void run() {
            // Save the urlsQueue in a file
            File f = new File("urlsQueue.txt");
            try {
                FileWriter fw = new FileWriter(f);
                BufferedWriter bw = new BufferedWriter(fw);
                for(String url : urlsQueue){
                    bw.write(url + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args) throws InterruptedException, RemoteException {
        // Criar uma lista sincronizada
        List<String> urlsQueue = Collections.synchronizedList(new LinkedList<>());
        // This is to save all the urls in the queue when we shut down the program
        Runtime.getRuntime().addShutdownHook(new Shutdown(urlsQueue));
        // Load the previous urls
        File f = new File("urlsQueue.txt");
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader (fr);
            String s;
            while((s = br.readLine()) != null){
                urlsQueue.add(s);
            }
        } catch (EOFException e) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //urlsQueue.add("https://www.uc.pt");
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
            downloaders.add(new Thread(new Downloader(urlsQueue, i)));
            downloaders.get(i).start();
        }

        System.out.println("A iniciar os barrels");
        // Criar e inicar os barrels
        ArrayList<Thread> barrels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            barrels.add(new Thread(new IndexStorageBarrel(i)));
            barrels.get(i).start();
        }

        // Esperar que os downloaders acabem
        for (int i = 0; i < 3; i++) {
            downloaders.get(i).join();
        }

    }
}