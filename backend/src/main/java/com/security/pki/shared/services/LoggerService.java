package com.security.pki.shared.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@Service
public class LoggerService {
    private static final Logger logger = Logger.getLogger(LoggerService.class.getName());
    private final FileHandler fileHandler= new FileHandler("log%g.log",15000000,40,true);

    public LoggerService() throws IOException {
        this.init();
    }

    public void init(){
        logger.addHandler(fileHandler);
    }

    public void withRecord(LogRecord log){
        logger.log(log);
    }

    public void info(String log){
        logger.info(log);
    }

    public void warning(String log){
        logger.warning(log);
    }

    public void severe(String log){
        logger.severe(log);
    }
}
