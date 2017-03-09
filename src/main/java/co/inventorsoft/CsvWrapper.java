package co.inventorsoft;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonPropertyOrder({"Title", "Lead Type"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsvWrapper {

    @JsonProperty("Title")
    public String title;

    @JsonProperty("Lead Type")
    public String leadType;
}
