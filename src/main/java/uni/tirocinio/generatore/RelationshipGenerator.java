package uni.tirocinio.generatore;

import org.json.JSONObject;

public class RelationshipGenerator implements ElementGenerator {
    private ElementGenerator next;

    @Override
    public String generateQuery(JSONObject data) {
        String query = "";

        if (canGenerateFromRelationship(data.getString("element"))) {
            // logica di creazione di una query quando il dato sensibile è una relazione

            System.out.println("Query generata partendo da una relazione");
        } else {
            query = next.generateQuery(data);
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

}
