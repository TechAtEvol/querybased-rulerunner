package se.evol.querybasedRuleRunner;

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

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;

@QuarkusTest
@ExtendWith(VirtualThreadExtension.class)
class OrganisationAPITest {
    private static final String ORGANIZATIONS = "organisations";
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "orgkontroll_db";

    private static MongoClient getMongoClient() {
        return MongoClients.create(CONNECTION_STRING);
    }
    private static MongoCollection<Document> getMongoCollection(MongoClient mongoClient) {
        MongoDatabase orgKontrollDb = mongoClient.getDatabase(DB_NAME);
        return orgKontrollDb.getCollection(ORGANIZATIONS);
    }

    @BeforeAll
    static void setUp() throws IOException {
        MongoClient mongoClient = getMongoClient();
        MongoCollection<Document> organisations = getMongoCollection(mongoClient);
        organisations.insertOne(Document.parse(JsonTestDataHelper.getEmployerThatIsForeignOwned()));
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
    @DisplayName("Posts an org and receives 201")
    void verifyPostOrganisation() throws IOException {
        RestAssured.given()
                .body(JsonTestDataHelper.getCompanyWithoutTaxRegistrationAsEmployer())
                .when().post("/organisations")
                .then().statusCode(201);
    }

    @Test
    @ShouldNotPin
    @DisplayName("Gets an org by id")
    void verifyGetOrganisations() {
        String orgNr = "5020201140";
        RestAssured.get("/organisations/" + orgNr).then().body(containsString(orgNr)).statusCode(200);
    }
}