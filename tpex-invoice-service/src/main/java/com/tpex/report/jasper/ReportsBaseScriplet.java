package com.tpex.report.jasper;

import java.net.URLEncoder;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


import net.sf.jasperreports.engine.JRDefaultScriptlet;
import org.apache.commons.lang3.StringUtils;


/**
 * Base scriptlet class for all reports.
 * Specific report scriptlet class with extend this this.
 *  
 */
public class ReportsBaseScriplet extends JRDefaultScriptlet
{
	protected static final String RESOURCE_FILE_NM  = "ROEM";
	protected static final String BLANK       = "";
	protected static String SPACE             = " ";
	protected static String DATE_SEPERATOR    = "/";
	protected static String DASH    = "-";
	protected static final String START_CHAR = "{";
	protected static final String END_CHAR   = "~ ";
	protected static final String CHAR_174   = "\u00AE";
	/**
	 * Function to get the Description based on the passed parameters.
	 * 
	 * @param astrCode
	 * @param astrDesc
	 * @return
	 */
	public String getDescription(String astrCode, String astrDesc)
	{
		if (astrDesc == null || astrDesc.isEmpty()) {
			return astrCode;
		}
		else {
			return astrCode + SPACE + astrDesc;
		}
	}
	
	/**
	 * Convert ddmmyyyy format to dd/mm/yyyy.
	 * 
	 * @param astrDate data in format ddmmyyyy
	 * @return String date in format dd/mm/yyyy
	 */
	
	public String getDateFormat(String astrDate)
	{
		if (astrDate == null || astrDate.isEmpty()) {
			return BLANK;
		} else if (astrDate.length() < 8) {
			return "Invalid date.";
		} 
		else {
			return 
				astrDate.substring(0, 2) + DATE_SEPERATOR + 
				astrDate.substring(2, 4) + DATE_SEPERATOR + 
				astrDate.substring(4); 
		}
	}
	
	
	
	

	/**
	 * Get month name based on month index.
	 * 
	 * @param astrMonthIndex - String index of month like 01 for January, 02 for February and so on.
	 * @param ablnIsFullMonth - true means return full month, false means short month.
	 * 
	 * @return Month name.
	 */
	public String getMonthName(String astrMonthIndex, boolean ablnIsFullMonth)
	{
		int lintMonthIndex;
        String lstrMonthName;
		
		try {
			lintMonthIndex = Integer.parseInt(astrMonthIndex);
	        
			if (lintMonthIndex < 1 || lintMonthIndex > 12) {
				lstrMonthName = "Invalid Month Index.";
			} else {
		        lstrMonthName = ablnIsFullMonth?
    					(new DateFormatSymbols()).getMonths()[lintMonthIndex-1]:
    					(new DateFormatSymbols()).getShortMonths()[lintMonthIndex-1];
			}
		} catch (Exception e) {
			//e.printStackTrace();
			lstrMonthName = "Invalid Month.";
		}
		
        return lstrMonthName;
    }
	
	
	/**
	 * Get full month name based on month index.
	 * 
	 * @param astrMonthIndex - String index of month like 01 for January, 02 for February and so on.
	 * 
	 * @return Month name.
	 */
	public String getMonthName(String astrMonthIndex)
	{
		return  getMonthName(astrMonthIndex, true);
    }
	
	
	/**
	 * Get previous month name based on month index.
	 * 
	 * @param aintMonth 
	 * @return lstrMonth
	 */

	public String getNthPrevMonth(int aintMonth) {
		
	   Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -aintMonth);
		String lstrMonth = getMonthName((c.get(Calendar.MONTH) + 1) + "", false);
		return lstrMonth;
	}
	
	
	/**
	 * Get formatted date on the basis of passed parameter.
	 * 
	 * @param astrDate : String Date to be Converted
	 * @param astrDate : String Date Format
	 * @param astrDate : String Date Format to be Converted 
	 * @return lstrDate
	 */

	public String getConvDateFormat(String astrDate, String astrFromFrmt, String astrToFrmt ) {
		
		String lstrDate = BLANK;
		
		SimpleDateFormat lsdfFromFrmt = new SimpleDateFormat(astrFromFrmt);
		SimpleDateFormat lsdfToFrmt = new SimpleDateFormat(astrToFrmt);
		
		try {
			 
			Date date = lsdfFromFrmt.parse(astrDate);
			lstrDate = lsdfToFrmt.format(date);
	 
		} catch (ParseException e) {
			return "Invalid Date Format";
		}
		
		return lstrDate;
	}
	
	/**
	 * Get Full & Short Thai Month Name on the basis of passed Month Index parameter.
	 * 
	 * @param astrMonthIndex : String Month Index
	 * @param ablnIsFullMonth : true - Full Thai Month
	 * 						    false - Short Thai Month
	 * @return lstrMonthName
	 */
	public String getThaiMonthName(String astrMonthIndex, boolean ablnIsFullMonth)
	{
		int lintMonthIndex;
		String lstrMonthName=null;

		try {
			lintMonthIndex = Integer.parseInt(astrMonthIndex);

			if (lintMonthIndex < 1 || lintMonthIndex > 12) {
				lstrMonthName= "Invalid Month Index.";
			} 
			else if(ablnIsFullMonth) {
				if( 1 == lintMonthIndex)
				{
					return "\u0E21\u0E01\u0E23\u0E32\u0E04\u0E21";
				}
				else if(2 == lintMonthIndex)
				{
					return "\u0E01\u0E38\u0E21\u0E20\u0E32\u0E1E\u0E31\u0E19\u0E18\u0E4C";
				}
				else if(3 == lintMonthIndex)
				{
					return "\u0E21\u0E35\u0E19\u0E32\u0E04\u0E21";
				}
				else if(4 == lintMonthIndex)
				{
					return "\u0E40\u0E21\u0E29\u0E32\u0E22\u0E19";
				}
				else if(5 == lintMonthIndex)
				{
					return "\u0E1E\u0E24\u0E29\u0E20\u0E32\u0E04\u0E21";
				}
				else if(6 == lintMonthIndex)
				{
					return "\u0E21\u0E34\u0E16\u0E38\u0E19\u0E32\u0E22\u0E19";
				}
				else if(7 == lintMonthIndex)
				{
					return "\u0E01\u0E23\u0E01\u0E0E\u0E32\u0E04\u0E21";
				}
				else if(8 == lintMonthIndex)
				{
					return "\u0E2A\u0E34\u0E07\u0E2B\u0E32\u0E04\u0E21";
				}
				else if(9 == lintMonthIndex)
				{
					return "\u0E01\u0E31\u0E19\u0E22\u0E32\u0E22\u0E19";
				}
				else if(10 == lintMonthIndex)
				{
					return "\u0E15\u0E38\u0E25\u0E32\u0E04\u0E21";
				}
				else if(11 == lintMonthIndex)
				{
					return "\u0E1E\u0E24\u0E28\u0E08\u0E34\u0E01\u0E32\u0E22\u0E19";
				}
				else if(12 == lintMonthIndex)
				{
					return "\u0E18\u0E31\u0E19\u0E27\u0E32\u0E04\u0E21";
				}
			} else  {
				if(1 == lintMonthIndex)
				{
					return "\u0E21.\u0E04.";
				}
				else if(2 == lintMonthIndex)
				{
					return  "\u0E01.\u0E1E." ;
				}
				else if(3 == lintMonthIndex)
				{
					return  "\u0E21\u0E35.\u0E04." ;
				}
				else if(4 == lintMonthIndex)
				{
					return  "\u0E40\u0E21.\u0E22." ;
				}
				else if(5 == lintMonthIndex)
				{
					return  "\u0E1E.\u0E04." ;
				}
				else if(6 == lintMonthIndex)
				{
					return  "\u0E21\u0E34.\u0E22." ;
				}
				else if(7 == lintMonthIndex)
				{
					return  "\u0E01.\u0E04." ;
				}
				else if(8 == lintMonthIndex)
				{
					return  "\u0E2A.\u0E04." ;
				}
				else if(9 == lintMonthIndex)
				{
					return  "\u0E01.\u0E22." ;
				}
				else if(10 == lintMonthIndex)
				{
					return  "\u0E15.\u0E04." ;
				}
				else if(11 == lintMonthIndex)
				{
					return  "\u0E1E.\u0E22." ;
				}
				else if(12 == lintMonthIndex)
				{
					return  "\u0E18.\u0E04." ;
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			lstrMonthName= "Invalid Month.";
		}

		return lstrMonthName;
	}
	
	/**
	 * Get Full Thai Month Name on the basis of passed Month Index parameter.
	 * 
	 * @param astrMonthIndex : String Month Index
	 * @return lstrMonthName
	 */
	
	public String getThaiMonthName(String astrMonthIndex)
	{
		return getThaiMonthName(astrMonthIndex,true);
	}
	
	/**
     * Extract tokens as a String[].
     * Uses java.util.StringTokenizer to extract the strings using the delimiter
     * and to populate the string array.
     * This is to be used (and not extractTokens) when even the spaces in the string being passed
     * are not to be ignored, assigns a blank("") value to such an element when added in the populated array
     * <br>e.g.<br>
     * astr - "niit/admin//project"<br>
     * astrDelim - "/"<br>
     * result string array - {"niit","admin", "", "project"}<br>
     *
     * @param String - astrInput containing tokens
     * @param String - astrDelim containing delimiter
     * @return String[]
     */
   public static String[] extractTokensWithSpaces( String astrInput, String astrDelim )
   {
       if ((astrInput==null)||(astrDelim==null))
           return null;
         
       int lintEndIndex    = -1;
       int lintElements    = 0;
       int lintCtr         = 0;
       int lintDelimLength = astrDelim.length();
       int lintStartIndex  = -(lintDelimLength);
       int lintIndex       = -(lintDelimLength);
         
       String lstrElement   = null;
       String lstrLastValue = null;

       do {
           lintIndex = astrInput.indexOf(astrDelim,lintIndex+lintDelimLength);
           if (lintIndex!=-1) {
               lintElements++;
           }
       } while (lintIndex!=-1);
         
       String[] strArrRet = new String[lintElements+1];
       
       for (int i=0;i<strArrRet.length;i++ ) {
           strArrRet[i]= BLANK;
       }

       do {
           lintEndIndex = astrInput.indexOf(astrDelim,lintStartIndex+lintDelimLength);
           
           if (lintEndIndex!=-1) {
               lstrElement = astrInput.substring(lintStartIndex+lintDelimLength,lintEndIndex);
               if (!((lstrElement==null)||(lstrElement.equals(""))||(lstrElement.length()==0))) {
                   strArrRet[lintCtr] = lstrElement;
               } else { //For null values, return blank
                   strArrRet[lintCtr] = BLANK;
               }
               lintCtr++;
           } else {
               lstrLastValue = astrInput.substring(lintStartIndex+lintDelimLength,astrInput.length());
               if ((lstrLastValue!=null)&&!(lstrLastValue.equals(""))) {
                    strArrRet[lintCtr] = lstrLastValue;
               }
           }

           lintStartIndex = lintEndIndex;
       } while (lintEndIndex!=-1);
       
       return strArrRet;
   }
   
    public boolean putValueInMap(Map amapReport, String astrKey, Object aobjValue, boolean astrShow)
	{
		  if (amapReport.containsKey(astrKey)) {
				 amapReport.remove(astrKey);
		  }
		  
		  amapReport.put(astrKey, aobjValue);
		  return astrShow;
	}   
       
    public Object getValueFromMap(Map amapReport, String astrKey)
	{
		  if (amapReport.containsKey(astrKey)) {
				 return amapReport.get(astrKey);
		  } else {
				 return null;
		  }
	}
    
	/**
	 * This method is to get difference between two dates in No. of days. 
	 * @param astrFromDt
	 * @param astrFrmtFromDt
	 * @param astrToDt
	 * @param astrFrmtToDt
	 * @return days
	 */
	
	public int getDateDiff(String astrFromDt, String astrFrmtFromDt,
							  String astrToDt, String astrFrmtToDt)
	{
		
		SimpleDateFormat frmtFromDt = new SimpleDateFormat(astrFrmtFromDt);
		SimpleDateFormat frmtToDt = new SimpleDateFormat(astrFrmtToDt);
		 
		Date ldtFromDt = null;
		Date ldtToDt = null;
		
		long diff = 0;
		long days = 0;
 
		try {
			ldtFromDt = frmtFromDt.parse(astrFromDt);
			ldtToDt = frmtToDt.parse(astrToDt);

			diff= (ldtToDt.getTime()- ldtFromDt.getTime());

			days = diff / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return (int) days;
		
	}
	
    /*To Print DSI No based on DSI length.
     * @param astrDSINo
     * @param astrDSISubStr : true:- For printing Full length(12) DSI No. 
     *                        false:-For printing DSI No upto length 9.
   */
   
   public String getDSINo(String astrDSINo,boolean astrDSISubStr)
   {
          if (astrDSINo == null || astrDSINo.isEmpty()) {
                 return BLANK;
          } else if (astrDSINo.length() >= 9 && astrDSISubStr == false) 
          {
                   return astrDSINo.substring(0, 3) + DATE_SEPERATOR + 
                            astrDSINo.substring(3, 6) + DATE_SEPERATOR + 
                            astrDSINo.substring(6,9); 
          } else if (astrDSINo.length() > 6) {
                 return 
                              astrDSINo.substring(0, 3) + DATE_SEPERATOR + 
                              astrDSINo.substring(3, 6) + DATE_SEPERATOR + 
                              astrDSINo.substring(6); 
          }
          else if (astrDSINo.length() > 3) {
                 return 
                              astrDSINo.substring(0, 3) + DATE_SEPERATOR + 
                              astrDSINo.substring(3); 
          } else{
                 return astrDSINo;
          }
   }

	
	/*To Print Part No based on Part No length.
	  * @param astrPartNo
	 */
	
	public String getPartNo(String astrPartNo)
	{    /*commented by Vaishali*/
		/*if (astrPartNo == null || astrPartNo.isEmpty()) {
			return BLANK;
		} else if (astrPartNo.length()>10) 
		{
			  return astrPartNo.substring(0, 5) + DASH + 
				     astrPartNo.substring(5, 10) + DASH + 
				     astrPartNo.substring(10); 
		} 
		else {
			return astrPartNo.substring(0, 5) + DASH + 
				    astrPartNo.substring(5); 
					
		}*/
		if (astrPartNo == null || astrPartNo.isEmpty()) {
			return BLANK;
		} else if (astrPartNo.length()<=5) {
			return astrPartNo;
		} else if (astrPartNo.length() <= 10) {
			  return astrPartNo.substring(0, 5) + DASH + 
					 astrPartNo.substring(5); 
		} else {
			  return astrPartNo.substring(0, 5)  + DASH + 
				     astrPartNo.substring(5, 10) + DASH + 
				     astrPartNo.substring(10);
		}

	}
   
    
    /**
     * Method to get the String for Barcode
     * @param astrBarTextIn : strBarTextIn in VB 
     * @return lstrBarTextOut : Final String
     */
    public String getBarCode(String astrBarTextIn) 
    {
           int lintSum = 103;
           int lintAsciiChar = 0;
           int lintCharValue = 0;
           int lintCheckSumValue = 0;
           
           String lstrBarTextOut = BLANK;
           String lstrCheckSum = BLANK;
           
           if(astrBarTextIn == null || astrBarTextIn.isEmpty()) {
        	   return BLANK;
           }
           
           for(int i=0; i<astrBarTextIn.length(); i++) 
           {
                 lintAsciiChar = (int) astrBarTextIn.charAt(i);
                  
                  if(lintAsciiChar  < 123) {
                       lintCharValue = lintAsciiChar - 32;
                  } else {
                       lintCharValue = lintAsciiChar - 70;
                  }
                  
                  lintSum = lintSum + (lintCharValue * (i+1)) ;

			   if(astrBarTextIn.charAt(i) == ' ') {
                       lstrBarTextOut = lstrBarTextOut + CHAR_174 ;
                  } else {
                       lstrBarTextOut =  lstrBarTextOut + astrBarTextIn.charAt(i);
                  }
           }

           
        // Find the remainder when Sum is divided by 103
           lintCheckSumValue =  (lintSum % 103);
        // Translate that value to an ASCII character
           if(lintCheckSumValue > 90) {
                 lstrCheckSum = Character.toString((char) ((char)(lintCheckSumValue) + 70)) ;
           } else if(lintCheckSumValue > 0) {
                 lstrCheckSum = Character.toString((char) ((char)(lintCheckSumValue) + 32)) ;
           } else {
                 lstrCheckSum = CHAR_174;
           }
           
        // Build output string, trailing space is for Windows rasterization bug
           lstrBarTextOut = START_CHAR + lstrBarTextOut + lstrCheckSum + END_CHAR;
           
           return lstrBarTextOut;
    }
    
    public int getMonthDays(String astrYearMonth) 
    {
           if(astrYearMonth == null || astrYearMonth.isEmpty() || astrYearMonth.length() < 6)
           {
                  return 0 ;
           }
           Calendar calendar = Calendar.getInstance();
           calendar.set(Calendar.YEAR, Integer.parseInt(astrYearMonth.substring(0,4)));
           calendar.set(Calendar.MONTH, Integer.parseInt(astrYearMonth.substring(4,6))-1);
           int lintnumDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
           return lintnumDays;
                        
    }
    
    public int getMonthDay(String astrYearMonth) 
    {
           if(astrYearMonth == null || astrYearMonth.isEmpty() || astrYearMonth.length() < 6)
           {
                  return 0 ;
           }
           Calendar calendar = Calendar.getInstance();
           calendar.set(Calendar.YEAR, Integer.parseInt(astrYearMonth.substring(0,4)));
           calendar.set(Calendar.MONTH, Integer.parseInt(astrYearMonth.substring(4,6))-1);
           int lintnumDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
           return lintnumDays;
                        
    }
    public String getNthNextMonth(String astrYearMonth,int aintIndex) {
        int lintMonth = 0;
        int lintYear = 0;
                             
        if (astrYearMonth == null || astrYearMonth.length() < 6) {
               return null;
        } else {
               lintMonth = Integer.parseInt(astrYearMonth.substring(4,6));
                      lintYear = Integer.parseInt(astrYearMonth.substring(0,4));    
               }
        
               Calendar c = Calendar.getInstance();
              c.setTime(new Date());
              c.set(Calendar.MONTH, lintMonth-1);
              c.add(Calendar.MONTH,aintIndex);
              String lstrMonth = getMonthName((c.get(Calendar.MONTH)+1) + BLANK, false);
              if (lintMonth + aintIndex > 12)
               {
                      lintYear = lintYear + 1;
                      return (lstrMonth + SPACE + lintYear);
               } else {
                     return (lstrMonth + SPACE + lintYear);
              }
              
        }

    public String getNthMonthName(String astrYearMonth,int aintIndex ) {
 	   	Calendar c = Calendar.getInstance();
 		c.setTime(new Date());
 		c.set(Calendar.MONTH, Integer.parseInt(astrYearMonth.substring(4,6))-1);
 		c.add(Calendar.MONTH,aintIndex);
 		String lstrMonth = getMonthName((c.get(Calendar.MONTH)+1) + "", false);
			return "("+lstrMonth + ")";
		
 	}
 	

}
