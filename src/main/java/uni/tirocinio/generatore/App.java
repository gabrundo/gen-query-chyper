package uni.tirocinio.generatore;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class App {
    public static void main(String[] args) {
        try {
            JsonReader reader = new JsonReader("14.json");

            for (JSONObject sensitveData : reader) {
                ElementGenerator relationshipGen = new RelationshipGenerator();
                ElementGenerator labelGen = new LabelGenerator();
                ElementGenerator propertyGen = new PropertyGenerator();

                propertyGen.setNext(labelGen);
                labelGen.setNext(relationshipGen);

                String query = propertyGen.generateQuery(sensitveData);

                System.out.println("Query da eseguire:");
                System.out.println(query);
            }

        } catch (IOException x) {
            x.printStackTrace();
        } catch (JSONException y) {
            y.printStackTrace();
        }
    }
}
