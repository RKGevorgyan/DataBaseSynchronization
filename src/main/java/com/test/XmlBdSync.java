package com.test;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class is uses static method to synchronize
 * database with given xml file.
 * Structure of xml must be the same as generated from MainBDReader.
 * If connection to database will be lost, update operations
 * will be canceled.
 * @see DBReader
 *
 */

public class XmlBdSync {
    final static Logger log = Logger.getLogger(XmlBdSync.class);

    /**
     * This method gives synchronization algorithm with safe Update operations.
     * It uses Scanner to get file name and parse values.
     * Synchronize() method using natural key of database(DepCode,DepJob)
     * to Insert,Update and Delete rows.
     * If database is empty, this method will Insert all values
     * from given xml file to database.
     * @see XmlFileStructure
     * @see DBConnection
     * @see ParseXml
     * @see Scanner
     * @throws IOException  Can be thrown If such file doesn't exist.
     * @throws SQLException Can be thrown if connection is lost or there are
     *                      incorrect sql query.
     */
    public static void synchronize() throws IOException, SQLException, ListDuplicateException {
        log.info("Method synchronize called");
        System.out.println("write the name of file to use for synchronization (e.g. \"file.xml\"):");
        Scanner scanner = new Scanner(System.in);
        log.info("Opening Scanner");
        String xmlName = scanner.next();
        log.info("Getting xml file name from user");
        scanner.close();
        log.info("Closing Scanner");
        ParseXml xml = new ParseXml();
        log.info("Creating new com.test.ParseXml object");
        File file = new File(xmlName);
        log.info("Creating new File "+xmlName);
        xml.parseXML(file);
        List<String> listDepCode = xml.getListDepCode();
        List<String> listDepJob = xml.getListDepJob();
        List<String> listDescription = xml.getListDescription();
        log.info("Getting values from xml "+xmlName);

        List<String> dbDepCode = new ArrayList<>();
        List<String> dbDepJob = new ArrayList<>();
        List<String> dbDescription = new ArrayList<>();
        log.info("Creating three ArrayList to save values from database");
        try(Connection connection = DBConnection.getConnection()){
            log.info("Getting connection to database in try with resources");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT DepCode, DepJob, Description FROM Company.Structure");
            log.info("Sending sql query to database");
            if (!resultSet.next()) {
                log.info("Checking if database is empty");
                for (int i = 0; i < listDepCode.size(); i++) {
                    if(listDescription.get(i)==null){
                        statement.execute(
                                "INSERT INTO Company.Structure(DepCode,DepJob) " +
                                        "VALUES ('" + listDepCode.get(i) + "','" + listDepJob.get(i)
                                        + "')");
                        log.info("Inserting values to database from xml "+xmlName);
                    }
                    statement.execute(
                            "INSERT INTO Company.Structure(DepCode,DepJob,Description) " +
                                    "VALUES ('" + listDepCode.get(i) + "','" + listDepJob.get(i)
                                    + "','" + listDescription.get(i) + "')");
                    log.info("Inserting values to database from xml "+xmlName);
                }
            }
            else {
                Statement statement1 = connection.createStatement();
                ResultSet resultSet1 = statement1.executeQuery("SELECT DepCode, DepJob, Description FROM Company.Structure");
                log.info("Creating new Statement and ResultSet if database is not empty");
                while (resultSet1.next()) {
                    dbDepCode.add(resultSet1.getString(1));
                    dbDepJob.add(resultSet1.getString(2));
                    dbDescription.add(resultSet1.getString(3));
                }
                log.info("Saving values from database to created ArrayLists");
                try {
                    connection.setAutoCommit(false);
                    log.info("Setting AutoCommit to false");
                    for (int i = 0; i < listDepCode.size(); i++) {
                        for (int j = 0; j < dbDepCode.size(); j++) {
                            if (listDepCode.get(i).equals(dbDepCode.get(j))) {
                                if (!listDepJob.get(i).equals(dbDepJob.get(j))) {
                                    if (listDescription.get(i)==null){
                                        statement.execute("update Company.Structure" +
                                                " set DepJob = '"+listDepJob.get(i) + "'"
                                                + " WHERE DepCode =" + listDepCode.get(i));
                                    }
                                    statement.execute("update Company.Structure" +
                                            " set DepJob = '" + listDepJob.get(i) + "'," + "Description = '"
                                            + listDescription.get(i) + "'" +
                                            " WHERE DepCode =" + listDepCode.get(i));
                                }
                            }
                        }
                    }
                    log.info("Updating database if DepCode is similar to  DepCode from xml but DepJob is different");
                    for (int k = 0; k < dbDepCode.size(); k++) {
                        if (!listDepCode.contains(dbDepCode.get(k))) {
                            statement.execute("DELETE FROM Company.Structure Where DepCode=" + dbDepCode.get(k));
                        }
                    }
                    log.info("Deleting rows from database if they are not in xml "+xmlName);
                    for (int l = 0; l < listDepCode.size(); l++) {
                        if (!dbDepCode.contains(listDepCode.get(l))) {
                            if (listDescription.get(l)==null){
                                statement.execute("INSERT INTO Company.Structure(DepCode,DepJob) " +
                                        "VALUES ('" + listDepCode.get(l) + "','" + listDepJob.get(l)+"')");
                            }
                            statement.execute("INSERT INTO Company.Structure(DepCode,DepJob,Description) " +
                                    "VALUES ('" + listDepCode.get(l) + "','" + listDepJob.get(l) + "','" + listDescription.get(l) + "')");
                        }
                    }
                    log.info("Inserting values from xml if they are not in database");
                    connection.commit();
                    log.info("Commiting connection");
                } catch (SQLException e){
                    connection.rollback();
                    log.error(e);
                }
            }
        }
    }

}
