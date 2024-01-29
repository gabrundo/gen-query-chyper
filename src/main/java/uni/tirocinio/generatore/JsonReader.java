package uni.tirocinio.generatore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * OVERVIEW: Classe con il compito di ottenere l'oggetto JSON dato il nome del file
 */
public class JsonReader {
    private final Path file;
    private JSONObject jObject;

    public JsonReader(String source) throws IOException, JSONException {
        file = FileSystems.getDefault().getPath("src/main/resources", source);
        StringBuilder sb = new StringBuilder();

        InputStream in = Files.newInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        jObject = new JSONObject(sb.toString());
    }

    public final JSONObject getSensitiveData() throws JSONException {
        return jObject.getJSONObject("sensitive-data");
    }

    // TODO: Aggiungere un metodo che permette di capire se il dato sensibile è
    // formato da un array di oggetti

}
