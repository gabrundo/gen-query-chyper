package uni.tirocinio.generatore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * OVERVIEW: Classe con il compito di ottenere i JSONObject che formano i dati sensibili di un'istanza di dati
 */
public class JsonReader implements Iterable<JSONObject> {
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

    @Override
    public Iterator<JSONObject> iterator() {
        List<JSONObject> coll = new ArrayList<>();
        String sensKey = "sensitive-data";

        JSONArray sensibleArray = jObject.optJSONArray(sensKey);
        if (sensibleArray != null) {
            System.out.println("Dato sensibile formato da un array");
            for (int i = 0; i < sensibleArray.length(); i++) {
                coll.add(sensibleArray.getJSONObject(i));
            }
        } else {
            coll.add(jObject.getJSONObject(sensKey));
        }
        return Collections.unmodifiableCollection(coll).iterator();

    }
}
