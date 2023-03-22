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
            // Aceder ao url
            Document doc = Jsoup.connect(this.url).get();
            // Titulo
            String title = doc.title();
            // Retirar as palavras
            StringTokenizer tokens = new StringTokenizer(doc.text());
            while (tokens.hasMoreElements()) {
                // Remover numeros e outros caracteres
                String newToken = tokens.nextToken().toLowerCase().replaceAll("[^a-z]", "");
                // Se sobrar algo, adicionar palavra
                if(newToken.length() != 0)
                    words.add(newToken);
            }
            // Primeiro <p>
            Elements paragraphs = doc.select("p:not(:has(#coordinates))");
            String citation = paragraphs.first().text();
            // Retirar os links
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                // Adicionar link
                urls.add(link.attr("abs:href"));
            }

            // TODO: Mandar para os barrels o search result e as palavras e links retirados (com multicast)
            SearchResult sr = new SearchResult(url, title, citation);
            System.out.println(sr);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
