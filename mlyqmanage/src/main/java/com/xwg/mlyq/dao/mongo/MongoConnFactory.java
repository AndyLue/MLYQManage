/*
 * @(#)MongoConnFactory.java  2015年8月26日	
 *
 * Copyright  2015 Chinatopdoc Corporation Copyright (c) All rights reserved.
 */

package com.xwg.mlyq.dao.mongo;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author: Andy_Liu
 * @version: 2015年8月26日
 */
public class MongoConnFactory {
    private static final String MONGODB_DBNAME = "mlyq";
    private static MongoClient mongoClient;

    public static MongoDatabase getDatabase() throws UnknownHostException {
       MongoDatabase conn = null;
       if(mongoClient == null){
           intializeMongoClient();
           MongoClientOptions config = mongoClient.getMongoClientOptions();
//           System.err.println("MONGODB的连接超时为："+config.getConnectTimeout());
//           System.err.println("MONGODB的服务选择超时为："+config.getServerSelectionTimeout());
      }
      String dbName = MongoConnFactory.MONGODB_DBNAME;
//      String username = AppConfig.getValue(Const.MONGODB_USERNAME);
//      String password = AppConfig.getValue(Const.MONGODB_PASSWORD);
      conn = mongoClient.getDatabase(dbName);
//      conn.authenticate(username, password.toCharArray());
      return conn;
      
    }
    
    /**
     * 删除集合
     * 
     * @param name void
     */
    public static void dropCollction(String collctionName){
        try {
            MongoDatabase conn = getDatabase();
            conn.getCollection(collctionName).drop();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    
    public static DB getDB() throws UnknownHostException {
        if(mongoClient == null){
            intializeMongoClient();
            MongoClientOptions config = mongoClient.getMongoClientOptions();
//            System.err.println("MONGODB的连接超时为："+config.getConnectTimeout());
//            System.err.println("MONGODB的服务选择超时为："+config.getServerSelectionTimeout());
       }
       String dbName = MongoConnFactory.MONGODB_DBNAME;
//       String username = AppConfig.getValue(Const.MONGODB_USERNAME);
//       String password = AppConfig.getValue(Const.MONGODB_PASSWORD);
       DB conn = mongoClient.getDB(dbName);
//       conn.authenticate(username, password.toCharArray());
       return conn;
         
       
     }

    private static void intializeMongoClient() throws UnknownHostException {
//          String host = "127.0.";
//          int port = AppConfig.getValueAsInteger(Const.MONGODB_PORT);
        Builder options = MongoClientOptions.builder();
        options.serverSelectionTimeout(1000);
        mongoClient = new MongoClient("127.0.0.1:27017",options.build());
    }

    public  static synchronized void closeConnection(){
      
              if(mongoClient != null){
                  
                  mongoClient.close();
                  
              }
    }
}
