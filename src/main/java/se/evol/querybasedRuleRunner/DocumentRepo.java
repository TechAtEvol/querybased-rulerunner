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
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class DocumentRepo {
    private String doc;

    String CONNECTION_STRING = ConfigProvider.getConfig().getValue("database.url", String.class);
    String DATABASE_NAME = ConfigProvider.getConfig().getValue("database.name", String.class);

    String COLLECTION_RULES_NAME = ConfigProvider.getConfig().getValue("database.collection.rules.name", String.class);
    String COLLECTION_ORGANISATIONS_NAME = ConfigProvider.getConfig().getValue("database.collection.organisations.name", String.class);
    public DocumentRepo() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        this.orgKontrollDb = mongoClient.getDatabase(DATABASE_NAME);
    }
    private final MongoDatabase orgKontrollDb;

    public Optional<Document> findLatestOrgById(String orgId) {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(COLLECTION_ORGANISATIONS_NAME);
        FindIterable<Document> searchResult = organizations.find(eq("organisation.orgNr", orgId)).sort(descending("ts"));
        return Optional.ofNullable(searchResult.first());
    }

    public String saveOrganisation(String stringDoc) {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(COLLECTION_ORGANISATIONS_NAME);
        Document doc = Document.parse(stringDoc);
        doc.put("ts", new Date());
        BsonValue insertId = organizations.insertOne(doc).getInsertedId();
        return Objects.requireNonNull(insertId).asObjectId().getValue().toString();
    }

    public Optional<Document> findLatestRuleVersionById(String ruleId) {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(COLLECTION_RULES_NAME);
        FindIterable<Document> searchResult = organizations.find(eq("rule.id", ruleId)).sort(descending("ts"));
        return Optional.ofNullable(searchResult.first());
    }

    public FindIterable<Document> findAllRules() {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(COLLECTION_RULES_NAME);
        return organizations.find()
                .sort(descending("ts"));
    }

    public String saveRule(String stringDoc) {
        MongoCollection<Document> organizations = orgKontrollDb.getCollection(COLLECTION_RULES_NAME);
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

        MongoCollection<Document> organizations = orgKontrollDb.getCollection(COLLECTION_ORGANISATIONS_NAME);
        return organizations.find(combinedQuery).sort(descending("ts"));
    }
}