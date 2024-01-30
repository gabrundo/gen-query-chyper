package uni.tirocinio.generatore;

import org.json.JSONObject;

public class LabelIdentifier implements ElementIdentifier {
    private final ElementIdentifier next;
    private final String element;
    private JSONObject description;

    public LabelIdentifier(JSONObject sensitiveData, ElementIdentifier nextIdentifier) {
        next = nextIdentifier;

        element = sensitiveData.getString("element");
        description = sensitiveData.getJSONObject("description");
    }

    @Override
    public void identify() {
        if (canIdentifyLabel()) {
            // logica per identidicare l'etichetta
            System.out.println("Identificata correttamente un'etichetta");
        } else {
            next.identify();
        }
    }

    private boolean canIdentifyLabel() {
        return element.equals("label");
    }

}
