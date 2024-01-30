package uni.tirocinio.generatore;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class App {
    public static void main(String[] args) {
        try {
            JsonReader reader = new JsonReader("8.json");

            for (JSONObject jsonObject : reader) {
                // System.out.println(jsonObject.toString());

                ElementIdentifier relationshipIdentifier = new RelationshipIdentifier(jsonObject, null);
                ElementIdentifier labelIdentifier = new LabelIdentifier(jsonObject, relationshipIdentifier);
                ElementIdentifier propertyIdentifier = new PropertyIdentifier(jsonObject, labelIdentifier);

                propertyIdentifier.identify();

            }

        } catch (IOException x) {
            x.printStackTrace();
        } catch (JSONException y) {
            y.printStackTrace();
        }
    }
}
