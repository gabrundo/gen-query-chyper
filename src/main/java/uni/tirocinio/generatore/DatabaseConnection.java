package uni.tirocinio.generatore;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;

public class DatabaseConnection {
    private final Driver driver;

    public DatabaseConnection(String dbUri, String dbUser, String dbPassword) {
        driver = GraphDatabase.driver(Objects.requireNonNull(dbUri),
                AuthTokens.basic(Objects.requireNonNull(dbUser), Objects.requireNonNull(dbPassword)));
    }

    public EagerResult execute(String dbName, String query, Map<String, Object> parameters) {
        EagerResult result = driver.executableQuery(query).withParameters(Objects.requireNonNull(parameters))
                .withConfig(QueryConfig.builder().withDatabase(Objects.requireNonNull(dbName)).build()).execute();

        System.out.println(result.summary().resultAvailableAfter(TimeUnit.MILLISECONDS));

        return result;
    }
}
