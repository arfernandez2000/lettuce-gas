package utils;

import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FileGenerator {

    private static final int UPPER_I = 199;
    private static final int UPPER_J = 99;
    private static final int LOWER = 1;

    public static String outFileName;

    public static void main(String[] args) {
        int N = ConsoleParser.parseArgumentsFileGenerator(args);
        HashMap<MutablePair<Integer, Integer>, Integer> cells = new HashMap<>();

        for (int k = 0; k < N; k++) {
            int i = (int) (Math.random() * (UPPER_I - LOWER)) + LOWER;
            int j = (int) (Math.random() * (UPPER_J - LOWER)) + LOWER;

            Random random = new Random();
            int direction = random.nextInt(6);
            if(cells.containsKey(new MutablePair<>(i, j))){
                k--;
                continue;
            }
            cells.putIfAbsent(new MutablePair<>(i, j), direction);
        }
        try {
            File newFile = new File("src/main/resources/" + outFileName);
            FileWriter myWriter = new FileWriter(newFile);
            myWriter.write("I\tJ\tDIRECTION\n");
            boolean first = true;
            for (Map.Entry<MutablePair<Integer, Integer>, Integer> entry: cells.entrySet()) {
                if(first){
                    myWriter.write(entry.getKey().getLeft() + "\t" + entry.getKey().getRight() + "\t" + entry.getValue());
                    first = false;
                } else
                    myWriter.write("\n" + entry.getKey().getLeft() + "\t" + entry.getKey().getRight() + "\t" + entry.getValue());
            }

            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
