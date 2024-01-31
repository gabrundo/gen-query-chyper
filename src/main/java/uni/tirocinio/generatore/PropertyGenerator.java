package uni.tirocinio.generatore;

import org.json.JSONException;
import org.json.JSONObject;

public class PropertyGenerator extends AbstractElementGenerator {
    private String key;
    private char var;

    @Override
    public String generateQuery(JSONObject data) {
        JSONObject description = data.getJSONObject("description");
        String query = "";

        if (canGenerateFromProperty(data.getString("element"))) {
            JSONObject linkedTo = description.getJSONObject("linked-to");
            StringBuilder sb = new StringBuilder();
            key = description.getString("key");

            // Generazione del MATCH
            generateMatchPattern(linkedTo, sb);

            // generazione della WHERE
            generateWherePattern(description, sb);

            // sanificazione del dato sensibile

            query = sb.toString();
        } else {
            query = next.generateQuery(data);
        }

        return query;
    }

    protected void generateWherePattern(JSONObject description, StringBuilder sb) {
        String type = description.getString("type");
        Boolean listOfValues = description.getBoolean("list");

        if (type.equals("key") || (type.equals("value") && !listOfValues)) {
            // WHERE var.key = value
            sb.append(WHERE).append(' ').append(var).append('.').append(key).append(" = ");
            appendValue(description, sb);
            sb.append('\n');
        }

        if (type.equals("value") && listOfValues) {
            // WHERE value IN var.key
            sb.append(WHERE).append(' ');
            appendValue(description, sb);
            sb.append(' ').append(IN).append(' ').append(var).append('.').append(key).append('\n');
        }
    }

    protected void generateMatchPattern(JSONObject linkedTo, StringBuilder sb) {
        if (isLinkedToNode(linkedTo)) {
            String nodeLabel = linkedTo.getString("label");
            var = Character.toLowerCase(nodeLabel.charAt(0));

            // MATCH (x:label)
            sb.append(MATCH).append(" (").append(var).append(':').append(nodeLabel).append(")").append('\n');
        }

        if (isLinkedToRelationship(linkedTo)) {
            String relLabel = linkedTo.getString("label");
            var = Character.toLowerCase(relLabel.charAt(0));

            JSONObject start = linkedTo.getJSONObject("start");
            JSONObject end = linkedTo.getJSONObject("end");
            String startLabel = start.getString("label");
            String endLabel = end.getString("label");

            // MATCH (:startLabel) -[var:relLabel]-> (:endLabel)
            sb.append(MATCH).append(" (:").append(startLabel).append(") -[").append(var).append(':')
                    .append("] -> (:").append(endLabel).append(")").append('\n');
        }
    }

    private void appendValue(JSONObject description, StringBuilder sb) {
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
