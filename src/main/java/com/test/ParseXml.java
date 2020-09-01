package com.test;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * The com.test.ParseXml class represents
 * functionality of getting values from .xml File
 * @see XmlFileStructure
 */
public class ParseXml {
    final static Logger log = Logger.getLogger(ParseXml.class);
    private List<String> listDepCode = new ArrayList<>();
    private List<String> listDepJob = new ArrayList<>();
    private List<String> listDescription = new ArrayList<>();

    /**
     * This method creates list with values from
     * given xml file and saves them in three different ArrayList
     * @param file xml file with the same structure as created file from database
     *             if file structure or name are incorrect it can be reason of
     *             generating error message
     *
     * @see XmlFileStructure
     */
    void parseXML(File file) throws ListDuplicateException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder = null;
        try {
            docbuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error(e);
            return;
        }
        Document document = null;
        try {
            document = docbuilder.parse(file);
        } catch (SAXException | IOException e) {
            log.error(e);
            log.info("Check your file name "+file);
            return;
        }
        log.info("Object document created by using "+file);
        Element element = document.getDocumentElement();
        log.info("Object Element created");
        NodeList node = element.getElementsByTagName("table");
        log.info("NodeList created");
        for (int i=0; i< node.getLength();i++){
            Element depCodeEl = (Element) node.item(i);
            NodeList rows = depCodeEl.getElementsByTagName("row");
            for (int j=0;j< rows.getLength();j++){
                Element all = (Element) rows.item(j);
                String depCode = all.getElementsByTagName("DepCode")
                        .item(0)
                        .getFirstChild().getNodeValue();
                String depJob = all.getElementsByTagName("DepJob")
                        .item(0)
                        .getFirstChild().getNodeValue();
                String description=null;
                if ((all.getElementsByTagName("Description")
                        .item(0)
                        .hasChildNodes())) {
                    description = all.getElementsByTagName("Description")
                            .item(0)
                            .getFirstChild().getNodeValue();
                }

                listDepCode.add(depCode);
                listDepJob.add(depJob);
                listDescription.add(description);
            }
            log.info("DepCode, DepJob, Description added to corresponding list");
        }
            log.info("Checking for duplicates");
            findDuplikates(listDepCode,listDepJob);
    }

    /**
     * This method finding duplicates in List
     * when list1 has duplicates at some index then <code>findDuplicates<code/>
     * searching of duplicates in list2 with same index
     * If list2 has duplicates it throws com.test.ListDuplicateException
     * @see ListDuplicateException
     * @param list1 ArrayList created from xml file value of DepCode
     * @param list2 ArrayList created from xml file value of DepJob
     * @throws ListDuplicateException Can be thrown if list1 and list 2 have duplicates
     */
    private static void findDuplikates(List<String> list1,List<String> list2) throws ListDuplicateException {
        for (int i =0; i<list1.size();i++){
            for (int j=i+1;j<list1.size();j++)
                if (list1.get(i).equals(list1.get(j)))
                    if (list2.get(i).equals(list2.get(j)))
                    throw new ListDuplicateException("DepCode and DepJob must be unique");
        }
    }

    /**
     * Getter for field listDepCode
     * @return listDepCode
     */
    public List<String> getListDepCode(){
        return listDepCode;
    }

    /**
     * Getter for field listDepJob
     * @return listDepJob
     */
    public List<String> getListDepJob() {
        return listDepJob;
    }

    /**
     * Getter for field listDescription
     * @return listDescription
     */
    public List<String> getListDescription() {
        return listDescription;
    }
}
