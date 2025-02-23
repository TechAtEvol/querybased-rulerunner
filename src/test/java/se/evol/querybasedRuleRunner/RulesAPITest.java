package se.evol.querybasedRuleRunner;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit5.virtual.ShouldNotPin;
import io.quarkus.test.junit5.virtual.internal.VirtualThreadExtension;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;

@QuarkusTest
@ExtendWith(VirtualThreadExtension.class)
class RulesAPITest {
    private static final String RULES = "rules";
    private static final String DB_NAME = "orgkontroll_db";

    private static MongoCollection<Document> getMongoCollection(MongoClient mongoClient) {
        MongoDatabase orgKontrollDb = mongoClient.getDatabase(DB_NAME);
        return orgKontrollDb.getCollection(RULES);
    }

    @Inject
    MongoClient mongoClient;

    @PostConstruct
    public void init() throws IOException {
        MongoCollection<Document> rules = getMongoCollection(mongoClient);
        rules.drop();
        rules.insertOne(Document.parse(JsonTestDataHelper.getRuleEmployerThatIsForeignOwned()));
    }

    @BeforeAll
    static void setUp() {
    }

    @AfterAll
    static void tearDown() {
    }

    @Test
    @ShouldNotPin
    @DisplayName("Posts a rule and receives 201")
    void verifyPostRules() throws IOException {
        RestAssured.given()
                .body(JsonTestDataHelper.getRuleEmployerThatIsForeignOwned())
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