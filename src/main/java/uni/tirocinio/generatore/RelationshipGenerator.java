package uni.tirocinio.generatore;

import org.json.JSONObject;

public class RelationshipGenerator extends AbstractElementGenerator {
    private String label;
    private char var;

    @Override
    public String generateQuery(JSONObject data) {
        String query = "";

        if (canGenerateFromRelationship(data.getString("element"))) {
            StringBuilder sb = new StringBuilder();
            JSONObject description = data.getJSONObject("description");

            label = description.getString("label");
            var = Character.toLowerCase(label.charAt(0));

            generateMatchPattern(description, sb);

            query = sb.toString();
        } else {
            if (next == null) {
                System.err.println("Generatore della query non supportato!");
                throw new RuntimeException();
            } else {
                query = next.generateQuery(data);
            }
        }

        return query;
    }

    private boolean canGenerateFromRelationship(String element) {
        return element.equals("relationship");
    }

    @Override
    public void setNext(ElementGenerator nextGenerator) {
        next = nextGenerator;
    }

    @Override
    protected void generateMatchPattern(JSONObject description, StringBuilder sb) {
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
}
