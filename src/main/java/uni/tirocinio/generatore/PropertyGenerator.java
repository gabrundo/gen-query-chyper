package uni.tirocinio.generatore;

import org.json.JSONException;
import org.json.JSONObject;

public class PropertyGenerator extends AbstractQueryGenerator {
    private final StringBuilder sb;
    private String key;
    private char var;

    public PropertyGenerator() {
        sb = new StringBuilder();
    }

    @Override
    public String generate(JSONObject data) {
        JSONObject description = data.getJSONObject("description");
        String query;

        if (canGenerateFromProperty(data.getString("element"))) {
            JSONObject linkedTo = description.getJSONObject("linked-to");
            key = description.getString("key");

            // Generazione del MATCH
            generateMatchPattern(linkedTo);

            // generazione della WHERE
            generateWherePattern(description);

            // sanificazione del dato sensibile

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

            JSONObject start = linkedTo.getJSONObject("start");
            JSONObject end = linkedTo.getJSONObject("end");
            String startLabel = start.getString("label");
            String endLabel = end.getString("label");

            // MATCH (:startLabel) -[var:relLabel]-> (:endLabel)
            sb.append(startLabel).append(") -[").append(var).append(':').append(relLabel)
                    .append("] -> (:").append(endLabel).append(")").append('\n');
        }
    }

    private void generateWherePattern(JSONObject description) {
        String type = description.getString("type");
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

    private void appendValue(JSONObject description) {
        try {
            String value = description.getString("value");
            sb.append('"').append(value).append('"');
        } catch (JSONException e) {
            System.out.println("Il valore non è una stringa");
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
