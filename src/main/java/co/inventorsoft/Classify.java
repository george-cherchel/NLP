package co.inventorsoft;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.WeightedRVFDataset;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.BasicDocument;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.ling.Document;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.objectbank.ObjectBank;
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
import java.util.Collections;
import java.util.List;

public class Classify {

    private static String trainedFileModel = "src/main/resources/traindedModel";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        ColumnDataClassifier cdc = new ColumnDataClassifier("src/main/resources/lead.prop");
        ColumnDataClassifier cdc = new ColumnDataClassifier("src/main/resources/examples/cheese2007.prop");

        File trainedFile = new File(trainedFileModel);
        if (!trainedFile.exists()) {
            serialization(cdc, trainedFileModel);
        }
        Classifier cl = getClassifier(trainedFileModel);
        System.out.println(cl);
        trainedFile.delete();
        List<String> tests = Files.readAllLines(Paths.get("src/main/resources/test.csv"));
        tests.forEach(e -> {
            Datum<String, String> d = cdc.makeDatumFromStrings(new String[] {"client", e});
            System.out.println(e + "  ==>  " + cl.scoresOf(d));
        });
    }

    private static void serialization(ColumnDataClassifier cdc, String destinationFileName) throws IOException {
        List<String> files = new ArrayList<>();
        files.add("src/main/resources/lead_titles_2016.csv");

//        GeneralDataset<String, String> rvfData = cdc.readTrainingExamples("src/main/resources/leads.csv");
//        Classifier<String, String> stringStringClassifier = cdc.makeClassifier(rvfData);

        GeneralDataset<String, String> generalDataset = new Dataset();
        List<String> clients = getClientTitles(files);
        List<String> nonClients = Files.readAllLines(Paths.get("src/main/resources/occupations.csv"));
        generalDataset.add(new BasicDatum(clients, "clients"));
        generalDataset.add(new BasicDatum(nonClients, "non"));
        Classifier<String, String> cl = cdc.makeClassifier(generalDataset);
        try (OutputStream outputStream = new FileOutputStream(destinationFileName)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(cl);
            objectOutputStream.close();
        }
    }

    private static Classifier getClassifier(String trainedFileModel) {
        try (InputStream fileInputStream = new FileInputStream(trainedFileModel)) {
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

    private static List<String> getClientTitles(List<String> files) {
        CsvReadService csvReadService = new CsvReadService();
        List<String> res = new ArrayList<>();
        files.forEach(e -> csvReadService.parse(e).stream().map(CsvWrapper::getTitle).forEach(res::add));
        return res;
    }
}
