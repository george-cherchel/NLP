package co.inventorsoft;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CsvReadService {

    private CsvMapper csvMapper = new CsvMapper();

    public List<CsvWrapper> parse(String fileName) {
        List<CsvWrapper> result = new ArrayList<>();
        CsvSchema csvSchema = csvMapper.schemaFor(CsvWrapper.class).withHeader().withColumnReordering(true).withColumnSeparator(',');
        try (InputStream inputStream = new FileInputStream(fileName)) {
            MappingIterator<CsvWrapper> objectMappingIterator = csvMapper.readerFor(CsvWrapper.class).with(csvSchema).readValues(inputStream);
            while (objectMappingIterator.hasNext()) {
                result.add(objectMappingIterator.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        CsvReadService csvReadService = new CsvReadService();
        System.out.println(csvReadService.parse("src/main/resources/lead_titles_2016.csv").stream().distinct().count());
//        List<CsvWrapper> parsed = csvReadService.parse(file);
//        Files.write(Paths.get("src/main/resources/partners.csv"), //.filter(e -> "Partner".equals(e.getLeadType()))
//                parsed.stream().sorted().map(e -> e.getLeadType() + ", " + e.getTitle()).distinct().collect(Collectors.toList()));
//        Long count = parsed.stream().filter(e -> "Partner".equals(e.getLeadType())).collect(Collectors)

//        System.out.println(count);

//        for (int i = 0; i < 10; ++i) {
//            System.out.println(parsed.get(i));
//        }
    }
}
