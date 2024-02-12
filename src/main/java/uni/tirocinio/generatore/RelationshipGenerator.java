package uni.tirocinio.generatore;

import org.json.JSONObject;

public class RelationshipGenerator extends AbstractQueryGenerator {
    private final StringBuilder sb;
    private String label;
    private char var;

    public RelationshipGenerator() {
        sb = new StringBuilder();
    }

    @Override
    public String generate(JSONObject data) {
        String query = "";
        String mode = data.getString("sanitize");

        if (canGenerateFromRelationship(data.getString("element"))) {
            JSONObject description = data.getJSONObject("description");

            label = description.getString("label");
            var = Character.toLowerCase(label.charAt(0));

            generateMatchPattern(description);

            generateSanitizePattern(description, mode);

            query = sb.toString();
        } else {
            if (next == null) {
                System.err.println("Generatore della query non supportato!");
                throw new RuntimeException();
            } else {
                query = next.generate(data);
            }
        }

        return query;
    }

    private boolean canGenerateFromRelationship(String element) {
        return element.equals("relationship");
    }

    private void generateMatchPattern(JSONObject description) {
        JSONObject start = description.getJSONObject("start");
        JSONObject end = description.getJSONObject("end");
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
        sb.append(MATCH).append(" (").append(varStart).append(':').append(startLabel).append(") -[").append(var)
                .append(':').append(label).append("]-> (").append(varEnd).append(':').append(endLabel).append(")\n");
    }

    private void generateSanitizePattern(JSONObject description, String mode) {
        if (mode.equals("delete")) {
            sb.append(DD).append(' ').append(var);
        } else {
            throw new IllegalArgumentException("Modalità di sanificazione non supportata");
        }
    }
}
