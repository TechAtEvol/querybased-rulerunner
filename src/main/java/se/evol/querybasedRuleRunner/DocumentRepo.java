package se.evol.querybasedRuleRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import io.quarkus.logging.Log;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class DocumentRepo {
    private String doc;

    public DocumentRepo() {
        // TODO: Configurable and secret for prod
        String CONNECTION_STRING = "mongodb://localhost:28017";
        this.mongoClient = MongoClients.create(CONNECTION_STRING);
        this.orgKontrollDb = mongoClient.getDatabase(DB_NAME);
    }
    private final MongoClient mongoClient;
    private final MongoDatabase orgKontrollDb;
    private final String DB_NAME = "orgkontroll_db";
    private final String ORGANIZATIONS = TemporaryTestConfigs.databaseCollectionOrganisationsName.getValueOf();
    String RULES = TemporaryTestConfigs.databaseCollectionRulesName.getValueOf();

    public void wipeDataBase() {
        MongoDatabase orgKontrollDb = mongoClient.getDatabase(DB_NAME);
        orgKontrollDb.getCollection(ORGANIZATIONS).drop();
        orgKontrollDb.getCollection("rules").drop();
    }

    public Optional<Document> findLatestOrgById(String orgId) {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(ORGANIZATIONS);
        FindIterable<Document> searchResult = organizations.find(eq("organisation.orgNr", orgId)).sort(descending("ts"));
        return Optional.ofNullable(searchResult.first());

    }

    public String saveOrganisation(String stringDoc) {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(ORGANIZATIONS);
        Document doc = Document.parse(stringDoc);
        doc.put("ts", new Date());
        BsonValue insertId = organizations.insertOne(doc).getInsertedId();
        return Objects.requireNonNull(insertId).asObjectId().getValue().toString();
    }

    public Optional<Document> findLatestRuleVersionById(String ruleId) {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(RULES);
        FindIterable<Document> searchResult = organizations.find(eq("rule.id", ruleId)).sort(descending("ts"));
        return Optional.ofNullable(searchResult.first());
    }

    public FindIterable<Document> findAllRules() {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(RULES);
        return organizations.find()
                .sort(descending("ts"));
    }

    public String saveRule(String stringDoc) {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(RULES);
        Document doc = Document.parse(stringDoc);
        doc.put("ts", new Date());
        BsonValue newId = organizations.insertOne(doc).getInsertedId();
        return Objects.requireNonNull(newId).toString();
    }

    public FindIterable<Document>  runQueryForOrgNr(RulesModel rule, String internalId) {
        Log.info(rule.name());
        Log.info("Internal id: " +internalId);
        BsonDocument freeformQuery = BasicDBObject.parse(rule.query()).toBsonDocument();
        Bson combinedQuery = and(Filters.eq("_id", new ObjectId(internalId)), freeformQuery);

        MongoCollection<Document> organizations = orgKontrollDb.getCollection(ORGANIZATIONS);
        return organizations.find(combinedQuery).sort(descending("ts"));
    }
}