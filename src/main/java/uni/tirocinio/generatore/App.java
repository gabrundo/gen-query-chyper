package uni.tirocinio.generatore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.*;

public class App {
    public static void main(String[] args) {
        Path file = FileSystems.getDefault().getPath("src/main/resources", "1a.json");
        StringBuilder sb = new StringBuilder();

        try {
            InputStream in = Files.newInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jObject = new JSONObject(sb.toString());
            System.out.println(jObject.getJSONObject("sensitive-data"));

        } catch (IOException x) {
            x.printStackTrace();
        } catch (JSONException y) {
            System.err.println("Errroe nella formattazione del file JSON!");
            y.printStackTrace();
        }
    }
}
