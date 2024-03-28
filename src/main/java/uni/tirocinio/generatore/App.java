package uni.tirocinio.generatore;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.EagerResult;

public class App {
    public static void main(String[] args) {
        try {
            String dbUri = "neo4j://localhost";
            String dbUser = "neo4j";
            String dbName = "twitter";
            String dbPassword = "TwitterDataset";

            DatabaseConnection database = new DatabaseConnection(dbUri, dbUser, dbPassword);

            int N = 5;
            long T = 0;

            Constraint constraint = new Constraint();
            constraint.createConstraint("encrypt", N);
            constraint.printFile("encrypt.json");
            JsonReader reader = new JsonReader("encrypt.json");

            for (JSONObject sensitiveData : reader) {
                QueryGenerator relationshipGen = new RelationshipGenerator();
                QueryGenerator labelGen = new LabelGenerator();
                QueryGenerator propertyGen = new PropertyGenerator();

                propertyGen.setNextGenerator(labelGen);
                labelGen.setNextGenerator(relationshipGen);

                String query = propertyGen.generate(sensitiveData);
                Map<String, Object> parameters = propertyGen.getParameters();
                EagerResult result = database.execute(dbName, query, parameters);

                T += result.summary().resultAvailableAfter(TimeUnit.MILLISECONDS);
            }
            System.out.printf("RECAP DI ESECUZIONE: \n--- T: %d ms, n: %d ---\n", T, N);
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
