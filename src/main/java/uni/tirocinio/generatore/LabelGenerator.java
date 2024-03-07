package uni.tirocinio.generatore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class LabelGenerator extends AbstractQueryGenerator {
    private final StringBuilder sb;
    private String label;
    private char var;
    private char varStart;
    private char varEnd;
    private String startLabel;
    private String endLabel;

    public LabelGenerator() {
        sb = new StringBuilder();
    }

    @Override
    public String generate(JSONObject data) {
        String query = "";
        String mode = data.getString("sanitize");

        if (canGenerateFromLabel(data.getString("element"))) {
            JSONObject description = data.getJSONObject("description");
            label = description.getString("label");

            // generazione del MATCH
            generateMatchPattern(description.getJSONObject("linked-to"));

            // generazione della sanificazione
            generateSanitizePattern(description, mode);

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
            if (!hasNodeMultipleLables(linkedTo)) {
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
        } else if (isLinkedToRelationship(linkedTo)) {
            startLabel = linkedTo.getJSONObject("start").getString("label");
            endLabel = linkedTo.getJSONObject("end").getString("label");

            varStart = Character.toLowerCase(startLabel.charAt(0));
            varEnd = Character.toLowerCase(endLabel.charAt(0));

            // gestione di etichette di partenza e arrivo uguali
            if (varStart == varEnd) {
                varStart = 'x';
                varEnd = 'y';
            }

            // MATCH (varStart:startLabel) -[var:label]-> (varEnd:endLabel)
            sb.append(varStart).append(':').append(startLabel).append(") -[").append(var).append(':').append(label)
                    .append("]-> (").append(varEnd).append(':').append(endLabel).append(")\n");
        } else {
            throw new IllegalArgumentException("Generazione non consentita, elemento non riconosciuto");
        }
    }

    private void generateSanitizePattern(JSONObject description, String mode) {
        JSONObject linkedTo = description.getJSONObject("linked-to");

        if (mode.equals("encrypt")) {
            if (isLinkedToRelationship(linkedTo)) {
                AESCipher cipher = new AESCipher("gabrielerundo");
                String newLabel = cipher.encrypt(label);

                // DELETE var
                // MERGE (varStart:startLable) -[:newLabel]-> (varEnd:endLabel)
                sb.append(DELETE).append(' ').append(var).append('\n');
                sb.append(MERGE).append(" (").append(varStart).append(':').append(startLabel).append(") -[:")
                        .append(newLabel).append("]->").append('(').append(varEnd).append(':').append(endLabel)
                        .append(")\n");
            } else if (isLinkedToNode(linkedTo)) {
                AESCipher cipher = new AESCipher("gabrielerundo");
                String newLabel = cipher.encrypt(label);

                // REMOVE var.label
                // MERGE var:newLabel
                sb.append(REMOVE).append(' ').append(var).append(':').append(label).append('\n');
                sb.append(MERGE).append(' ').append(var).append(':').append(newLabel).append('\n');
            } else {
                throw new IllegalArgumentException("Cifratura non supportata!");
            }
        } else if (mode.equals("delete")) {
            if (isLinkedToNode(linkedTo) && hasNodeMultipleLables(linkedTo)) {
                int numberOfLabels = linkedTo.getJSONArray("labels").length();

                if (numberOfLabels >= 2)
                    sb.append(REMOVE).append(' ').append(var).append(':').append(label).append('\n');
            } else {
                sb.append(DELETE).append(' ').append(var).append('\n');
            }
        } else {
            throw new IllegalArgumentException("Modalità di sanificazione non riconosciuta!");
        }
    }

    private boolean canGenerateFromLabel(String element) {
        return element.equals("label");
    }

    private boolean hasNodeMultipleLables(JSONObject linkedTo) {
        return linkedTo.getBoolean("multiple-labels");
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
        return Collections.unmodifiableMap(new HashMap<>());
    }
}
