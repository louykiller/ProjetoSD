import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class MulticastClient extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 4321;

    public static void main(String[] args) {
        MulticastClient client = new MulticastClient();
        client.start();
    }

    public void run() {
        MulticastSocket socket = null;
        String [] st_aux;
                
        try {
            HashMap<String,String> urls = new HashMap<String,String>();
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                
                if(Character.isDigit(message.charAt(0))){
                    System.out.println("Recebido o numero: "+ message);
                    System.out.println("====== Informação presente no hashmap ======");
                    for (Map.Entry<String, String> set : urls.entrySet()) {
                        System.out.println(set.getKey() + " " + set.getValue());
                    }
                    urls = new HashMap<String,String>();
                    System.out.println("===============================================");
                }

                else{
                    System.out.println("Recebido a mensage: " + message);
                    st_aux = message.split(" ");
                    urls.put(st_aux[0],st_aux[1]);
                }
        
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
