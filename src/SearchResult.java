import java.io.Serializable;

public class SearchResult implements Serializable {
    private String url;
    private int relevence;
    private String title;
    private String citation;

    public SearchResult(String url, String title, String citation) {
        this.url = url;
        this.relevence = 1;
        this.title = title;
        this.citation = citation;
    }

    public String toString(){
        return title + '\n' + url + '\n' + citation;
    }
}
