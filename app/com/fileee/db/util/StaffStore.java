package com.fileee.db.util;

import com.fileee.models.mongo.Staff;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

public class StaffStore extends MongoUtil {

    private static final String db = "payroll";
    private static final String collection = "staff";
    private static final String primaryKey = "_id";
    private SequenceGenerator sequenceGenerator = new SequenceGenerator();

    public StaffStore() {
    }

    public int createStaff(Map<String, Object> data) {
        Document document = new Document();
        data.forEach((key, value) -> document.append(key, value));
        document.put(primaryKey, sequenceGenerator.generateSequence(Staff.SEQUENCE_NAME));
        Document added = createDocument(db, collection, document);
        return (int) added.get("_id");
    }

    public void updateStaff(int id, Map<String, Object> data) {
        Document document = new Document();
        data.forEach((key, value) -> document.append(key, value));
        Bson criteria = Filters.eq(primaryKey, id);
        updateDocument(db, collection, criteria, document);

    }

    public void deleteStaff(int id) {
        Document document = new Document(primaryKey, id);
        deleteDocument(db, collection, document);
    }

    public List<Map> fetchStaff() {
        return fetchDocuments(db, collection);
    }

    public Map<String, Object> fetchStaffById(int id) {
        Document query = new Document(primaryKey, id);
        return fetchDocumentById(db, collection, query);
    }

    public Document addWorkLog(int id, Map<String, Object> workLog) {
        Bson criteria = Filters.eq(primaryKey, id);
        Bson updateValue = Updates.set("workLog", workLog);
        return partialUpdateDocument(db, collection, criteria, updateValue);
    }

}
