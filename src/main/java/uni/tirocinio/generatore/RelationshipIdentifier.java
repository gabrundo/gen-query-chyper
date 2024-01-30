package uni.tirocinio.generatore;

import org.json.JSONObject;

public class RelationshipIdentifier implements ElementIdentifier {
    private final ElementIdentifier next;
    private final String element;
    private JSONObject description;

    public RelationshipIdentifier(JSONObject sensitiveData, ElementIdentifier nextIdentifier) {
        next = nextIdentifier;

        element = sensitiveData.getString("element");
        description = sensitiveData.getJSONObject("description");
    }

    @Override
    public void identify() {
        if (canIdentifyRelationship()) {
            // logica per riconoscere una relazione
            System.out.println("Identificata correttamente una relazione");

        } else {
            System.err.println("Elemento del LPG non identificabile");
        }
    }

    private boolean canIdentifyRelationship() {
        return element.equals("relationship");
    }
}
