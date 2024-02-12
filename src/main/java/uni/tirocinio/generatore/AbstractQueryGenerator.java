package uni.tirocinio.generatore;

public abstract class AbstractQueryGenerator implements QueryGenerator {
    protected final String DD = "DETACH DELETE";
    protected final String REMOVE = "REMOVE";
    protected final String MATCH = "MATCH";
    protected final String WHERE = "WHERE";
    protected final String IN = "IN";
    protected QueryGenerator next;

    @Override
    public void setNextGenerator(QueryGenerator nextGenerator) {
        next = nextGenerator;
    }
}