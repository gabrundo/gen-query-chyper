package uni.tirocinio.generatore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Constraint {
    private final List<Relationship> relationships = List.of(
            new Relationship("FOLLOWS", "User", "User"),
            new Relationship("MENTIONS", "Tweet", "Me"),
            new Relationship("USING", "Tweet", "Source"),
            new Relationship("TAGS", "Tweet", "Hashtag"),
            new Relationship("CONTAINS", "Tweet", "Link"),
            new Relationship("POSTS", "User", "Tweet"),
            new Relationship("MENTIONS", "Tweet", "User"),
            new Relationship("RETWEETS", "Tweet", "Tweet"),
            new Relationship("REPLAY_TO", "Tweet", "Tweet"));
    private final List<String> nodeLabels = List.of("Hashtag", "Link", "Source", "Tweet", "User", "Me");
    private final Map<String, List<String>> nodeProperties = Map.of(
            "Hashtag", List.of("name"),
            "Link", List.of("url"),
            "Source", List.of("name"),
            "Tweet", List.of("id", "text", "created_at", "import_method", "favorites", "id_str"),
            "User",
            List.of("followers", "following", "name", "screen_name", "statuses", "profile_image_url", "url"));
    private final List<String> text;
    private final List<String> screenName;
    private final List<String> name;

    private final JSONObject vincolo;
    private final Random random;

    public Constraint() {
        vincolo = new JSONObject();
        random = new Random(Double.doubleToLongBits(Math.random()));

        text = CSVReader.read("src/main/resources", "text.csv");
        screenName = CSVReader.read("src/main/resources", "screenName.csv");
        name = CSVReader.read("src/main/resources", "nameHashtag.csv");
    }

    public void createConstraint(String mode, int n) {
        JSONArray array = new JSONArray(n);

        int i = 0;
        while (i < n) {
            while (0 <= i && i < n * 0.62) {
                System.out.println("Generazione dato sensibile associato ad un al testo del Tweet");
                JSONObject data = new JSONObject();
                JSONObject description = new JSONObject();
                JSONObject linkedTo = new JSONObject();

                linkedTo.put("label", "Tweet");
                linkedTo.put("object", "node");
                description.put("linked-to", linkedTo);

                description.put("list", false);
                description.put("key", "text");
                description.put("value", text.get(random.nextInt(text.size())));
                description.put("type", "value");

                data.put("element", "property");
                data.put("description", description);
                data.put("sanitize", mode);

                array.put(data);
                i++;
            }

            while (0.62 * n <= i && i < n * 0.92) {
                System.out.println("Generazione dato sensibile associato ad un al nome del User");
                JSONObject data = new JSONObject();
                JSONObject description = new JSONObject();
                JSONObject linkedTo = new JSONObject();

                linkedTo.put("label", "User");
                linkedTo.put("object", "node");
                description.put("linked-to", linkedTo);

                description.put("list", false);
                description.put("key", "screen_name");
                description.put("value", screenName.get(random.nextInt(screenName.size())));
                description.put("type", "value");

                data.put("element", "property");
                data.put("description", description);
                data.put("sanitize", mode);

                array.put(data);
                i++;
            }

            while (0.92 * n <= i && i < n * 0.98) {
                System.out.println("Generazione dato sensibile associato ad un al nome del Hashtag");
                JSONObject data = new JSONObject();
                JSONObject description = new JSONObject();
                JSONObject linkedTo = new JSONObject();

                linkedTo.put("label", "Hashtag");
                linkedTo.put("object", "node");
                description.put("linked-to", linkedTo);

                description.put("list", false);
                description.put("key", "name");
                description.put("value", name.get(random.nextInt(name.size())));
                description.put("type", "value");

                data.put("element", "property");
                data.put("description", description);
                data.put("sanitize", mode);

                array.put(data);
                i++;
            }

            while (0.98 * n <= i && i < n * 0.991) {
                System.out.println("Generazione dato sensibile associato ad una chiave di una proprietà");

                JSONObject data = new JSONObject();
                JSONObject description = new JSONObject();
                JSONObject linkedTo = new JSONObject();

                String node = nodeLabels.get(random.nextInt(nodeLabels.size()));

                linkedTo.put("label", node);
                linkedTo.put("object", "node");
                description.put("linked-to", linkedTo);

                description.put("list", false);
                description.put("key", nodeProperties.get(node).get(random.nextInt(nodeProperties.get(node).size())));
                description.put("type", "key");

                data.put("element", "property");
                data.put("description", description);
                data.put("sanitize", mode);

                array.put(data);
                i++;
            }

            while (0.991 * n <= i && i < n * 0.997) {
                System.out.println("Generazione di un dato sensibile associato ad una relazione");
                JSONObject data = new JSONObject();
                JSONObject description = new JSONObject();

                Relationship r = relationships.get(random.nextInt(relationships.size()));

                description.put("label", r.label());
                description.put("start", new JSONObject(Map.of("label", r.start())));
                description.put("end", new JSONObject(Map.of("label", r.end())));

                data.put("element", "relationship");
                data.put("description", description);
                data.put("sanitize", mode);

                array.put(data);
                i++;
            }

            while (0.997 * n <= i && i < n) {
                System.out.println("Generazione di un dato sensibile associato ad un'etichetta");
                JSONObject data = new JSONObject();
                JSONObject description = new JSONObject();
                JSONObject linkedTo = new JSONObject();

                String label = nodeLabels.get(nodeLabels.size());

                linkedTo.put("multiple-labels", false);
                linkedTo.put("object", "node");

                description.put("label", label);
                description.put("linked-to", linkedTo);

                data.put("element", "label");
                data.put("description", description);
                data.put("sanitize", mode);

                array.put(data);
                i++;
            }
        }

        vincolo.put("sensitive-data", array);
    }

    public void printFile(String filename) {
        try {
            PrintWriter writer = new PrintWriter(new File("src/main/resources/" + filename));
            writer.write(vincolo.toString(4));
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Constraint c = new Constraint();

        c.createConstraint("delete", 10);
        c.printFile("delete.json");
    }
}
