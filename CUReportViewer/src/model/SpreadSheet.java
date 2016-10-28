/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import static org.apache.poi.hssf.usermodel.HeaderFooter.file;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author brian.marshall
 */
public class SpreadSheet {

    private FileOutputStream output;
    private XSSFWorkbook wb;
    private XSSFSheet worksheet;
    private JTable table;
    private String path;

    public SpreadSheet(String newPath) throws FileNotFoundException, IOException {
        path = newPath;
        instantiateWorksheet();
    }

    public void instantiateWorksheet() throws FileNotFoundException, IOException {
        path = path + ".xlsx";

        this.output = new FileOutputStream(new File(path));
        this.wb = new XSSFWorkbook();
        this.worksheet = wb.createSheet();
        //this.wb.write(output);
        //output.close();
    }

    public void populateSpreadSheet(JTable table) throws IOException {
        this.table = table;
        CellStyle style = wb.createCellStyle();//Create style
        Font font = wb.createFont();//Create font
        font.setBold(true);//Make font bold
        style.setFont(font);//set it to bold
        try {
            TableModel model = table.getModel();
            //FileWriter excel = new FileWriter(new File(path));
            XSSFRow row = worksheet.getRow(0);
            if (row == null) {
                row = worksheet.createRow(0);
            }
            XSSFCell cell;

            for (int i = 0; i < model.getColumnCount(); i++) {
                cell = row.getCell(i);
                if (cell == null) {
                    cell = row.createCell(i);
                    cell.setCellStyle(style);
                }
                cell.setCellValue(model.getColumnName(i));

            }
            int tableRow = 0;
            for (int spreadSheetRow = 1; spreadSheetRow <= model.getRowCount(); spreadSheetRow++) {
                row = worksheet.getRow(spreadSheetRow);
                if (row == null) {
                    row = worksheet.createRow(spreadSheetRow);
                }
                for (int j = 0; j < model.getColumnCount(); j++) {
                    cell = row.getCell(j);
                    if (cell == null) {
                        cell = row.createCell(j);
                    }
                    if (model.getValueAt(tableRow, j) == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(model.getValueAt(tableRow, j).toString());
                    }

                }
                tableRow++;
            }
            for (int i = 0; i < model.getColumnCount(); i++) {
                worksheet.autoSizeColumn(i);
            }
            this.wb.write(output);
            wb.close();
            output.close();
        } catch (IOException e) {
            System.out.println(e);
        }

    }
}
