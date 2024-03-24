package uni.tirocinio.generatore;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CSVReader {
    public static List<String> read(String path, String file) {
        StringBuilder builder = new StringBuilder();
        List<String> list;
        try {
            Charset charset = Charset.forName("UTF-8");
            Path filePath = FileSystems.getDefault().getPath(path, file);
            BufferedReader reader = Files.newBufferedReader(filePath, charset);

            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file");
        }

        String[] s = builder.toString().split("\"\"\"");

        list = new ArrayList<>(Arrays.asList(s));
        list.remove(0);
        list.removeIf(l -> l.isBlank());

        return Collections.unmodifiableList(list);
    }

    public static void main(String[] args) {
        List<String> l = CSVReader.read("", "text.csv");

        System.out.println("Numero di vincolo letti: " + l.size());

        for (int i = 0; i < l.size() && i < 10; i++) {
            System.out.println("<" + l.get(i) + ">");
        }
    }
}