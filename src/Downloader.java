// Downloader – São os componentes que obtêm as páginas Web em paralelo,
// as analisam (usando o jsoup) e atualizam o índice através de Multicast.

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;
public class Downloader implements Runnable{
    private String url;
    Thread t;
    HashSet<String> words;
    HashSet<String> urls;

    public Downloader(String url){
        this.url = url;
        this.words = new HashSet<String>();
        this.urls = new HashSet<String>();
        t = new Thread(this, url);
        t.start();
    }

    public void run() {
        try {


            // PARA A CITAÇÃO ENCONTRAR O PRIMEIRO <P>



            // Aceder ao url
            Document doc = Jsoup.connect(this.url).get();
            // Titulo
            String title = doc.title();
            System.out.println(title);
            // Retirar as palavras
            StringTokenizer tokens = new StringTokenizer(doc.text());
            int countTokens = 0;
            while (tokens.hasMoreElements()) {
                String newToken = tokens.nextToken().toLowerCase().replaceAll("[^a-z]", "");
                if(newToken.length() != 0) {
                    words.add(newToken);
                    //System.out.println(words.get(countTokens));
                    countTokens++;
                }
            }

            System.out.println(countTokens);
            // Retirar os links
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                urls.add(link.attr("abs:href"));
                //System.out.println(link.text() + "\n" + link.attr("abs:href") + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String w : words){
            System.out.println(w);
        }
        for(String u : urls){
            //System.out.println(u);
        }

    }
}
