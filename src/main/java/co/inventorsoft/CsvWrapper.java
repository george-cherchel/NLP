package co.inventorsoft;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@JsonPropertyOrder({"Title", "Lead Type"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsvWrapper implements Comparable<CsvWrapper> {

    @JsonProperty("Title")
    public String title;

    @JsonProperty("Lead Type")
    public String leadType;

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
