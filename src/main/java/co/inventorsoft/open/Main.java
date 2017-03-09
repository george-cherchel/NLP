package co.inventorsoft.open;

import co.inventorsoft.CsvReadService;
import co.inventorsoft.CsvWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        CsvReadService csvReadService = new CsvReadService();
        File trainingFilePath = new File("src/main/resources/lead_titles_2016.csv");
        List<CsvWrapper> parsed = csvReadService.parse(trainingFilePath);
        File outputFile = new File("src/main/resources/res.model");
        if (!outputFile.exists()) {
            outputFile.createNewFile();
//            OutputStream outputStream = new FileOutputStream(outputFile);
//            outputStream.write(parsed.stream().map(e -> e.getTitle() + " " + e.getLeadType()).collect(Collectors.joining("\n")).getBytes());
//            outputStream.flush();
//            outputStream.close();
        }



//        ObjectStream<DocumentSample> objectStream = new DocumentSampleStream()

//        ObjectStream os = new CollectionObjectStream(parsed);
////        DoccatModel doccatModel = DocumentCategorizerME.train("en", os, TrainingParameters.defaultParams(), );
//        os.close();
////        DocumentCategorizerME.train("en", )
//
//        try (ObjectStream<CsvWrapper> os = new CollectionObjectStream<>(parsed)) {
//            System.out.println(os.read());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
