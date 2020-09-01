package com.test;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Main class to start synchronization
 * Contains main method
 */
public class SynchronizeMain {
    final static Logger log = Logger.getLogger(SynchronizeMain.class);
    /**
     * start point of synchronization
     * @param args Argument from command line
     */
    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        System.out.println("Write function 'read' to create xml from db");
        System.out.println("Write function 'synchronize' to update database");
        String str = scanner.nextLine();
        if (str.equals("synchronize")) {
            try {
                XmlBdSync.synchronize();
            } catch (IOException e) {
                log.error(e);
            } catch (SQLException exception) {
                log.error(exception);
            } catch (ListDuplicateException e) {
                log.error("Duplicates found" + e);
                e.printStackTrace();
            }
        } else if (str.equals("read"))
            DBReader.fromDbToXml();
    }
}
