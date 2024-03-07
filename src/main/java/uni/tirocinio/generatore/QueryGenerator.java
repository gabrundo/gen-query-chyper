package uni.tirocinio.generatore;

import java.util.Map;

import org.json.JSONObject;

public interface QueryGenerator {
    public String generate(JSONObject data);

    public Map<String, Object> getParameters();

    public void setNextGenerator(QueryGenerator nextGenerator);
}
