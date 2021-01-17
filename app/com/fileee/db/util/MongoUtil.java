package com.fileee.db.util;

import com.fileee.db.connector.MongoConnector;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MongoUtil {

    private MongoCollection<Document> getCollection(String db, String collection) {
        MongoClient mongoClient = MongoConnector.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(db);
        return database.getCollection(collection);
    }

    public Document createDocument(String dbName, String collectionName, Document input) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        collection.insertOne(input);
        return input;
    }

    public Document updateDocument(String dbName, String collectionName, Bson criteria, Document input) {
        FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
        options.upsert(true);
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        return collection.findOneAndReplace(criteria, input, options);
    }

    public Document deleteDocument(String dbName, String collectionName, Bson criteria) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        return collection.findOneAndDelete(criteria);
    }

    public List<Map> fetchDocuments(String dbName, String collectionName) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        return collection.find(new Document(), Map.class).into(new ArrayList<Map>());
    }

    public Map<String, Object> fetchDocumentById(String dbName, String collectionName, Bson criteria) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        return collection.find(criteria, Map.class).first();
    }

    public Document partialUpdateDocument(String dbName, String collectionName, Bson criteria, Bson input) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        return collection.findOneAndUpdate(criteria, input);
    }


//    public int generateSequence(String seqName) {
//        MongoCollection<Document> sequence = getCollection("payroll", "sequence");
//        Document query = new Document("_id", seqName);
//        Document increase = new Document("seq", 1);
//        Document update = new Document("$inc", increase);
//        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
//        options.returnDocument(ReturnDocument.AFTER);
//        options.upsert(true);
//        Document counterDoc = sequence.findOneAndUpdate(query, update, options);
//        return counterDoc != null ? counterDoc.getInteger("seq") : 1;
//    }

}
