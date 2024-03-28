package uni.tirocinio.generatore;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String fileText = builder.toString();
        Pattern pattern = Pattern.compile("\"\"\"(.*?)\"\"\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fileText);

        list = new ArrayList<>();

        while (matcher.find()) {
            list.add(matcher.group(1));
        }

        return Collections.unmodifiableList(list);
    }

    public static void main(String[] args) {
        List<String> l = CSVReader.read("src/main/resources", "text.csv");

        System.out.println("Numero di vincolo letti: " + l.size());

        for (int i = 0; i < l.size() && i < 10; i++) {
            System.out.println("<" + l.get(i) + ">");
        }
    }
}