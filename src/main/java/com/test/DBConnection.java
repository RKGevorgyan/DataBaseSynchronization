package com.test;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * This static class is getting connection to Database.
 * Connection parameters are in @see data.property.
 */
public class DBConnection {
    final static Logger log = Logger.getLogger(DBConnection.class);

    /**
     * This static method uses data.property
     * to get connection to Database
     * @return Connection
     * @throws SQLException Can be thrown if connection parameters are incorrect.
     * @throws IOException Can be thrown if file path is incorrect.
     */
    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        String separator = File.separator;
        String path = "src"+separator+"main"+separator+"resources"+separator+"data.properties";
        InputStream in = new FileInputStream(path);
            props.load(in);
            log.info("Getting connection properties from data.properties");
        String url = props.getProperty("url");
        String user = props.getProperty("user");
        String password = props.getProperty("password");
        return DriverManager.getConnection(url,user,password);
    }

}