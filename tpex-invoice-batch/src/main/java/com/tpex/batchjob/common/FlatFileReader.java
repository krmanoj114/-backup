package com.tpex.batchjob.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tpex.exception.BatchReadFailedException;

public class FlatFileReader {

	private Logger logger = LoggerFactory.getLogger(FlatFileReader.class);
	
    private String fileName;
    
    private File file;

    private BufferedReader br;
    
    private Integer currentLineNo;
    
    private String [] currentLineTokens;
    
    
    public FlatFileReader(String fileName) {
        this.fileName = fileName;
        this.currentLineNo = 0;
    }

    /**
     * Method to initialize reader.
     * 
     * @throws FileNotFoundException 
     */
    private void initReader() throws FileNotFoundException {

    	if (file == null) {
    		file = new File(fileName);
    	}

    	if (br == null) {
    		br = new BufferedReader(new FileReader(file));
    	}
    }

    /**
     * Method to read whole line (Without breakup).
     * 
     * @return
     * @throws IOException 
     */
    public String readLine() throws IOException  {

    	if (br == null) {
    		initReader();
    	}
    	
    	String line = br.readLine();
		currentLineNo = currentLineNo + 1;
    	
    	if (line == null) {
    		currentLineNo = null;
    		currentLineTokens = null;
    	} else {
    		currentLineTokens = new String[1];
    		currentLineTokens[0] = line;
    	}
    	
    	return line;
    }
    
    /**
     * Method to read line having separator.
     * Individual tokens are returned without trim.
     * 
     * @throws IOException 
     */
    public String [] readLineWithoutTrim(String separator) throws IOException {
    	
    	if (br == null) {
    		initReader();
    	}
    	
    	String line = br.readLine();
    	
    	if (line == null) {
    		currentLineNo = null;
    		currentLineTokens = null;
    	} else {
    		currentLineNo = currentLineNo + 1;
    		currentLineTokens = line.split(separator);
    	}
    	
    	return currentLineTokens;
    }

    /**
     * Method to read line comma separated lines as string array.
     * Individual tokens are trimmed.
     * 
     * @throws IOException
     */
    public String [] readLine(String separator) throws IOException {
    	readLineWithoutTrim(separator);
    	return currentLineTokens == null ? null : Arrays.stream(currentLineTokens).map(String :: trim ).toArray(String [] :: new);
    }

    /**
     * Method to read line having separator.
     * Individual tokens are returned without trim.
     * @throws IOException 
     * 
     * @throws Exception
     */
    public String [] readLineWithoutTrim(int lineLength, int [] [] tokenPositions) throws IOException  {
    	
    	if (br == null) {
    		initReader();
    	}
    	
    	String line = br.readLine();
		currentLineNo = currentLineNo + 1;
    	
    	if (line == null) {
    		currentLineNo = null;
    		currentLineTokens = null;
    	} else if (line.length() < lineLength) {
    		throw new BatchReadFailedException("Invalid length");
    	} else {
    		currentLineTokens = new String[tokenPositions.length];
			for (int i = 0; i < tokenPositions.length; i++) {
				currentLineTokens[i] = line.substring(tokenPositions[i][0], tokenPositions[i][1]);
    		}
    	}
    	
    	return currentLineTokens;
    }

    /**
     * Method to read line having separator.
     * Individual tokens are returned without trim.
     * 
     * @throws IOException 
     */
    public String [] readLine(int lineLength, int [] [] tokenPositions) throws IOException {
    	readLineWithoutTrim(lineLength, tokenPositions);
    	return currentLineTokens == null ? null : Arrays.stream(currentLineTokens).map(String :: trim ).toArray(String [] :: new);
    }
    	
    /**
     * Method to print content of current line.
     * 
     */
    public void printLine() {

    	if (currentLineTokens != null) {

    		logger.info("Start : Line {} of file {}", currentLineNo, fileName);

        	for (String lineToken :  currentLineTokens) {
        		logger.info(lineToken);
        	}
        	
        	logger.info("End : Line {} of file {}", currentLineNo, fileName);
    	}
    }
    
    /**
     * Method to return current line no.
     * 
     * @return
     */
    public Integer getCurrentLineNo() {
    	return currentLineNo;
    }
    
    /**
     * Method to close reader.
     * 
     * @throws IOException 
     */
    public void closeReader() throws IOException {
    	if (br != null) {
    		br.close();
    	}
    }
    
}
