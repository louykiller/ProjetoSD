import java.util.Objects;

public class ResultadoPesquisa {
    private String URL;
    private int relevence;
    private String title;
    private String citation;

    public ResultadoPesquisa(String URL, int relevence, String title, String citation) {
        this.URL = URL;
        this.relevence = relevence;
        this.title = title;
        this.citation = citation;
    }

    public ResultadoPesquisa(String URL) {
        this.URL = URL;
        this.relevence = 1;
    }

    @Override
    public String toString() {
        return  title + '\n' + URL + '\n' + citation + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultadoPesquisa that = (ResultadoPesquisa) o;
        return relevence == that.relevence && URL.equals(that.URL) && Objects.equals(title, that.title) && Objects.equals(citation, that.citation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(URL, relevence, title, citation);
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getRelevence() {
        return relevence;
    }

    public void setRelevence(int relevence) {
        this.relevence = relevence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }
}
