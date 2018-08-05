/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.mongoDB;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;

public interface MongoDBManager {

    void connect();

    void connectAuthentication();

    void connectAuthentication(final String username, final String password, final String database);

    void closeConnection();

    void insertDocument(final Document document, final String collection);

    void insertDocuments(final List<Document> documentList, final String collection);

    void updateDocument(final String fieldName, final Object fieldValue, final String updateFieldName, final Object newValue, final String collection);

    void updateDocument(final String fieldName, final Object objectValue, final HashMap<String, Object> updateHash, final String collection);

    void replace(final String fieldName, final Object objectValue, final Document replaceDocument, final String collection);

    void deleteDocument(final String fieldName, final Object objectValue, final String collection);

    Boolean containsValue(final Object objectValue, final String collection);

    FindIterable<Document> getDocumentsInCollection(final String collection);

    Object getObject(final String field, final Object value, final String valueField, final String collection);

    MongoCollection<Document> getCollection(final String key);

    MongoDatabase getMongoDatabase(final String key);

    MongoDatabase getMongoDatabase();

    MongoClient getClient();

}
