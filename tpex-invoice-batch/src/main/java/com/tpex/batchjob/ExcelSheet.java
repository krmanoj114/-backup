package com.tpex.batchjob;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.extensions.excel.Sheet;
import org.springframework.lang.Nullable;

/**
 * The Class ExcelSheet.
 */
public class ExcelSheet implements Sheet {

	/** The data formatter. */
	private final DataFormatter dataFormatter = new DataFormatter();

    /** The delegate. */
    private final org.apache.poi.ss.usermodel.Sheet delegate;

    /** The number of rows. */
    private final int numberOfRows;

    /** The name. */
    private final String name;

    /** The evaluator. */
    private FormulaEvaluator evaluator;

    /**
    * Constructor which takes the delegate sheet.
    * @param delegate the apache POI sheet
    */
    ExcelSheet(final org.apache.poi.ss.usermodel.Sheet delegate) {
        super();
        this.delegate = delegate;
        this.numberOfRows = this.delegate.getLastRowNum() + 1;
        this.name = this.delegate.getSheetName();
    }

    /**
     * {@inheritDoc}
     */
     @Override
     public int getNumberOfRows() {
         return this.numberOfRows;
     }

     /**
     * {@inheritDoc}
     */
     @Override
     public String getName() {
         return this.name;
     }

     /**
     * {@inheritDoc}
     */
     @Override
     @Nullable
     public String[] getRow(final int rowNumber) {
         final Row row = this.delegate.getRow(rowNumber);
         return map(row);
     }
     
     /**
      * Map.
      *
      * @param row the row
      * @return the string[]
      */
     @Nullable
     private String[] map(Row row) {
         if (row == null) {
             return null;
         }
         final List<String> cells = new LinkedList<>();
         final int numberOfColumns = row.getLastCellNum();

         for (int i = 0; i < numberOfColumns; i++) {
             Cell cell = row.getCell(i);
             CellType cellType = cell.getCellType();
             if (cellType == CellType.FORMULA) {
                 cells.add(this.dataFormatter.formatCellValue(cell, getFormulaEvaluator()));
             }
             else {
                 cells.add(this.dataFormatter.formatCellValue(cell));
             }
         }
         return cells.toArray(new String[0]);
     }
     
     /**
      * Lazy getter for the {@code FormulaEvaluator}. Takes some time to create an
      * instance, so if not necessary don't create it.
      * @return the {@code FormulaEvaluator}
      */
      private FormulaEvaluator getFormulaEvaluator() {
          if (this.evaluator == null) {
              this.evaluator = this.delegate.getWorkbook().getCreationHelper().createFormulaEvaluator();
          }
          return this.evaluator;
      }

      /**
       * Iterator.
       *
       * @return the iterator
       */
      @Override
      public Iterator<String[]> iterator() {
          return new Iterator<String[]>() {
              private final Iterator<Row> delegateIter = ExcelSheet.this.delegate.iterator();

              @Override
              public boolean hasNext() {
                  return this.delegateIter.hasNext();
              }

              @Override
              public String[] next() {
                  return map(this.delegateIter.next());
              }
          };
      }
      
}
