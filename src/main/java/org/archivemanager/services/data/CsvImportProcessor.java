package org.archivemanager.services.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.archivemanager.services.entity.ImportProcessor;


public abstract class CsvImportProcessor extends ImportProcessor {
	private static final long serialVersionUID = 8880482181476862351L;
	
	protected List<String> columns = new ArrayList<String>();
	protected boolean inBold = false;
	protected boolean inItalic = false;
	protected boolean inUnderline = false;
	
    
	protected String getStringCellValue(Row data, String column) {
		if(data == null || column == null) return "";
		int columnIndex = columns.indexOf(column);
		if(columnIndex == -1) return "";
		Cell cell = data.getCell(columnIndex);
		if(cell != null) {
			switch (cell.getCellType()) {
	        case Cell.CELL_TYPE_STRING:
	            return cell.getRichStringCellValue().getString().trim();
	        case Cell.CELL_TYPE_NUMERIC:
	            if(DateUtil.isCellDateFormatted(cell)) {
	                return cell.getDateCellValue().toString().trim();
	            } else {
	                return String.valueOf((int)cell.getNumericCellValue()).trim();
	            }
	        case Cell.CELL_TYPE_BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue()).trim();
	        default:
	            return "";
			}
		}
		return "";
	}
	protected double getNumericCellValue(Row data, String column) {
		if(data == null || column == null) return 0;
		int columnIndex = columns.indexOf(column);
		if(columnIndex == -1) return 0;
		Cell cell = data.getCell(columnIndex);
		if(cell != null) {
			switch (cell.getCellType()) {
	        case Cell.CELL_TYPE_STRING:
	            return Double.valueOf(cell.getStringCellValue().trim());
	        case Cell.CELL_TYPE_NUMERIC:
	           return cell.getNumericCellValue();
	        default:
	            return 0;
			}
		}
		return 0;
	}
	protected String getStringCellValue(Cell cell) {
		if(cell == null) return "";
		switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            return cell.getRichStringCellValue().getString();
        case Cell.CELL_TYPE_NUMERIC:
            if(DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            } else {
                return String.valueOf((int)cell.getNumericCellValue());
            }
        case Cell.CELL_TYPE_BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
        default:
            return "";
		}
	}
	protected double getNumericCellValue(Cell cell) {
		if(cell == null) return 0;
		switch (cell.getCellType()) {
        case Cell.CELL_TYPE_NUMERIC:
            return cell.getNumericCellValue();
        default:
            return 0;
		}
	}
	protected String getHtmlFormatedCellValue(XSSFCell cell) {		
		XSSFRichTextString cellText = cell.getRichStringCellValue();
        String htmlCode = "";
        int length = cellText.toString().length();        
        for (int i = 0; i < length; i++) {
        	try {
        		XSSFFont font = cellText.getFontAtIndex(i);
        		if(font != null) htmlCode += getFormatFromFont(font);
        	} catch(Exception e) {
        		//e.printStackTrace();
        	}
        	String character = Character.toString(cellText.getString().charAt(i));
        	if(!character.equals("\r")) htmlCode += character;
        }
        if (inItalic) {
            htmlCode += "</i>";
            inItalic = false;
        }
        if (inBold) {
            htmlCode += "</b>";
            inBold = false;
        }
        if (inUnderline) {
            htmlCode += "</u>";
            inUnderline = false;
        }
        //System.out.println(htmlCode);
        return htmlCode;
    }
	protected String getFormatFromFont(XSSFFont font) {
		String formatHtmlCode = "";
        if (font.getItalic() && !inItalic) {
            formatHtmlCode += "<i>";
            inItalic = true;
        } else if (!font.getItalic() && inItalic) {
            formatHtmlCode += "</i>";
            inItalic = false;
        }
        if (font.getBold() && !inBold) {
            formatHtmlCode += "<b>";
            inBold = true;
        } else if (!font.getBold() && inBold) {
            formatHtmlCode += "</b>";
            inBold = false;
        }
        if(font.getUnderline() > 0 && !inUnderline) {        	
            formatHtmlCode += "<u>";
            inUnderline = true;
        } else if (font.getUnderline() == 0 && inUnderline) {
            formatHtmlCode += "</u>";
            inUnderline = false;
        }
        return formatHtmlCode;
    }
	protected String clean(String in) {
		return in.trim();
	}
}
