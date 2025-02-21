package se.evol.querybasedRuleRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;


@ApplicationScoped
public class DocumentRepo {
    @Inject
    MongoClient mongoClient;
    @ConfigProperty(name = "database.name")
    String DATABASE_NAME;

    @ConfigProperty(name = "database.collection.rules.name")
    String COLLECTION_RULES_NAME;

    @ConfigProperty(name = "database.collection.organisations.name")
    String COLLECTION_ORGANISATIONS_NAME;


    public Optional<Document> findLatestOrgById(String orgId) {
        MongoCollection<Document> organizations = getOrganisationsCollection();
        FindIterable<Document> searchResult = organizations.find(eq("organisation.orgNr", orgId)).sort(descending("ts"));
        return Optional.ofNullable(searchResult.first());
    }

    public Collection<String> getAllOrganisations() {
        MongoCollection<Document> organizations = getOrganisationsCollection();
        FindIterable<Document> searchResult = organizations.find().sort(descending("ts"));
        return searchResult.map(Document::toJson).into(new ArrayList<String>());
    }

    public String saveOrganisation(String stringDoc) {
        MongoCollection<Document> organizations = getOrganisationsCollection();
        Document doc = Document.parse(stringDoc);
        doc.put("ts", new Date());
        BsonValue insertId = organizations.insertOne(doc).getInsertedId();
        return Objects.requireNonNull(insertId).asObjectId().getValue().toString();
    }

    public Optional<Document> findLatestRuleVersionById(String ruleId) {
        MongoCollection<Document> rules = getRulesCollection();
        FindIterable<Document> searchResult = rules.find(eq("rule.id", ruleId)).sort(descending("ts"));
        return Optional.ofNullable(searchResult.first());
    }

    public FindIterable<Document> findAllRules() {
        MongoCollection<Document> rules = getRulesCollection();
        return rules.find()
                .sort(descending("ts"));
    }

    public String saveRule(String stringDoc) {
        MongoCollection<Document> rules = getRulesCollection();
        Document doc = Document.parse(stringDoc);
        doc.put("ts", new Date());
        BsonValue newId = rules.insertOne(doc).getInsertedId();
        return Objects.requireNonNull(newId).toString();
    }

    public FindIterable<Document>  runQueryForOrgNr(RulesModel rule, String internalId) {
        BsonDocument freeformQuery = BasicDBObject.parse(rule.query()).toBsonDocument();
        Bson combinedQuery = and(Filters.eq("_id", new ObjectId(internalId)), freeformQuery);
        MongoCollection<Document> organizations = getOrganisationsCollection();
        return organizations.find(combinedQuery).sort(descending("ts"));
    }

    public FindIterable<Document>  runQueryForAll(RulesModel rule) {
        BsonDocument freeformQuery = BasicDBObject.parse(rule.query()).toBsonDocument();
        Bson projectionDef = include("_id", "organisation.orgNr");
        MongoCollection<Document> organizations = getOrganisationsCollection();
        return organizations.find(freeformQuery).projection(projectionDef).sort(descending("orgNr"));
    }

    private MongoCollection<Document> getOrganisationsCollection() {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_ORGANISATIONS_NAME);
    }

    private MongoCollection<Document> getRulesCollection() {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_RULES_NAME);
    }
}