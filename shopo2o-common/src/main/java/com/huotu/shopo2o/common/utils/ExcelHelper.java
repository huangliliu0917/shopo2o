package com.huotu.shopo2o.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public class ExcelHelper {
    /**
     * 创建一个excel文档
     *
     * @param sheetName   sheet的名称
     * @param thCells     标题
     * @param rowAndCells 行和列信息 {@link CellDesc}
     * @return
     */
    public static HSSFWorkbook createWorkbook(
            String sheetName,
            String[] thCells,
            List<List<CellDesc>> rowAndCells
    ) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        //创建表头
        HSSFRow thRow = sheet.createRow(0);
        for (int i = 0; i < thCells.length; i++) {
            thRow.createCell(i).setCellValue(thCells[i]);
        }
        //创建表单
        for (int i = 0; i < rowAndCells.size(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < rowAndCells.get(i).size(); j++) {
                CellDesc cellDesc = rowAndCells.get(i).get(j);
                HSSFCell cell = row.createCell(j);
                cell.setCellType(cellDesc.cellType);
                if (cellDesc.cellType == Cell.CELL_TYPE_NUMERIC) {
                    if (cellDesc.getValue() != null) {
                        cell.setCellValue(new Double(String.valueOf(cellDesc.getValue())));
                    } else {
                        cell.setCellValue((double) 0);
                    }
                } else {
                    cell.setCellValue(String.valueOf(cellDesc.getValue()));
                }
            }
        }
        return workbook;
    }
    public static CellDesc asCell(Object value, int cellType) {
        return new CellDesc(value, cellType);
    }

    public static CellDesc asCell(Object value) {
        return new CellDesc(value, Cell.CELL_TYPE_STRING);
    }
    @Setter
    @Getter
    @AllArgsConstructor
    public static class CellDesc {
        private Object value;
        private int cellType;
    }
}
