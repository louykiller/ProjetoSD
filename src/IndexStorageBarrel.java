// Index Storage Barrel – É o servidor central (replicado) que armazena todos os
//dados da aplicação, recebendo os elementos do índice (palavras e URLs) através de Multicast,
// enviados pelos Downloaders. Para tal, deverão aplicar um protocolo
// de multicast fiável, uma vez que todos os storage barrels devem ter informação
// idêntica ainda que possam existir avarias de omissão.

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class IndexStorageBarrel extends UnicastRemoteObject implements Search, Runnable {

    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 4321;
    private long SLEEP_TIME = 15000;
    private int id;

    public HashMap<String,ArrayList<String>> words_a_m;
    public HashMap<String,ArrayList<String>> words_n_z;
    public HashMap<String,ArrayList<String>> url_a_m;
    public HashMap<String,ArrayList<String>> url_n_z;
    @Override
    public ArrayList<SearchResult> search(String searchWords) throws RemoteException {
        ArrayList<SearchResult> srs = new ArrayList<>();
        srs.add(new SearchResult("The url" , "The title", "The citation", null, null));
        return srs;
    }

    // Main class
    /**
     * @param nome_barrel nome do txt onde se vai estrair informação
     * @return retorna o barrel com a informação do txt
     * @throws IOException
     */
    public HashMap<String,ArrayList<String>> GetInfoBarrel(String nome_barrel) throws IOException{
        HashMap<String,ArrayList<String>> urls = new HashMap<String,ArrayList<String>>();

        ArrayList<String> list_aux;
        
        File barrel_1 = new File(nome_barrel);
        BufferedReader br1 = new BufferedReader(new FileReader(barrel_1));
        String st = "s";
        String [] st_aux;
        
        while ((st = br1.readLine()) != null){
            list_aux = new ArrayList<String>();
			st_aux = st.split(",");
            
            for(int i = 1 ; i<st_aux.length ; i++){
                list_aux.add(st_aux[i]);
            }

            urls.put(st_aux[0],list_aux);
        }

        br1.close();
        return urls;
    }

    /**
     * 
     * @param barrel barrel onde está guardada a informação 
     * @param searchedWord palavra procurada 
     * @param searchedUrl url procurado
     * @return urls com o novo adicionado
     * @throws IOException
     */
    public HashMap<String,String> AddToHash(HashMap<String,String> barrel,String searchedWord,String searchedUrl) throws IOException{
        HashMap<String,String> urls = new HashMap<String,String>();
        urls = barrel;
        barrel.put(searchedWord, searchedUrl);
        return urls;
    }

    public void addToUrl(String url, String word){
        char firstChar = word.charAt(0);
        char upperFirst = Character.toUpperCase(firstChar);

        if(Character.compare(upperFirst, 'N') < 0){
            if(this.url_a_m.containsKey(url)){
                this.url_a_m.get(url).add(word);
            }
            else{
                ArrayList<String> newArray = new ArrayList<String>();
                newArray.add(word);
                this.url_a_m.put(url,newArray);
            }
        }
        else{
            if(this.url_n_z.containsKey(url)){
                this.url_n_z.get(url).add(word);
            }
            else{
                ArrayList<String> newArray = new ArrayList<String>();
                newArray.add(word);
                this.url_n_z.put(url,newArray);
            }
        }
    }
    public  void addToWord(String key_word,String value_word){
        char firstChar = value_word.charAt(0);
        char upperFirst = Character.toUpperCase(firstChar);

        if(Character.compare(upperFirst, 'N') < 0){
            if(this.words_a_m.containsKey(key_word)){
                this.words_a_m.get(key_word).add(value_word);
            }
            else{
                ArrayList<String> newArray = new ArrayList<String>();
                newArray.add(value_word);
                this.words_a_m.put(key_word,newArray);
            }
        }
        else{
            if(this.words_n_z.containsKey(key_word)){
                this.words_n_z.get(key_word).add(key_word);
            }
            else{
                ArrayList<String> newArray = new ArrayList<String>();
                newArray.add(value_word);
                this.words_n_z.put(key_word,newArray);
            }
        }
    }

    // Para separar barrel no futuro ignorar por enquanto

    
    /*
    char firstChar = fileName.charAt(0);
    char upperFirst = Character.toUpperCase(firstChar);
    String fullUrl = "https://"+fileName;
    
    try{
            if(Character.compare(upperFirst, 'M') < 0){
                System.out.println("Insert in first barrel!");
                BufferedWriter bw1 = new BufferedWriter(new FileWriter("a-m_barrel.txt",true));
                bw1.write("\n"+fullUrl);
                bw1.close();
            }
            else{
                System.out.println("Insert in the second barrel!");
                BufferedWriter bw2 = new BufferedWriter(new FileWriter("n-z_barrel.txt",true));
                bw2.write("\n"+fullUrl);
                bw2.close();
            }
        }
        catch(IOException exception){
            exception.printStackTrace();
        }
        */

    public HashMap<String,String> createInverted(HashMap<String,ArrayList<String>> urls){
        HashMap<String,String> inverted = new HashMap<String,String>();
        String url;

        for (Entry<String, ArrayList<String>> set : urls.entrySet()) {
            url = set.getKey();
            for (String x : set.getValue()){
                inverted.put(x,url);
            }
        }

        return inverted;
    }

    public IndexStorageBarrel(int id) throws RemoteException {
        this.id = id;
        this.words_a_m = new HashMap<String,ArrayList<String>>();
        this.words_n_z = new HashMap<String,ArrayList<String>>();
        this.url_a_m = new HashMap<String,ArrayList<String>>();
        this.url_n_z = new HashMap<String,ArrayList<String>>();
    }

    private ArrayList<HashMap<String, ArrayList<String>>> SplitBarrels(HashMap<String, ArrayList<String>> words_barrel,HashMap<String, ArrayList<String>> url_barrel) {
        ArrayList<HashMap<String, ArrayList<String>>> out_list = new ArrayList<HashMap<String, ArrayList<String>>>();
        String url;

        
        char firstChar,upperFirst;

        for (Entry<String, ArrayList<String>> set : words_barrel.entrySet()) {
            ArrayList<String> word_aux = new ArrayList<String>();
            ArrayList<String> word_aux2 = new ArrayList<String>();

            url = set.getKey();

            for (String x : set.getValue()){
                firstChar = x.charAt(0);
                upperFirst = Character.toUpperCase(firstChar);
        
                if(Character.compare(upperFirst, 'N') < 0){
                    word_aux.add(x);
                }
                else{
                    word_aux2.add(x);
                }
    
            }

            System.out.println("===========");
            for (String x : word_aux)
                System.out.println(x);

            System.out.println("===========");
            for (String x : word_aux2)
                System.out.println(x);

            System.out.println("===========");

            words_a_m.put(url, word_aux);
            words_n_z.put(url, word_aux2);

        }

        for (Entry<String, ArrayList<String>> set : url_barrel.entrySet()) {
            ArrayList<String> url_aux = new ArrayList<String>();
            ArrayList<String> url_aux2 = new ArrayList<String>();

            url = set.getKey();

            for (String x : set.getValue()){
                firstChar = x.charAt(0);
                upperFirst = Character.toUpperCase(firstChar);
        
                if(Character.compare(upperFirst, 'M') < 0){
                    url_aux.add(x);
                }
                else{
                    url_aux2.add(x);
                }
    
            }

            this.url_a_m.put(url, url_aux);
            this.url_n_z.put(url, url_aux2);
        }

        out_list.add(this.words_a_m);
        out_list.add(this.words_n_z);
        out_list.add(this.url_a_m);
        out_list.add(this.url_n_z);
        return out_list;
    }

	public void run(){
        Registry r = null;
        try {
            r = LocateRegistry.createRegistry(8000 + id);
            r.rebind("search", this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        MulticastSocket socket = null;
        try{

            ArrayList<HashMap<String,ArrayList<String>>> spliter_helper = new ArrayList<HashMap<String,ArrayList<String>>>();
            
            HashMap<String,ArrayList<String>> words_barrel = new HashMap<String,ArrayList<String>>();

            HashMap<String,ArrayList<String>> url_barrel = new HashMap<String,ArrayList<String>>();

            //HashMap<String,String> inverted = new HashMap<String,String>();

            // word -> url
            words_barrel = GetInfoBarrel("a-m_barrel.txt");
            url_barrel = GetInfoBarrel("n-z_barrel.txt");
            
            spliter_helper = SplitBarrels(words_barrel,url_barrel);
            
            this.words_a_m = spliter_helper.get(0);
            this.words_n_z = spliter_helper.get(1);
            this.url_a_m = spliter_helper.get(2);
            this.url_n_z = spliter_helper.get(3);


            //addToUrl("https://teste.com","aaaaaa");
            //addToUrl("https://teste.com","pppppp");
            //addToWord("aaaaa","aaaaaa");
            //addToWord("aaaaaaaa","ppppppppp");

            System.out.println("==== Info in the words a__ barrel =====");
            for (Entry<String, ArrayList<String>> set : words_a_m.entrySet()) {
                System.out.println("Url " + set.getKey() +" contem as seguintes palavras");
                for (String x : set.getValue()){
                    System.out.println(x);
                }
            }

            System.out.println("==== Info in the words n_z barrel =====");
            for (Entry<String, ArrayList<String>> set : words_n_z.entrySet()) {
                System.out.println("Url " + set.getKey() +" contem as seguintes palavras");
                for (String x : set.getValue()){
                    System.out.println(x);
                }
            }

            System.out.println("==== Info in the url a__ barrel =====");
            for (Entry<String, ArrayList<String>> set : url_a_m.entrySet()) {
                System.out.println("Url " + set.getKey() +" contem as seguintes palavras");
                for (String x : set.getValue()){
                    System.out.println(x);
                }
            }

            System.out.println("==== Info in the url n_z barrel =====");
            for (Entry<String, ArrayList<String>> set : url_n_z.entrySet()) {
                System.out.println("Url " + set.getKey() +" contem as seguintes palavras");
                for (String x : set.getValue()){
                    System.out.println(x);
                }
            }
            //inverted = createInverted(barrel);

            /*for (Entry<String, String> set : inverted.entrySet()) {
                System.out.println("Palavra " + set.getKey() + " associado ao seguinte url " + set.getValue());
            }*/

            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            String url = "", title = "", citation = "";
            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> urls = new ArrayList<>();

            while(true){
                // Recieve packets
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String p = new String(packet.getData(), 0, packet.getLength());
                // Se o length for menor que 4, houve algum erro
                if(p.length() < 4)
                    continue;
                String[] stuff = p.split("\\|");
                // Verificar a primeira palavra (word ou link)
                // Words
                if(stuff[0].startsWith("word")){
                    for(String s : stuff){
                        String[] temp = s.split(";");
                        if(temp.length > 1)
                            words.add(temp[1]);
                    }
                }
                // Links
                else if (stuff[0].startsWith("link")){
                    for(String s : stuff){
                        String[] temp = s.split(";");
                        if(temp.length > 1)
                            urls.add(temp[1]);
                    }
                }
                // Header
                else if (stuff[0].startsWith("url")){
                    if(!url.equals("")){
                        // TODO: Adicionar ao barrel
                        SearchResult sr = new SearchResult(url, title, citation, words, urls);
                        System.out.println(sr + "\n" + words.size() + " words and " + urls.size() + " links\n");
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
                    temp = stuff[2].split(";");
                    if(temp.length > 1)
                        citation = temp[1];
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
