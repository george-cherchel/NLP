package co.inventorsoft.stanford;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.LinearClassifier;
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

public class Classify {

    private static String trainedFileModel = "src/main/resources/traindedModel";
    private static String trainingExampleFile = "";

    public static void main(String[] args) throws IOException {
//        ColumnDataClassifier cdc = new ColumnDataClassifier("src/main/resources/lead.prop");
//        Classifier<String, String> cl =
//                cdc.makeClassifier(cdc.readTrainingExamples("src/main/resources/lead_titles_2016.csv"));
//                cdc.makeClassifier(cdc.readTrainingExamples("src/main/resources/leads.csv"));
        File file = new File(trainedFileModel);
        if (!file.exists()) {
            serialization(trainedFileModel);
        }


//        for (String line : ObjectBank.getLineIterator("src/main/resources/test.csv", "utf-8")) {
//            Datum<String, String> d = cdc.makeDatumFromLine(line);
//            System.out.println(line + "  ==>  " + cl.classOf(d));
//        }
    }

    private static void serialization(String destinationFileName) throws IOException {
        ColumnDataClassifier cdc = new ColumnDataClassifier("src/main/resources/lead.prop");
        Classifier<String, String> cl =
                cdc.makeClassifier(cdc.readTrainingExamples("src/main/resources/leads.csv"));
        try (OutputStream outputStream = new FileOutputStream(destinationFileName)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(cl);
            objectOutputStream.close();
        }
    }

    private static ColumnDataClassifier getClassifier(String trainedFileModel) throws IOException, ClassNotFoundException {
        try (InputStream fileInputStream = new FileInputStream(trainedFileModel)) {
            ObjectInputStream ois = new ObjectInputStream(fileInputStream);
            return ErasureUtils.uncheckedCast(ois.readObject());
        }
    }
}
