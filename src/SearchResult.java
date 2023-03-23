import java.io.Serializable;
import java.util.HashSet;

public class SearchResult implements Serializable {
    public String url;
    public int relevence;
    public String title;
    public String citation;
    HashSet<String> words;
    HashSet<String> urls;

    public SearchResult(String url, String title, String citation, HashSet<String> words, HashSet<String> urls) {
        this.url = url;
        this.relevence = 1;
        this.title = title;
        this.citation = citation;
        this.words = words;
        this.urls = urls;
    }

    public String toString(){
        return title + '\n' + url + '\n' + citation;
    }
}
