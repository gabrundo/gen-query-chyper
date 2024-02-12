package uni.tirocinio.generatore;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class App {
    public static void main(String[] args) {
        try {
            JsonReader reader = new JsonReader("14.json");

            for (JSONObject sensitveData : reader) {
                QueryGenerator relationshipGen = new RelationshipGenerator();
                QueryGenerator labelGen = new LabelGenerator();
                QueryGenerator propertyGen = new PropertyGenerator();

                propertyGen.setNextGenerator(labelGen);
                labelGen.setNextGenerator(relationshipGen);

                System.out.println("Query da eseguire:");
                System.out.println(propertyGen.generate(sensitveData));
            }

        } catch (IOException x) {
            x.printStackTrace();
        } catch (JSONException y) {
            y.printStackTrace();
        } catch (IllegalArgumentException ae) {
            ae.printStackTrace();
        }
    }
}
