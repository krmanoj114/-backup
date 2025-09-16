package com.tpex.batchjob.common;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvFileReader {

	private Logger logger = LoggerFactory.getLogger(CsvFileReader.class);
	
    private String fileName;
    
    private File file;

    private CSVReader csvReader;
    
    private Integer currentLineNo;
    
    private String [] currentLineContent;
    
    public CsvFileReader(String fileName) {
        this.fileName = fileName;
        currentLineNo = 0;
    }

    /**
     * Method to initialize reader.
     * 
     * @throws IOException
     */
    private void initReader() throws IOException {

    	if (file == null) {
    		file = new File(fileName);
    	}

    	if (csvReader == null) {
    		csvReader = new CSVReader(new FileReader(file));
    	}
    }
    
    /**
     * Method to read line comma separated lines as string array.
     * Individual tokens are returned without trim.
     * 
     * @throws IOException, CsvValidationException 
     */
    public String [] readLineWithoutTrim() throws IOException, CsvValidationException {
    	
    	if (csvReader == null) {
    		initReader();
    	}

		currentLineContent = csvReader.readNext();
    	
    	if (currentLineContent == null) {
    		currentLineNo = null;
    	} else {
    		currentLineNo = currentLineNo + 1;
    	}
    	
    	return currentLineContent;
    }

    /**
     * Method to read line comma separated lines as string array.
     * Individual tokens are trimmed.
     * @throws CsvValidationException 
     * 
     * @throws Exception
     */
    public String [] readLine() throws IOException, CsvValidationException {
    	readLineWithoutTrim();
    	return currentLineContent == null ? null : Arrays.stream(currentLineContent).map(String :: trim ).toArray(String [] :: new);
    }
    
    
    /**
     * Method to print content of current line.
     * 
     */
    public void printLine() {

    	if (currentLineContent != null) {

    		logger.info("Start : Line {} of file {}", currentLineNo, fileName);

        	for (String lineToken :  currentLineContent) {
        		logger.info(lineToken);
        	}
        	
        	logger.info("End : Line {} of file {}", currentLineNo, fileName);
    	}
    }
    
    /**
     * Method to close reader.
     * 
     * @throws IOException
     */
    public void closeReader() throws IOException {
    	if (csvReader != null) {
    		csvReader.close();
    	}
    }
}
