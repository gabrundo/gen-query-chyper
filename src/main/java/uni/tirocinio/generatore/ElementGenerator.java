package uni.tirocinio.generatore;

import org.json.JSONObject;

public interface ElementGenerator {
    public String generateQuery(JSONObject data);

    public void setNext(ElementGenerator nextGenerator);
}
