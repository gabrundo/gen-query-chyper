package uni.tirocinio.generatore;

import org.json.JSONException;
import org.json.JSONObject;

public class PropertyGenerator extends AbstractQueryGenerator {
    private final StringBuilder sb;
    private String type;
    private String key;
    private char var;

    public PropertyGenerator() {
        sb = new StringBuilder();
    }

    @Override
    public String generate(JSONObject data) {
        JSONObject description = data.getJSONObject("description");
        String mode = data.getString("sanitize");
        String query;

        if (canGenerateFromProperty(data.getString("element"))) {
            JSONObject linkedTo = description.getJSONObject("linked-to");
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
        sb.append(MATCH).append(" (");

        if (isLinkedToNode(linkedTo)) {
            String nodeLabel = linkedTo.getString("label");
            var = Character.toLowerCase(nodeLabel.charAt(0));

            // MATCH (var:nodeLabel)
            sb.append(var).append(':').append(nodeLabel).append(")").append('\n');
        }

        if (isLinkedToRelationship(linkedTo)) {
            String relLabel = linkedTo.getString("label");
            var = Character.toLowerCase(relLabel.charAt(0));

            String startLabel = linkedTo.getJSONObject("start").getString("label");
            String endLabel = linkedTo.getJSONObject("end").getString("label");

            // MATCH (:startLabel) -[var:relLabel]-> (:endLabel)
            sb.append(':').append(startLabel).append(") -[").append(var).append(':').append(relLabel)
                    .append("] -> (:").append(endLabel).append(")").append('\n');
        }
    }

    private void generateWherePattern(JSONObject description) {
        Boolean listOfValues = description.getBoolean("list");

        if (type.equals("key") || (type.equals("value") && !listOfValues)) {
            // WHERE var.key = value
            sb.append(WHERE).append(' ').append(var).append('.').append(key).append(" = ");
            appendValue(description);
            sb.append('\n');
        }

        if (type.equals("value") && listOfValues) {
            // WHERE value IN var.key
            sb.append(WHERE).append(' ');
            appendValue(description);
            sb.append(' ').append(IN).append(' ').append(var).append('.')

                    .append(key).append('\n');
        }
    }

    private void generateSanitizePattern(JSONObject description, String mode) {
        Boolean listOfValues = description.getBoolean("list");
        AESCipher cipher = new AESCipher("gabriele.rundo");

        if (mode.equals("encrypt")) {
            if (type.equals("key")) {
                String newKey = cipher.encrypt(key);
                sb.append(REMOVE).append(' ').append(var).append('.').append(key);
                sb.append(SET).append(' ').append(var).append('.').append(newKey).append(" = ");
                appendValue(description);
                sb.append('\n');
            } else if (type.equals("value")) {
                String value = null;
                try {
                    value = description.getString("value");
                } catch (JSONException e) {
                    value = Integer.toString(description.getInt(value));
                }

                String newValue = cipher.encrypt(value);

                if (listOfValues) {
                    // TODO: gestione della cancellazione per multi-valore interrogando il database
                    sb.append(REMOVE).append(' ').append(var).append('.').append(key).append('\n');
                } else {
                    // SET var.key = newValue
                    sb.append(SET).append(' ').append(var).append('.').append(key).append(" = ").append('"')
                            .append(newValue).append('"').append('\n');
                }
            } else {
                throw new IllegalArgumentException("Cifratura non supportata!");
            }
        } else if (mode.equals("delete")) {
            if (listOfValues) {
                // TODO: gestione della cancellazione per multi-valore interrogando il database
                sb.append(REMOVE).append(' ').append(var).append('.').append(key).append('\n');
            } else {
                sb.append(REMOVE).append(' ').append(var).append('.').append(key).append('\n');
            }
        } else {
            throw new IllegalArgumentException("Modalità di cancellazione non supportata!");
        }
    }

    private void appendValue(JSONObject description) {
        try {
            String value = description.getString("value");
            sb.append('"').append(value).append('"');
        } catch (JSONException e) {
            sb.append(description.getInt("value"));
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
