package uni.tirocinio.generatore;

import org.json.JSONObject;

public abstract class AbstractElementGenerator implements ElementGenerator {
    protected final String MATCH = "MATCH";
    protected final String WHERE = "WHERE";
    protected final String IN = "IN";
    protected ElementGenerator next;

    @Override
    public void setNext(ElementGenerator nextGenerator) {
        next = nextGenerator;
    }

    protected abstract void generateMatchPattern(JSONObject linkedTo, StringBuilder sb);
}