package uni.tirocinio.generatore;

import org.json.JSONObject;

public interface QueryGenerator {
    public String generate(JSONObject data);

    public void setNextGenerator(QueryGenerator nextGenerator);
}
