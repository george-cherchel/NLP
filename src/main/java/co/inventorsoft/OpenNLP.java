package co.inventorsoft;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenNLP {

    private List<String> getClientTitles(List<String> files) {
        CsvReadService csvReadService = new CsvReadService();
        List<String> res = new ArrayList<>();
        files.forEach(e -> csvReadService.parse(e).stream().map(CsvWrapper::getTitle).forEach(res::add));
        return res;
    }

    private void train(String docatModelFileName) throws IOException {
        List<String> files = new ArrayList<>();
        files.add("src/main/resources/lead_titles_2016.csv");
        List<String> clients = getClientTitles(files);
        List<String> nonClients = Files.readAllLines(Paths.get("src/main/resources/occupations.csv"));

        DocumentSample nonClientsData = new DocumentSample("non", nonClients.stream().map(String::valueOf).toArray(String[]::new));
        DocumentSample clientsData = new DocumentSample("clients", clients.stream().map(String::valueOf).toArray(String[]::new));
        List<DocumentSample> data = new ArrayList<>();
        data.add(nonClientsData);
        data.add(clientsData);

        ObjectStream<DocumentSample> inputStream = new CollectionObjectStream<>(data);
        DoccatModel doccatModel = DocumentCategorizerME.train("en", inputStream, TrainingParameters.defaultParams(), new DoccatFactory());
        try (OutputStream outDoccatModel = new FileOutputStream(docatModelFileName)) {
            doccatModel.serialize(outDoccatModel);
        }
    }

    private void test(String doccatModelFileName, List<String> tests) {
        DoccatModel doccatModel = null;
        try {
            doccatModel = new DoccatModel(new File(doccatModelFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DocumentCategorizerME categorizerME = new DocumentCategorizerME(doccatModel);
        tests.forEach(e -> {
            double[] classDistribution = categorizerME.categorize(e);
            System.out.println(Arrays.toString(classDistribution));
            System.out.println(e + " ==> " + categorizerME.getBestCategory(classDistribution));
        });
    }

    public static void main(String[] args) throws IOException {
        List<String> tests = Files.readAllLines(Paths.get("src/main/resources/test.csv"));
        String doccatModelFileName = "src/main/resources/traindedModel";
        OpenNLP openNLP = new OpenNLP();
        openNLP.train(doccatModelFileName);
        openNLP.test(doccatModelFileName, tests);
    }
}
