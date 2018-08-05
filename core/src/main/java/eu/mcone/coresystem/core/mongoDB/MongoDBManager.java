/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.mongoDB;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import eu.mcone.coresystem.core.mysql.Database;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class MongoDBManager implements eu.mcone.coresystem.api.core.mongoDB.MongoDBManager {
    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private String username = null;
    @Getter
    private String password = null;
    @Getter
    private String database = null;

    private MongoClient client;
    private MongoDatabase mongoDatabase;

    public MongoDBManager(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public MongoDBManager(final Database database) {
        this.host = database.getHostname();
        this.port = database.getPort();
        this.username = database.getUsername();
        this.password = database.getPassword();
        this.database = database.getDatabase();
    }

    @Override
    public void connect() {
        this.client = MongoClients.create("mongodb://" + host + ":" + port + "");
    }

    @Override
    public void connectAuthentication() {
        this.client = MongoClients.create("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/" + database + "");
        this.mongoDatabase = this.client.getDatabase("mc1system");
    }

    @Override
    public void connectAuthentication(final String username, final String password, final String database) {
        this.client = MongoClients.create("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/" + database + "");
    }

    @Override
    public void closeConnection() {
        this.client.close();
    }

    @Override
    public MongoDatabase getMongoDatabase(final String database) {
        return this.client.getDatabase(database);
    }

    @Override
    public MongoDatabase getMongoDatabase() {
        return this.mongoDatabase;
    }

    @Override
    public void insertDocument(final Document document, final String collection) {
        try {
            System.out.println("Insert new document");
            MongoCollection<Document> insertCollection = getCollection(collection);
            insertCollection.insertOne(document);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertDocuments(final List<Document> documentList, final String collection) {
        try {
            System.out.println("Insert a list of documents");
            MongoCollection<Document> insertCollection = getCollection(collection);
            insertCollection.insertMany(documentList);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDocument(final String fieldName, final Object objectValue, final String updateFieldName, final Object newValue, final String collection) {
        try {
            System.out.println("Update Document equals " + fieldName + " and fieldValue " + objectValue);
            MongoCollection<Document> insertCollection = getCollection(collection);
            UpdateResult updateResult = insertCollection.updateOne(eq(fieldName, objectValue), combine(set(updateFieldName, newValue)));
            System.out.println("[" + collection + "] Update Status: " + updateResult.wasAcknowledged());
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDocument(final String fieldName, final Object objectValue, final HashMap<String, Object> updateHash, final String collection) {
        try {
            System.out.println("Update Document equals " + fieldName + " and fieldValue " + objectValue);
            MongoCollection<Document> insertCollection = getCollection(collection);

            for (HashMap.Entry<String, Object> hash : updateHash.entrySet()) {
                UpdateResult updateResult = insertCollection.updateOne(eq(fieldName, objectValue), combine(set(hash.getKey(), hash.getValue())));
                System.out.println("[" + collection + "] Update Status: " + updateResult.wasAcknowledged());
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replace(final String fieldName, final Object objectValue, final Document replaceDocument, final String collection) {
        try {
            System.out.println("Replace fieldName  " + fieldName + " with Document");
            MongoCollection<Document> insertCollection = getCollection(collection);
            UpdateResult updateResult = insertCollection.replaceOne(eq(fieldName, objectValue), replaceDocument);
            System.out.println("[" + collection + "] Update Status: " + updateResult.wasAcknowledged());
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDocument(final String fieldName, final Object objectValue, final String collection) {
        try {
            System.out.println("Delete document with the fieldName " + fieldName);
            MongoCollection<Document> insertCollection = getCollection(collection);
            DeleteResult deleteResult = insertCollection.deleteOne(eq(fieldName, objectValue));
            System.out.println("[" + collection + "] Update Status: " + deleteResult.wasAcknowledged());
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean containsValue(final Object objectValue, final String collection) {
        try {
            MongoCollection<Document> databaseCollection = getCollection(collection);
            for (Document document : databaseCollection.find()) {
                return document.containsValue(objectValue);
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getObject(final String field, final Object value, final String valueField, final String collection) {
        MongoCollection<Document> databaseCollection = getCollection(collection);
        for (Document objectDocument : databaseCollection.find(eq(field, value))) {
            return objectDocument.get(valueField);
        }
        return null;
    }



    @Override
    public FindIterable<Document> getDocumentsInCollection(final String collection) {
        MongoCollection<Document> databaseCollection = getCollection(collection);
        return databaseCollection.find();
    }

    @Override
    public MongoCollection<Document> getCollection(final String collection) {
        try {
            return this.mongoDatabase.getCollection(collection);
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MongoClient getClient() {
        return this.client;
    }
}
