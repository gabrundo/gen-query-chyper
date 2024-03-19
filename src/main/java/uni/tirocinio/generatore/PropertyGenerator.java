package uni.tirocinio.generatore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class PropertyGenerator extends AbstractQueryGenerator {
    private final StringBuilder sb;
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

        if (type.equals("value")) {
            Object value = description.get("value");
            if (listOfValues) {
                // WHERE $value IN var.key
                sb.append(WHERE).append(" $value ").append(IN).append(' ').append(var).append('.').append(key)
                        .append('\n');

                // Aggiunta alla mappa del parametro con il tipo corretto
                parameters.put("value", value);
            } else {
                // WHERE var.key = $key
                sb.append(WHERE).append(' ').append(var).append('.').append(key).append(" = ").append('$').append(key)
                        .append('\n');

                // Aggiunta alla mappa del parametro con il tipo corretto
                parameters.put(key, value);
            }
        }
    }

    private void generateSanitizePattern(JSONObject description, String mode) {
        AESCipher cipher = new AESCipher("gabrielerundo");

        if (mode.equals("encrypt")) {
            if (type.equals("key")) {
                sb.append(REMOVE).append(' ').append(var).append('.').append(key);
            }

            if (type.equals("value")) {
                Object value = description.get("value");
                String newValue = cipher.encrypt(value.toString());

                if (listOfValues) {
                    // TODO: Cifratura di un valore in una lista
                    sb.append(REMOVE).append(' ').append(var).append('.').append(key).append('\n');
                } else {
                    // SET var.key = $new
                    sb.append(SET).append(' ').append(var).append('.').append(key).append(" = $new\n");

                    // Aggiunta alla mappa dei parametri con il tipo corretto
                    parameters.put("new", newValue);
                }
            }
        } else if (mode.equals("delete")) {
            if (listOfValues) {
                int numberOfValues = description.getJSONArray("values").length();
                if (numberOfValues > 2) {
                    // SET var.key = [x IN var.key WHERE x <> $key]
                    sb.append(SET).append(' ').append(var).append('.').append(key).append(" = [x ").append(IN)
                            .append(' ').append(var).append('.').append(key).append(' ').append(WHERE)
                            .append(" x <> $value]\n");

                    // valore già inserito perché tratto una lista
                } else {
                    sb.append(REMOVE).append(' ').append(var).append('.').append(key).append('\n');
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

    @Override
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

}
