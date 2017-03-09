package co.inventorsoft;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CsvReadService {

    private CsvMapper csvMapper = new CsvMapper();

    public List<CsvWrapper> parse(File file) {
        List<CsvWrapper> result = new ArrayList<>();
        CsvSchema csvSchema = csvMapper.schemaFor(CsvWrapper.class).withHeader().withColumnReordering(true).withColumnSeparator(',');
        try (InputStream inputStream = new FileInputStream(file)) {
            MappingIterator<CsvWrapper> objectMappingIterator = csvMapper.readerFor(CsvWrapper.class).with(csvSchema).readValues(inputStream);
            while (objectMappingIterator.hasNext()) {
                result.add(objectMappingIterator.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        CsvReadService csvReadService = new CsvReadService();
        File file = new File("src/main/resources/lead_titles_2016.csv");
        List<CsvWrapper> parsed = csvReadService.parse(file);
        Long count = parsed.stream().filter(e -> "Partner".equals(e.getLeadType())).count();
        System.out.println(count);

//        for (int i = 0; i < 10; ++i) {
//            System.out.println(parsed.get(i));
//        }
    }
}
