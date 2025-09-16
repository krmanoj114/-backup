package com.tpex.batchjob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.extensions.excel.AbstractExcelItemReader;
import org.springframework.batch.extensions.excel.Sheet;
import org.springframework.core.io.Resource;

public class ExcelSheetItemReader<T> extends AbstractExcelItemReader<T> {

	private Workbook workbook;

    private InputStream inputStream;
    
    private int sheetIndex = 0;

    @Override
    protected Sheet getSheet(final int sheet) {
        return new ExcelSheet(this.workbook.getSheetAt(sheetIndex));
    }

    @Override
    protected int getNumberOfSheets() {
        return 1;
    }
    
    @Override
    protected void doClose() throws Exception {
        super.doClose();
        if (this.inputStream != null) {
            this.inputStream.close();
            this.inputStream = null;
        }

        if (this.workbook != null) {
            this.workbook.close();
            this.workbook = null;
        }
    }
    
    @Override
    protected void openExcelFile(final Resource resource, String password) throws Exception {

        try {
            File file = resource.getFile();
            this.workbook = WorkbookFactory.create(file, password, false);
        }
        catch (FileNotFoundException ex) {
            this.inputStream = resource.getInputStream();
            this.workbook = WorkbookFactory.create(this.inputStream, password);
        }
        this.workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

}
