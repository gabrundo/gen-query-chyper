package uni.tirocinio.generatore;

import org.json.JSONObject;

public class LabelGenerator implements ElementGenerator {
    private ElementGenerator next;

    @Override
    public String generateQuery(JSONObject data) {
        String query = "";

        if (canGenerateFromLabel(data.getString("element"))) {
            // logica di creazione di una query quando il dato sensibile è un'etichetta

            System.out.println("Query generata partendo da un'etichetta");
        } else {
            query = next.generateQuery(data);
        }

        return query;
    }

    @Override
    public void setNext(ElementGenerator nextGenerator) {
        next = nextGenerator;
    }

    private boolean canGenerateFromLabel(String element) {
        return element.equals("label");
    }
}
