// Index Storage Barrel – É o servidor central (replicado) que armazena todos os
//dados da aplicação, recebendo os elementos do índice (palavras e URLs) através de Multicast,
// enviados pelos Downloaders. Para tal, deverão aplicar um protocolo
// de multicast fiável, uma vez que todos os storage barrels devem ter informação
// idêntica ainda que possam existir avarias de omissão.

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class IndexStorageBarrel extends UnicastRemoteObject implements Search, Runnable {

    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 4321;
    private final int id;

    private HashMap<String, SearchResult> knownUrls = new HashMap<>();
    private HashMap<String, Set<String>> parentUrls = new HashMap<>();
    private HashMap<String, Set<String>> wordsToUrls = new HashMap<>();


    public static class CustomComparator implements Comparator<SearchResult> {
        @Override
        public int compare(SearchResult sr1, SearchResult sr2) {
            try {
                if (sr1.parentUrls.size() == sr2.parentUrls.size())
                    return 0;
                return sr1.parentUrls.size() > sr2.parentUrls.size() ? -1 : 1;
            } catch (NullPointerException e){
                return 0;
            }
        }
    }
    @Override
    public ArrayList<SearchResult> search(String searchWords) throws RemoteException {
        // TODO: Ir buscar os 10 mais relevantes para a pesquisa
        HashSet<String> potentialresults = new HashSet<>();
        // Get all potentialResults form the words
        for(String s : searchWords.toLowerCase().split(" ")){
            Set<String> temp = wordsToUrls.get(s);
            if(temp != null)
                potentialresults.addAll(wordsToUrls.get(s));
        }
        // Get all the Search Results
        ArrayList<SearchResult> results = new ArrayList<>();
        for(String s : potentialresults){
            results.add(knownUrls.get(s));
        }
        // Sort them
        results.sort(new CustomComparator());
        return results;
    }

    public IndexStorageBarrel(int id) throws RemoteException {
        this.id = id;
    }

    public void run(){
        Registry r = null;
        ServerActions su = null;
        try {
            r = LocateRegistry.createRegistry(8000 + id);
            r.rebind("search", this);
            su = (ServerActions) LocateRegistry.getRegistry(7000).lookup("server");
            su.updateBarrelStatus(true, id, PORT);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        MulticastSocket socket = null;
        try{
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            String url = "", title = "", citation = "";
            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> urls = new ArrayList<>();

            //printBarrels();

            while(true){
                // Recieve packets
                byte[] buffer = new byte[1050];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String p = new String(packet.getData(), 0, packet.getLength());

                // Se o length for menor que 4, houve algum erro
                if(p.length() < 4)
                    continue;
                String[] stuff = p.split("\\|");
                // Verificar a primeira palavra (word ou link)
                // Words (wordX;"something")
                if(stuff[0].startsWith("word")){
                    for(String s : stuff){
                        String[] temp = s.split(";");
                        if(temp.length > 1)
                            words.add(temp[1]);
                    }
                }
                // Links (linkX;"something")
                else if (stuff[0].startsWith("link")){
                    for(String s : stuff){
                        String[] temp = s.split(";");
                        if(temp.length > 1)
                            urls.add(temp[1]);
                    }
                }
                // Header (url;"something"|title;"something"|citation;"something")
                else if (stuff[0].startsWith("url")){
                    if(!url.equals("")){
                        // TODO: Adicionar ao barrel
                        SearchResult sr = new SearchResult(url,title, citation);
                        // Update known urls
                        knownUrls.put(url, sr);
                        // Update child urls
                        for (String x : urls){
                            Set<String> temp = new HashSet<>();
                            // If already exists replace it
                            if(parentUrls.containsKey(x)){
                                temp = parentUrls.get(x);
                            }
                            // Add the url
                            temp.add(url);
                            parentUrls.put(x, temp);
                        }
                        // Update wordsToUrls
                        for(String x : words){
                            Set<String> temp = new HashSet<>();
                            // If already exists replace it
                            if(wordsToUrls.containsKey(x)){
                                temp = wordsToUrls.get(x);
                            }
                            // Add the url
                            temp.add(url);
                            wordsToUrls.put(x, temp);
                        }


                        // Atualizar a lista de parentUrls
                        if(parentUrls.containsKey(url))
                            knownUrls.get(url).parentUrls = new ArrayList<>(parentUrls.get(url));

                        words = new ArrayList<String>();
                        urls = new ArrayList<String>();
                        //writeBarrels();

                    }
                    // url
                    String[] temp = stuff[0].split(";");
                    if(temp.length > 1)
                        url = temp[1];
                    // title
                    temp = stuff[1].split(";");
                    if(temp.length > 1)
                        title = temp[1];
                    // citation
                    if(stuff[2].length() > 9)
                        citation = stuff[2].substring(9);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            try {
                su.updateBarrelStatus(false, id, PORT);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

}





















