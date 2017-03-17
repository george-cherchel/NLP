package nlp;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"title"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsvWrapper implements Comparable<CsvWrapper> {

    @JsonProperty("Title")
    public String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int compareTo(CsvWrapper o) {
        return title.compareTo(o.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title);
    }

    @Override
    public boolean equals(Object obj) {
        CsvWrapper other = (CsvWrapper) obj;
        return title.equals(other.getTitle());
    }
}
