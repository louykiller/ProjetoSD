import java.io.Serializable;

public class SearchResult implements Serializable {
    public String url;
    public int relevance;
    public String title;
    public String citation;

    public SearchResult(String url, String title, String citation) {
        this.url = url;
        this.relevance = 1;
        this.title = title;
        this.citation = citation;
    }

    public String toString(){
        return title + '\n' + url + '\n' + citation;
    }
}
