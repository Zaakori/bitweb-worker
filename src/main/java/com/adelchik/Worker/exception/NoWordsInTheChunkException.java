package com.adelchik.Worker.exception;

public class NoWordsInTheChunkException extends Exception {
    public NoWordsInTheChunkException(){
        super("This file contained no words.");
    }
}
