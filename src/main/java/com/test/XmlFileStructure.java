package com.test;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

/**
 * This class encapsulates xml file structure.
 */

public class XmlFileStructure {
    final static Logger log = Logger.getLogger(XmlFileStructure.class);
    /**
     * Root element of xml file.
     */
    private Element dataBase;
    /**
     * Child element of database.
     */
    private Element table;
    private Document document;

    /**
     * Creates structure of Xml.
     * @param document Argument which is used to create structure of xml.
     */
    void createElements(Document document){
        log.info("Method createElements() called");
        dataBase = document.createElement("database");
        dataBase.setAttribute("name","Company");
        table = document.createElement("table");
        table.setAttribute("name","Structure");
        document.appendChild(dataBase);
        dataBase.appendChild(table);
        log.info("Structure of xml created");
    }

    /**
     * Creates new Document.
     * @return Document object
     * @throws ParserConfigurationException Can be thrown if it's impossible to create Document.
     */
    Document getDocument() throws ParserConfigurationException {
        log.info("Method getDocument() called");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.newDocument();
        log.info("Document created");
        return document;
    }

    /**
     * Getting file name from user and creates xml file.
     * @throws TransformerException Can be thrown if it's impossible to create xml file.
     * @throws FileNotFoundException Can be thrown if file name or path are incorrect,
     */
    void createXml() throws TransformerException, FileNotFoundException {
        log.info("Method createXML() called");
        System.out.println("Write the file name to save copy of database (e.g. \"file.xml\"): ");
        Scanner scanner = new Scanner(System.in);
        log.info("Opening Scanner");
        String xmlName = scanner.next();
        log.info("Getting xml file name from user");
        scanner.close();
        log.info("Closing Scanner");
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT,"yes");
        t.transform(new DOMSource(document),new StreamResult(new FileOutputStream(xmlName)));
        log.info("Xml created");
    }

    /**
     * Creates row elements of xml file with values from database.
     * @see DBConnection
     * @see DBReader
     * @see Document
     * @param rowValue setting row attribute value
     * @param doc object of Document
     * @param DepCode value from database column 1
     * @param DepJob value from database column 2
     * @param Description value from database column 3
     * @return Returning row element
     */
    static Node Row(int rowValue, Document doc, String DepCode, String DepJob, String Description) {
        log.info("Creating rows of xml");
        Element row = doc.createElement("row");
        row.setAttribute("value", String.valueOf(rowValue));
        row.appendChild(getRowElements(doc, "DepCode", DepCode));
        row.appendChild(getRowElements(doc, "DepJob", DepJob));
        row.appendChild(getRowElements(doc, "Description", Description));
        return row;
    }

    private static Node getRowElements(Document doc,String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    /**
     * Getter method for objects element.
     * @return Returning table element.
     */
    public Element getTable() {
        return table;
    }

}
