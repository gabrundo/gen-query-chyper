package uni.tirocinio.generatore;

import org.json.JSONArray;
import org.json.JSONObject;

public class LabelGenerator extends AbstractQueryGenerator {
    private final StringBuilder sb;
    private String label;
    private char var;

    public LabelGenerator() {
        sb = new StringBuilder();
    }

    @Override
    public String generate(JSONObject data) {
        String query = "";

        if (canGenerateFromLabel(data.getString("element"))) {
            JSONObject description = data.getJSONObject("description");
            label = description.getString("label");

            // generazione del MATCH
            generateMatchPattern(description.getJSONObject("linked-to"));

            query = sb.toString();
        } else {
            query = next.generate(data);
        }

        return query;
    }

    private void generateMatchPattern(JSONObject linkedTo) {
        var = Character.toLowerCase(label.charAt(0));

        sb.append(MATCH).append(" (");
        if (isLinkedToNode(linkedTo)) {

            sb.append(var);
            if (!linkedTo.getBoolean("multiple-labels")) {
                // MATCH (var:label)
                sb.append(':').append(label);
            } else {
                // MATCH (var:label1:label2...)
                JSONArray labels = linkedTo.getJSONArray("labels");
                for (int i = 0; i < labels.length(); i++) {
                    sb.append(':').append(labels.get(i));
                }
            }
            sb.append(")\n");
        }

        if (isLinkedToRelationship(linkedTo)) {
            // Variabili da recuperare per generare la sanificazione se l'etichetta è legata
            // ad una relazione
            JSONObject start = linkedTo.getJSONObject("start");
            JSONObject end = linkedTo.getJSONObject("end");
            String startLabel = start.getString("label");
            String endLabel = end.getString("label");
            Character varStart = Character.toLowerCase(startLabel.charAt(0));
            Character varEnd = Character.toLowerCase(endLabel.charAt(0));

            // gestione di etichette di partenza e arrivo uguali
            if (varStart == varEnd) {
                varStart = 'x';
                varEnd = 'y';
            }

            // MATCH (varStart:startLabel) -[var:label]-> (varEnd:endLabel)
            sb.append(varStart).append(':').append(startLabel).append(") -[").append(var).append(':').append(label)
                    .append("]-> (").append(varEnd).append(':').append(endLabel).append(")\n");
        }
    }

    private boolean canGenerateFromLabel(String element) {
        return element.equals("label");
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
