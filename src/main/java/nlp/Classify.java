package nlp;

import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.util.ErasureUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Classify {

    public static final String NON = "non";
    public static final String CLIENT = "client";
    private ColumnDataClassifier cdc;
    private LinearClassifier<String, String> classifier;

    /**
     * @param trainedModelPath location of trained model
     */
    public Classify(String trainedModelPath) {
        cdc = new ColumnDataClassifier("src/main/resources/lead.prop");
        initClassifier(trainedModelPath);
    }

    /**
     * Create trained model and save it in file if not exist file
     * If exist then load from file
     * @param trainedModelPath
     */
    private void initClassifier(String trainedModelPath) {
        if (!(new File(trainedModelPath).exists())) {
            train();
            saveClassifier(trainedModelPath);
        } else {
            getClassifier(trainedModelPath);
        }
    }

    /**
     * Find most probably class name for title
     * @param title
     * @return name of class
     */
    public String classOf(String title) {
        Datum<String, String> datum = cdc.makeDatumFromStrings(new String[]{"", title});
        return classifier.classOf(datum);
    }

    /**
     * @param title - of occupation
     * @param className - interested class (CLIENT, NON)
     * @return probability of title contains in this class
     */
    public double probabilityOfClass(String title, String className) {
        Datum<String, String> datum = cdc.makeDatumFromStrings(new String[]{"", title});
        return classifier.probabilityOf(datum).getCount(className);
    }

    /**
     * Training model from training files with clients and nonClients
     */
    private void train() {
        List<String> clients = null;
        List<String> nonClients = null;
        try {
            clients = Files.readAllLines(Paths.get("src/main/resources/clients.csv"));
            nonClients = Files.readAllLines(Paths.get("src/main/resources/nonClients.csv"));
        } catch (IOException e) {
            log.warn("Exception while reading training data. Reason: ", e);
        }
        GeneralDataset<String, String> generalDataset = new Dataset<>();
        generalDataset.addAll(makeTrainingData(clients, CLIENT));
        generalDataset.addAll(makeTrainingData(nonClients, NON));
        classifier = (LinearClassifier<String, String>) cdc.makeClassifier(generalDataset);
    }

    /**
     * @param data list of titles
     * @param className name of class
     * @return data for API
     */
    private List<Datum<String, String>> makeTrainingData(List<String> data, String className) {
        return data.stream().map(e -> cdc.makeDatumFromStrings(new String[]{className, e})).collect(Collectors.toList());
    }

    /**
     * Save trained model in file
     * @param destinationFile
     */
    private void saveClassifier(String destinationFile) {
        try (OutputStream outputStream = new FileOutputStream(destinationFile)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(classifier);
            objectOutputStream.close();
        } catch (IOException e) {
            log.warn("Exception while create and save classifier. Reason: ", e);
        }
    }

    /**
     * Load trained model from file
     * @param trainedFile
     */
    private void getClassifier(String trainedFile) {
        try (InputStream fileInputStream = new FileInputStream(trainedFile)) {
            ObjectInputStream ois = new ObjectInputStream(fileInputStream);
            classifier = ErasureUtils.uncheckedCast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Exception while get trained model. Reason: ", e);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Classify classify = new Classify("src/main/resources/trainedModel");
        List<String> tests = Files.readAllLines(Paths.get("src/main/resources/test.csv"));
        tests.forEach(e -> System.out.println(e + "  ==>  " + classify.classOf(e)));
    }
}
