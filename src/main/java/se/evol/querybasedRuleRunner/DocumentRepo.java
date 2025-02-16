package se.evol.querybasedRuleRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;


@ApplicationScoped
public class DocumentRepo {
    @Inject
    MongoClient mongoClient;
    @ConfigProperty(name = "database.name")
    String DATABASE_NAME;

    @ConfigProperty(name = "database.collection.rules.name")
    String COLLECTION_RULES_NAME;
    //String COLLECTION_RULES_NAME = ConfigProvider.getConfig().getValue("database.collection.rules.name", String.class);

    @ConfigProperty(name = "database.collection.organisations.name")
    String COLLECTION_ORGANISATIONS_NAME;
    //String COLLECTION_ORGANISATIONS_NAME = ConfigProvider.getConfig().getValue("database.collection.organisations.name", String.class);


    public Optional<Document> findLatestOrgById(String orgId) {
        MongoCollection<Document> organizations = getOrganisationsCollection();
        FindIterable<Document> searchResult = organizations.find(eq("organisation.orgNr", orgId)).sort(descending("ts"));
        return Optional.ofNullable(searchResult.first());
    }

    public String saveOrganisation(String stringDoc) {
        MongoCollection<Document> organizations = getOrganisationsCollection();
        Document doc = Document.parse(stringDoc);
        doc.put("ts", new Date());
        BsonValue insertId = organizations.insertOne(doc).getInsertedId();
        return Objects.requireNonNull(insertId).asObjectId().getValue().toString();
    }

    public Optional<Document> findLatestRuleVersionById(String ruleId) {
        MongoCollection<Document> organizations = getRulesCollection();
        FindIterable<Document> searchResult = organizations.find(eq("rule.id", ruleId)).sort(descending("ts"));
        return Optional.ofNullable(searchResult.first());
    }

    public FindIterable<Document> findAllRules() {
        MongoCollection<Document> organizations = getRulesCollection();
        return organizations.find()
                .sort(descending("ts"));
    }

    public String saveRule(String stringDoc) {
        MongoCollection<Document> organizations = getRulesCollection();
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
        MongoCollection<Document> organizations = getOrganisationsCollection();
        return organizations.find(combinedQuery).sort(descending("ts"));
    }

    private MongoCollection<Document> getOrganisationsCollection() {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_ORGANISATIONS_NAME);
    }

    private MongoCollection<Document> getRulesCollection() {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_RULES_NAME);
    }
}