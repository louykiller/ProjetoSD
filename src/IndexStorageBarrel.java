// Index Storage Barrel – É o servidor central (replicado) que armazena todos os
//dados da aplicação, recebendo os elementos do índice (palavras e URLs) através de Multicast,
// enviados pelos Downloaders. Para tal, deverão aplicar um protocolo
// de multicast fiável, uma vez que todos os storage barrels devem ter informação
// idêntica ainda que possam existir avarias de omissão.
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class IndexStorageBarrel extends Thread{

    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 4321;
    private long SLEEP_TIME = 15000;

    // Main class
    /**
     * @param nome_barrel nome do txt onde se vai estrair informação
     * @return retorna o barrel com a informação do txt
     * @throws IOException
     */
    public static HashMap<String,String> GetInfoBarrel(String nome_barrel) throws IOException{
        HashMap<String,String> urls = new HashMap<String,String>();

        File barrel_1 = new File(nome_barrel);
        BufferedReader br1 = new BufferedReader(new FileReader(barrel_1));
        String st = "s";
        String [] st_aux;
        
        while ((st = br1.readLine()) != null){
			st_aux = st.split(",");
            urls.put(st_aux[0],st_aux[1]);
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
    public static HashMap<String,String> AddToHash(HashMap<String,String> barrel,String searchedWord,String searchedUrl) throws IOException{
        HashMap<String,String> urls = new HashMap<String,String>();
        urls = barrel;
        barrel.put(searchedWord, searchedUrl);
        return urls;
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

    public IndexStorageBarrel() {
        super("Server " + (long) (Math.random() * 1000));
    }
    public static void main(String[] args) {
        IndexStorageBarrel server = new IndexStorageBarrel();
        server.start();
    }

	public void run(){
        MulticastSocket socket = null;
        try{
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            HashMap<String,String> barrel = new HashMap<String,String>();
            HashMap<String,String> barrel2 = new HashMap<String,String>();
            
            while(true){
                barrel = GetInfoBarrel("a-m_barrel.txt");
                barrel2 = GetInfoBarrel("n-z_barrel.txt");
                barrel.putAll(barrel2);

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                
                String message = String.valueOf(barrel.size());
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                for (Map.Entry<String, String> set : barrel.entrySet()) {
                    message = set.getKey() + " " + set.getValue();
                    buffer = message.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                }
    
                //barrel_merged = AddToHash(barrel_merged);
                //System.out.println(barrel_merged);;

                try { 
                    sleep(SLEEP_TIME); 
                } 
                catch (InterruptedException e) { 

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
