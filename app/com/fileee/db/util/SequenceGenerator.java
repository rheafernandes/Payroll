package com.fileee.db.util;

import com.fileee.db.connector.MongoConnector;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;

public class SequenceGenerator {

    public MongoCollection<Document> getCollection(String db, String collection) {
        MongoClient mongoClient = MongoConnector.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(db);
        return database.getCollection(collection);
    }

    public int generateSequence(String seqName) {
        MongoCollection<Document> sequence = getCollection("payroll", "sequence");
        Document query = new Document("_id", seqName);
        Document increase = new Document("seq", 1);
        Document update = new Document("$inc", increase);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);
        options.upsert(true);
        Document counterDoc = sequence.findOneAndUpdate(query, update, options);
        return counterDoc != null ? counterDoc.getInteger("seq") : 1;
    }
}
