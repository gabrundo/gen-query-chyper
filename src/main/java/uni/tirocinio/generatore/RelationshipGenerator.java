package uni.tirocinio.generatore;

import org.json.JSONObject;

public class RelationshipGenerator extends AbstractElementGenerator {
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

    @Override
    protected void generateMatchPattern(JSONObject linkedTo, StringBuilder sb) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateMatchPattern'");
    }

    @Override
    protected void generateWherePattern(JSONObject description, StringBuilder sb) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateWherePattern'");
    }

}
