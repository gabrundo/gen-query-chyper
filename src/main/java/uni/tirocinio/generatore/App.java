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

                String query = propertyGen.generate(sensitveData);
                System.out.println("Query da eseguire: \n" + query);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Operazione non supportata");
            e.printStackTrace();
        }
    }
}
