package uni.tirocinio.generatore;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class PropertyGenerator extends AbstractQueryGenerator {
    private final StringBuilder sb;
    // TODO: Rappresentare correttamente il tipo del valore di ritotno
    private Map<String, Object> parameters;
    private String type;
    private String key;
    private char var;
    private Boolean listOfValues;

    public PropertyGenerator() {
        sb = new StringBuilder();
        parameters = new HashMap<>();
    }

    @Override
    public String generate(JSONObject data) {
        JSONObject description = data.getJSONObject("description");
        String mode = data.getString("sanitize");
        String query;

        if (canGenerateFromProperty(data.getString("element"))) {
            JSONObject linkedTo = description.getJSONObject("linked-to");
            listOfValues = description.getBoolean("list");
            key = description.getString("key");
            type = description.getString("type");

            // Generazione del MATCH
            generateMatchPattern(linkedTo);

            // generazione della WHERE
            generateWherePattern(description);

            // sanificazione del dato sensibile
            generateSanitizePattern(description, mode);

            query = sb.toString();

            System.out.println("Parametri della query:");
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                System.out.println(entry.getKey() + ", " + entry.getValue());
            }
            System.out.println("---");

        } else {
            query = next.generate(data);
        }

        return query;
    }

    private void generateMatchPattern(JSONObject linkedTo) {
        if (isLinkedToNode(linkedTo)) {
            String nodeLabel = linkedTo.getString("label");
            var = Character.toLowerCase(nodeLabel.charAt(0));

            // MATCH (var:nodeLabel)
            sb.append(MATCH).append(" (").append(var).append(':').append(nodeLabel).append(")").append('\n');
        }

        if (isLinkedToRelationship(linkedTo)) {
            String relLabel = linkedTo.getString("label");
            var = Character.toLowerCase(relLabel.charAt(0));

            String startLabel = linkedTo.getJSONObject("start").getString("label");
            String endLabel = linkedTo.getJSONObject("end").getString("label");

            // MATCH (:startLabel) -[var:relLabel]-> (:endLabel)
            sb.append(MATCH).append(" (").append(':').append(startLabel).append(") -[").append(var).append(':')
                    .append(relLabel).append("] -> (:").append(endLabel).append(")").append('\n');
        }
    }

    private void generateWherePattern(JSONObject description) {
        Boolean listOfValues = description.getBoolean("list");
        Object value = description.get("value");

        if (type.equals("key") || (type.equals("value") && !listOfValues)) {
            // WHERE var.key = $key
            sb.append(WHERE).append(' ').append(var).append('.').append(key).append(" = ").append('$').append(key)
                    .append('\n');

            // TODO: Aggiunta dei parametri in una mappa
            parameters.put("$" + key, value);
        }

        if (type.equals("value") && listOfValues) {
            // WHERE $value IN var.key
            sb.append(WHERE).append(" $value ").append(IN).append(' ').append(var).append('.').append(key).append('\n');

            // TODO: Aggiunta dei parametri in una mappa
            parameters.put("$value", value);
        }
    }

    private void generateSanitizePattern(JSONObject description, String mode) {
        AESCipher cipher = new AESCipher("gabriele.rundo");

        if (mode.equals("encrypt")) {
            if (type.equals("key")) {
                sb.append(REMOVE).append(' ').append(var).append('.').append(key);
            }

            if (type.equals("value")) {
                String value = null;
                try {
                    value = description.getString("value");
                } catch (JSONException e) {
                    value = Integer.toString(description.getInt(value));
                }
                String newValue = cipher.encrypt(value);

                if (listOfValues) {
                    System.out.println("Lista di valori in cifratura");
                    int numberOfValues = 0; /* TODO: Dimensione della lista di valori */
                    if (numberOfValues > 2) {
                    } else {
                        sb.append(REMOVE).append(' ').append(var).append('.').append(key).append('\n');
                    }
                } else {
                    // SET var.key = $new
                    sb.append(SET).append(' ').append(var).append('.').append(key).append(" = ").append("$new");

                    // TODO: gestione dei parametri da aggiungere alla mappa
                    parameters.put("$new", newValue);
                }
            }
        } else if (mode.equals("delete")) {
            if (listOfValues) {
                System.out.println("Lista di valori in cancellazione");
                int numberOfValues = 0; /* TODO: Da inizializzare interrogando il database */
                if (numberOfValues > 2) {
                    // TODO: Cancellazione tramite list-comprension
                }
            } else {
                sb.append(REMOVE).append(' ').append(var).append('.').append(key).append('\n');
            }
        } else {
            throw new IllegalArgumentException("Modalità di cancellazione non supportata!");
        }
    }

    private boolean canGenerateFromProperty(String element) {
        return element.equals("property");
    }

    private boolean isLinkedToNode(JSONObject linkedTo) {
        String linkedElement = linkedTo.getString("object");

        return linkedElement.equals("node");
    }

    private boolean isLinkedToRelationship(JSONObject linkedTo) {
        String linkedElement = linkedTo.getString("object");

        return linkedElement.equals("relationship");
    }

}
