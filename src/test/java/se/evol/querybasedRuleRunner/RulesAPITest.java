package se.evol.querybasedRuleRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit5.virtual.ShouldNotPin;
import io.quarkus.test.junit5.virtual.internal.VirtualThreadExtension;
import io.restassured.RestAssured;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;

@QuarkusTest
@ExtendWith(VirtualThreadExtension.class)
class RulesAPITest {

    private static final String RULES = TemporaryTestConfigs.databaseCollectionRulesName.getValueOf();
    private static final String filePathToJson = TemporaryTestConfigs.pathToRuleForUtlandskFilial.getValueOf();
    private static final String CONNECTION_STRING = "mongodb://localhost:28017";
    private static final String DB_NAME = "orgkontroll_db";


    private static MongoClient getMongoClient() {
        return MongoClients.create(CONNECTION_STRING);
    }
    private static MongoCollection<Document> getMongoCollection(MongoClient mongoClient) {
        MongoDatabase orgKontrollDb = mongoClient.getDatabase(DB_NAME);
        return orgKontrollDb.getCollection(RULES);
    }


    @BeforeAll
    static void setUp() throws IOException {
        MongoClient mongoClient = getMongoClient();
        MongoCollection<Document> organisations = getMongoCollection(mongoClient);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonFile = objectMapper.readTree(new File(filePathToJson));
        organisations.insertOne(Document.parse(jsonFile.toString()));
        mongoClient.close();
    }

    @AfterAll
    static void tearDown() {
        MongoClient mongoClient = getMongoClient();
        MongoCollection<Document> organisations = getMongoCollection(mongoClient);
        organisations.drop();
        mongoClient.close();
    }

    @Test
    @ShouldNotPin
    @DisplayName("Posts a rule and receives 201")
    void verifyPostRules() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonFile = objectMapper.readTree(new File(TemporaryTestConfigs.pathToRuleForUtlandskFilial.getValueOf()));
        String jsonString = jsonFile.toString();

        RestAssured.given()
                .body(jsonString)
                .when().post("/rules")
                .then().statusCode(201);
    }

    @Test
    @ShouldNotPin
    @DisplayName("Gets a rule by id")
    void verifyGetRuleById() {
        String ruleId = "1";
        RestAssured.get("/rules/" + ruleId).then().body(containsString("FILIAL_TILL_UTLANDSKT_BOLAG")).statusCode(200);
    }
}