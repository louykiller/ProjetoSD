// Downloader – São os componentes que obtêm as páginas Web em paralelo,
// as analisam (usando o jsoup) e atualizam o índice através de Multicast.

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
public class Downloader implements Runnable{
    private final List<String> urlsQueue;

    public Downloader(List<String> urlsQueue){
        this.urlsQueue = urlsQueue;
    }

    public void run() {
        // Enquanto houver urls na queue
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
                    // TODO: Mandar para os barrels o search result e as palavras e links retirados (com multicast)
                    SearchResult sr = new SearchResult(url, title, citation, words, urls);
                    System.out.println(sr);
                    //System.out.println("Total de palavras: " + words.size() + "\nTotal de links: " + urls.size());


                } catch (HttpStatusException e) {
                    System.out.println("Couldn't access '" + e.getUrl() + "'");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
