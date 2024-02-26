package uni.tirocinio.generatore;

public abstract class AbstractQueryGenerator implements QueryGenerator {
    protected final String DELETE = "DELETE";
    protected final String DD = "DETACH DELETE";
    protected final String IN = "IN";
    protected final String MATCH = "MATCH";
    protected final String MERGE = "MERGE";
    protected final String REMOVE = "REMOVE";
    protected final String SET = "SET";
    protected final String WHERE = "WHERE";
    protected QueryGenerator next;

    @Override
    public void setNextGenerator(QueryGenerator nextGenerator) {
        next = nextGenerator;
    }
}