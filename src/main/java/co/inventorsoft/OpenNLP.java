package co.inventorsoft;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.maxent.GISTrainer;
import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.ml.perceptron.SimplePerceptronSequenceTrainer;
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

    private void train(String doccatModelFileName) throws IOException {

//        TrainingParameters params = TrainingParameters.defaultParams();

        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(0));
//        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(1000));

        params.put(TrainingParameters.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);
//        params.put(TrainingParameters.ALGORITHM_PARAM, QNTrainer.MAXENT_QN_VALUE);
//        params.put(TrainingParameters.ALGORITHM_PARAM, GISTrainer.MAXENT_VALUE);
//        params.put(TrainingParameters.TRAINER_TYPE_PARAM, NaiveBayesTrainer.EVENT_VALUE);
//        params.put(TrainingParameters.ALGORITHM_PARAM, PerceptronTrainer.PERCEPTRON_VALUE);
//        params.put(TrainingParameters.ALGORITHM_PARAM, SimplePerceptronSequenceTrainer.PERCEPTRON_SEQUENCE_VALUE);

        List<String> files = new ArrayList<>();
        files.add("src/main/resources/lead_titles_2016.csv");
        List<String> clients = getClientTitles(files);
        List<String> nonClients = Files.readAllLines(Paths.get("src/main/resources/occupations.csv"));

        DocumentSample nonClientsData = new DocumentSample("non", nonClients.stream().map(String::valueOf).toArray(String[]::new));
        DocumentSample clientsData = new DocumentSample("client", clients.subList(0, 1230).stream().map(String::valueOf).toArray(String[]::new));
        List<DocumentSample> data = new ArrayList<>();
        data.add(clientsData);
        data.add(nonClientsData);

        ObjectStream<DocumentSample> inputStream = new CollectionObjectStream<>(data);
        DoccatModel doccatModel = DocumentCategorizerME.train("en", inputStream, params, new DoccatFactory());
        try (OutputStream outDoccatModel = new FileOutputStream(doccatModelFileName)) {
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
        String doccatModelFileName = "src/main/resources/trainedModel";
        OpenNLP openNLP = new OpenNLP();
        openNLP.train(doccatModelFileName);
        openNLP.test(doccatModelFileName, tests);
        new File("src/main/resources/trainedModel").delete();
    }
}
