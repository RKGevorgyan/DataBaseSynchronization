package com.test;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is getting values from Database
 * and convert them to xml.
 * For connection to database, this class uses com.test.DBConnection
 * @see DBConnection
 *
 */
public class DBReader {
    final static Logger log = Logger.getLogger(DBReader.class);

    /**
     * This method is creating xml from database.
     * It can be interrupted if here some problems with
     * Database connection, incorrect file name, parse exception
     * @see XmlFileStructure
     */
    public static void fromDbToXml() {
        String query = "SELECT DepCode, DepJob, Description FROM Company.Structure";
        String query2 = "SELECT * FROM Company.Structure";
        XmlFileStructure xmlFile = new XmlFileStructure();
        log.info("Creating XmlFileReader object");
        try(Connection connection = DBConnection.getConnection()){
            log.info("Getting connection");
            Statement statement = connection.createStatement();
            ResultSet isEmpty = statement.executeQuery(query2);
            log.info("Creating Statement and ResultSet");
            log.info("Checking for records in database");
            if (!isEmpty.next()){
                log.error("Database is empty!");
                System.out.println("Database is empty");
                return;
            }
            Statement statement1 = connection.createStatement();
            ResultSet resultSet = statement1.executeQuery(query);
            log.info("Creating new Statement and ResultSet if database have records");
            Document document = xmlFile.getDocument();
            xmlFile.createElements(document);
            log.info("Creating document and its elements");
            int i = 0;
                while (resultSet.next()) {
                    String DepCode = resultSet.getString(1);
                    String DepJob = resultSet.getString(2);
                    String Description = resultSet.getString(3);
                    xmlFile.getTable().appendChild(XmlFileStructure.Row(i, document, DepCode, DepJob, Description));
                    i++;
                }
                log.info("Moving values from database to xml file");
                xmlFile.createXml();
                log.info("Xml file is created");
        } catch (SQLException e) {
            System.out.println("Check your connection properties");
            log.error(e);
            return;
        } catch (IOException e) {
            System.out.println("Check properties file path");
            log.error(e);
            return;
        } catch (ParserConfigurationException | TransformerException e) {
            log.error(e);
        }
    }
}
