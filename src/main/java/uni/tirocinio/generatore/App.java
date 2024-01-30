package uni.tirocinio.generatore;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class App {
    public static void main(String[] args) {
        try {
            JsonReader reader = new JsonReader("1a.json");

            for (JSONObject jsonObject : reader) {
                System.out.println(jsonObject.toString());
            }

        } catch (IOException x) {
            x.printStackTrace();
        } catch (JSONException y) {
            y.printStackTrace();
        }
    }
}
