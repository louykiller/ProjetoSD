// Downloader – São os componentes que obtêm as páginas Web em paralelo,
// as analisam (usando o jsoup) e atualizam o índice através de Multicast.

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
public class Downloader implements Runnable{
    private final List<String> urlsQueue;

    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 4321;
    private int id;



    public Downloader(List<String> urlsQueue, int id){
        this.urlsQueue = urlsQueue;
        this.id = id;
    }

    public void run() {
        // Iniciar socket
        MulticastSocket socket = null;
        // Enquanto houver urls na queue
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            while (true) {
                if (!urlsQueue.isEmpty()) {
                    // Ir buscar o proximo url
                    String url = urlsQueue.remove(0);
                    // TODO: Talvez verificar se o link ja foi visitado
                    HashSet<String> words = new HashSet<String>();
                    HashSet<String> urls = new HashSet<String>();
                    try {
                        // Aceder ao url
                        Document doc = Jsoup.connect(url).get();
                        // Titulo
                        String title = doc.title();
                        if (title.isEmpty()) {
                            throw new HttpStatusException("404 Page not found", 404, url);
                        }
                        // Retirar as palavras
                        StringTokenizer tokens = new StringTokenizer(doc.text());
                        while (tokens.hasMoreElements()) {
                            // Remover numeros e outros caracteres
                            String newToken = tokens.nextToken().toLowerCase().replaceAll("[^a-z]", "");
                            // Se sobrar algo, adicionar palavra
                            if (newToken.length() != 0)
                                words.add(newToken);
                        }
                        // Primeiro <p>
                        Elements paragraphs = doc.select("p:not(:has(#coordinates))");
                        String citation = "";
                        // Se houver um elemento <p> ir buscar o texto do primeiro
                        if (paragraphs.first() != null) {
                            citation = paragraphs.first().text();
                        }
                        // Retirar os links
                        Elements links = doc.select("a[href]");
                        for (Element link : links) {
                            // Adicionar link
                            urls.add(link.attr("abs:href"));
                        }

                        // Adicionar os links retirados ao fim da queue
                        urlsQueue.addAll(urls);

                        // MULTICAST
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        // header
                        String header = "url;" + url + "|title;" + title + "|citation;" + citation;
                        DatagramPacket packet = new DatagramPacket(header.getBytes(), header.getBytes().length, group, PORT);
                        socket.send(packet);
                        // Words
                        String wordsToSend = "";
                        int counter = 0;
                        for(String s : words){
                            if(counter % 5 == 0){
                                packet = new DatagramPacket(wordsToSend.getBytes(), wordsToSend.getBytes().length, group, PORT);
                                socket.send(packet);
                                wordsToSend = "";
                            }
                            wordsToSend += "word" + counter + ";" + s + "|";
                            counter++;
                        }
                        // Links
                        String linksToSend = "";
                        counter = 0;
                        for(String s : urls){
                            if(counter % 5 == 0){
                                packet = new DatagramPacket(linksToSend.getBytes(), linksToSend.getBytes().length, group, PORT);
                                socket.send(packet);
                                linksToSend = "";
                            }
                            linksToSend += "link" + counter + ";" + s + "|";
                            counter++;
                        }
                        //System.out.println(sr);


                    } catch (HttpStatusException e) {
                        System.out.println("Couldn't access '" + e.getUrl() + "'");
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("Error loading page");
                        e.printStackTrace();
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("Socket Error");
            e.printStackTrace();
        }
        finally {
            socket.close();
        }
    }
}
