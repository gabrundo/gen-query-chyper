package uni.tirocinio.generatore;

import org.json.JSONObject;

public class PropertyIdentifier implements ElementIdentifier {
    private final ElementIdentifier next;
    private final String element;
    private JSONObject description;

    public PropertyIdentifier(JSONObject sensitiveData, ElementIdentifier nextIdentifier) {
        next = nextIdentifier;

        element = sensitiveData.getString("element");
        description = sensitiveData.getJSONObject("description");
    }

    private boolean canIdentifyProperty() {
        return element.equals("property");
    }

    @Override
    public void identify() {
        if (canIdentifyProperty()) {
            // logica di identificazione degli elementi di una proprietà
            System.out.println("Identificata correttamente una proprietà");

        } else {
            next.identify();
        }
    }
}
