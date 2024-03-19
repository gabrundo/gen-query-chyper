package uni.tirocinio.generatore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Constraint {
    private final List<String> element = List.of("property", "label", "relationship");
    private final List<Relationship> relationships = List.of(
            new Relationship("FOLLOWS", "User", "User"),
            new Relationship("MENTIONS", "Tweet", "Me"),
            new Relationship("POSTS", "User", "Tweet"),
            new Relationship("USING", "Tweet", "Source"),
            new Relationship("TAGS", "Tweet", "Hashtag"),
            new Relationship("CONTAINS", "Tweet", "Link"),
            new Relationship("POSTS", "User", "Tweet"),
            new Relationship("MENTIONS", "Tweet", "User"),
            new Relationship("RETWEETS", "Tweet", "Tweet"),
            new Relationship("REPLAY_TO", "Tweet", "Tweet"));
    private final List<String> nodeLabels = List.of("Hashtag", "Link", "Source", "Tweet", "User", "Me");
    private final Map<String, List<String>> nodeProperties = Map.of(
            "Hashtag", List.of("name"),
            "Link", List.of("url"),
            "Source", List.of("name"),
            "Tweet", List.of("id", "text", "created_at", "import_method", "favorites", "id_str"),
            "User",
            List.of("followers", "following", "name", "screen_name", "statuses", "profile_image_url", "url"),
            "Me", List.of("followers", "following", "name", "screen_name", "statuses", "profile_image_url", "url"));
    private final List<String> propHashtagName = List.of("education", "neo4jauraenterprise", "neo4j",
            "antimoneylaundering",
            "graphdatabases",
            "automotive", "iam", "auraenterprise", "graphes", "cloud");
    private final List<String> propSourceName = List.of("Buffer", "Tweetbot for iΟS", "TweetDeck", "Twitter Web App",
            "SocialPilot.co", "startup.jobs", "Twitter for Android", "HubSpot", "Tweetbot for Mac",
            "Twitter for iPhone");
    private final List<String> propLinkUrl = List.of(
            "https://twitter.com/i/web/status/1371815021265747970",
            "https://twitter.com/i/web/status/1371778287970705408",
            "https://twitter.com/i/web/status/1371687808847519748",
            "http://neo4j-contrib.github.io/cypher-dsl/current/#2021.1",
            "https://twitter.com/i/web/status/1371748138491510786",
            "https://twitter.com/i/web/status/1371673079517171712",
            "https://twitter.com/i/web/status/1371846247913512966",
            "https://twitter.com/i/web/status/1371672671419826177",
            "https://twitter.com/i/web/status/1371614692528025601",
            "https://buff.ly/39Zwfui");
    private final List<String> propTweetId = List.of("1371815021265747970", "1371778287970705408",
            "1371687808847519748",
            "1371787098945114113", "1371673079517171712",
            "1371773486348255233", "1371672671419826177", "1371756686407577611", "1371846247913512966",
            "1371748138491510786");
    private final List<String> propUserScreenName = List.of("neo4j", "NASAPersevere", "pranitahakim1", "galeister",
            "rhema_beth",
            "AngeliusAngel", "michibertoldi", "plotmath", "kaynairv", "jamesejr7", "amanvincent", "BytesByHolly",
            "gunerd_b", "njdequeiros", "emilianogomez33", "pchaturvedi512", "mendonca2709", "AndreaS35872269",
            "ahmed7emedan", "28_fireball");

    private final JSONObject vincolo;
    private final Random random;

    public Constraint() {
        vincolo = new JSONObject();
        random = new Random(Double.doubleToLongBits(Math.random()));
    }

    public void createConstraint(String mode, int n) {
        JSONArray array = new JSONArray(n);

        for (int i = 0; i < n; i++) {
            JSONObject data = new JSONObject();
            JSONObject description = new JSONObject();
            JSONObject linkedTo = new JSONObject();

            int randomElemenent = random.nextInt(3);

            switch (randomElemenent) {
                // PROPRIETÀ
                case 0:

                    // gestione dell'elemento a cui è associata la proprietà
                    int randomNode = random.nextInt(nodeLabels.size() - 1);
                    linkedTo.put("label", nodeLabels.get(randomNode));
                    linkedTo.put("object", "node");

                    description.put("linked-to", linkedTo);

                    // gestione della parte sensibile della proprietà
                    int randomPropType = random.nextInt(2);
                    if (randomPropType == 0) {
                        // Chiave rappresenta il dato sensibile della proprietà
                        String nodeLabel = nodeLabels.get(randomNode);

                        int randomKey = random.nextInt(nodeProperties.get(nodeLabel).size());

                        description.put("type", "key");
                        description.put("list", false);
                        description.put("key", nodeProperties.get(nodeLabel).get(randomKey));

                    } else {
                        // Valore rappresenta il dato sensibile della proprietà
                        description.put("type", "value");
                        description.put("list", false);

                        String key;
                        String value;

                        switch (randomNode) {
                            case 0:
                                key = "name";
                                value = propHashtagName.get(random.nextInt(propHashtagName.size()));
                                break;

                            case 1:
                                key = "url";
                                value = propLinkUrl.get(random.nextInt(propLinkUrl.size()));
                                break;

                            case 2:
                                key = "name";
                                value = propSourceName.get(random.nextInt(propSourceName.size()));
                                break;

                            case 3:
                                key = "id_str";
                                value = propTweetId.get(random.nextInt(propTweetId.size()));
                                break;

                            case 4:
                                key = "screen_name";
                                value = propUserScreenName.get(random.nextInt(propUserScreenName.size()));
                                break;

                            default:
                                key = "";
                                value = "";
                                break;
                        }

                        description.put("key", key);
                        description.put("value", value);
                    }

                    break;
                // ETICHETTA
                case 1:

                    // gestione dell'elemento a cui è associata l'etichetta
                    int randomLabelType = random.nextInt(2);

                    if (randomLabelType == 0) {
                        // ASSOCIATA AD UN NODO
                        linkedTo.put("object", "node");

                        int randomNodeLabel = random.nextInt(nodeLabels.size());

                        description.put("label", nodeLabels.get(randomNodeLabel));
                        if (nodeLabels.get(randomNodeLabel).equals("Me")) {
                            JSONArray labels = new JSONArray(List.of("Me", "User"));
                            linkedTo.put("multiple-labels", true);
                            linkedTo.put("labels", labels);
                        } else {
                            linkedTo.put("multiple-labels", false);
                        }

                    } else {
                        // ASSOCIATA AD UNA RELAZIONE
                        int randomRelation = random.nextInt(relationships.size());
                        description.put("label", relationships.get(randomRelation).label());

                        linkedTo.put("object", "relationship");
                        linkedTo.put("start", Map.of("label", relationships.get(randomRelation).start()));
                        linkedTo.put("end", Map.of("label", relationships.get(randomRelation).end()));
                    }

                    description.put("linked-to", linkedTo);

                    break;
                // RELAZIONE
                case 2:

                    int relIndex = random.nextInt(relationships.size());
                    description.put("label", relationships.get(relIndex).label());
                    description.put("start", Map.of("label", relationships.get(relIndex).start()));
                    description.put("end", Map.of("label", relationships.get(relIndex).end()));

                    break;

            }

            data.put("element", element.get(randomElemenent));
            data.put("description", description);
            data.put("sanitize", mode);

            array.put(data);
        }
        vincolo.put("sensitive-data", array);
    }

    public void printFile(String filename) {
        try {
            PrintWriter writer = new PrintWriter(new File("src/main/resources/" + filename));
            writer.write(vincolo.toString(4));
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Constraint c = new Constraint();

        c.createConstraint("delete", 10);
        c.printFile("delete10.json");
    }
}
