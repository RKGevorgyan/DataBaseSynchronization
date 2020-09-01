package com.test;

/**
 * This class Extends @see Exception.
 * It will be thrown if there are duplicates in List.
 *
 */
public class ListDuplicateException extends Exception{
    /**
     * Constructor to get Exception message.
     * @param message A String message that will be shown when creating exception
     */
    public ListDuplicateException(String message) {
        super(message);
    }

    public ListDuplicateException() {
    }
}
