package com.fileee.db.connector;

import com.fileee.exceptions.ServerException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MongoConnector {

    private static String mongoIP = "mongodb+srv://test:Test1234@cluster0.wlbme.mongodb.net/test?retryWrites=true&w=majority";
    private static MongoClient mongoClient;
    private static final Logger log = LoggerFactory.getLogger(MongoConnector.class);


    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
            log.info("MongoConnector::getMongoClient:: Trying to create a new Mongodb client");
            try {
                mongoClient = MongoClients.create(mongoIP);
            } catch (Exception e) {
                log.error("MongoConnector::getMongoClient:: Unable to make a database connection, please try again later" + e.getMessage());
                throw new ServerException("ERR_DB_CONNECTION", "Unable to make a database connection, please try again later.");
            }
        }
        return mongoClient;
    }

    private MongoConnector() {

    }

}
