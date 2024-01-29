package uni.tirocinio.generatore;

import java.io.IOException;

import org.json.JSONException;

public class App {
    public static void main(String[] args) {
        try {
            JsonReader reader = new JsonReader("1a.json");
            System.out.println(reader.getSensitiveData());

        } catch (IOException x) {
            x.printStackTrace();
        } catch (JSONException y) {
            y.printStackTrace();
        }
    }
}
