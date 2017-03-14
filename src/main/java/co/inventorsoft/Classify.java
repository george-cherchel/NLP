package co.inventorsoft;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.util.ErasureUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Classify {

    private void createClassifier(ColumnDataClassifier cdc, File destinationFile, GeneralDataset generalDataset) throws IOException {
        Classifier<String, String> cl = cdc.makeClassifier(generalDataset);
        try (OutputStream outputStream = new FileOutputStream(destinationFile)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(cl);
            objectOutputStream.close();
        }
    }

    private Classifier getClassifier(File trainedFile) {
        try (InputStream fileInputStream = new FileInputStream(trainedFile)) {
            ObjectInputStream ois = new ObjectInputStream(fileInputStream);
            return ErasureUtils.uncheckedCast(ois.readObject());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> getClientTitles(List<String> files) {
        CsvReadService csvReadService = new CsvReadService();
        List<String> res = new ArrayList<>();
        files.forEach(e -> csvReadService.parse(e).stream().map(CsvWrapper::getTitle).forEach(res::add));
        return res;
    }

    private List<Datum<String, String>> makeTrainingData(ColumnDataClassifier cdc, List<String> data, String className) {
        return data.stream().map(e -> cdc.makeDatumFromStrings(new String[] {className, e})).collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Classify classify = new Classify();
        ColumnDataClassifier cdc = new ColumnDataClassifier("src/main/resources/lead.prop");
        File trainedFile = new File("src/main/resources/trainedModel");
        if (!trainedFile.exists()) {
            List<String> files = new ArrayList<>();
            files.add("src/main/resources/lead_titles_2016.csv");
            List<String> nonClients = Files.readAllLines(Paths.get("src/main/resources/occupations.csv"));
            GeneralDataset<String, String> generalDataset = new Dataset<>();
            List<String> clients = classify.getClientTitles(files);
            generalDataset.addAll(classify.makeTrainingData(cdc, clients, "client"));
            generalDataset.addAll(classify.makeTrainingData(cdc, nonClients, "non"));
            classify.createClassifier(cdc, trainedFile, generalDataset);
        }
        Classifier cl = classify.getClassifier(trainedFile);
        trainedFile.delete();
        List<String> tests = Files.readAllLines(Paths.get("src/main/resources/test.csv"));
        tests.forEach(e -> {
            Datum<String, String> d = cdc.makeDatumFromStrings(new String[] {"", e});
            System.out.println(e + "  ==>  " + cl.classOf(d));
        });
    }
}
