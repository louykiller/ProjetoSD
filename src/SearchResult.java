import java.io.Serializable;
import java.util.ArrayList;

public class SearchResult implements Serializable {
    public String url;
    public int relevance;
    public String title;
    public String citation;
    public ArrayList<String> words;
    public ArrayList<String> urls;

    public SearchResult(String url, String title, String citation, ArrayList<String> words, ArrayList<String> urls) {
        this.url = url;
        this.relevance = 1;
        this.title = title;
        this.citation = citation;
        this.words = words;
        this.urls = urls;
    }

    public String toString(){
        return title + '\n' + url + '\n' + citation;
    }
}
