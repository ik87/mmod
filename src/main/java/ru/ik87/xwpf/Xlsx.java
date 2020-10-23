package ru.ik87.xwpf;

import net.jcip.annotations.ThreadSafe;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
/**
 * Обработчик xlsx файла, cусть взять xlsx файл
 * и сохранить её в список из сущностей EntintyRow
 *
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 24.10.2020
 */
@ThreadSafe
public class Xlsx {
    //входящий и исходящие файлы, т.е который будет считываться и куда будет записыаться
    //в основном записывается одно поле "Состояние"
    private String fileIn;
    private String fileOut;


    public Xlsx(String fileIn, String fileOut) {
        this.fileIn = fileIn;
        this.fileOut = fileOut;
    }

    /**
     * Считыватель xlsx таблицы
     * @param predicate параметр представляющий собой фильтр полей которые нужно считать и записать
     *                  в  список EntityRow
     * @return список EntityRow
     * @throws IOException в случае проблем с открытием или закрытием xlsx файла
     * @throws InvalidFormatException в случае если файл имеет другой формат, отличный от xlsx
     */
    public  synchronized List<EntityRow> cached(Predicate<EntityRow> predicate) throws IOException, InvalidFormatException {
        List<EntityRow> cache = new ArrayList<>();
        try (XSSFWorkbook wb = new XSSFWorkbook(new File(fileIn))) {
            List<String> methods = new ArrayList<>();
            FormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator(wb);
            Sheet sheet1 = wb.getSheetAt(0);
            for (Row row : sheet1) {
                Map<String, String> table = new LinkedHashMap<>();
                for (Cell cell : row) {
                    if (row.getRowNum() == 0) {
                        String method = cell.getRichStringCellValue().getString();
                        methods.add(method);
                    } else {

                        if (cell.getCellType() == CellType.STRING) {
                            String value = cell.getRichStringCellValue().getString();
                            String key = methods.get(cell.getColumnIndex());
                            table.put(key, value);
                        }

                        if (cell.getCellType() == CellType.FORMULA) {
                            DataFormatter df = new DataFormatter();
                            String key = methods.get(cell.getColumnIndex());
                            objFormulaEvaluator.evaluate(cell);
                            String value = df.formatCellValue(cell, objFormulaEvaluator);
                            table.put(key, value);
                        }
                    }
                }
                if (row.getRowNum() != 0) {
                    EntityRow entityRow = new EntityRow(row.getRowNum(), table);
                    if (predicate.test(entityRow)) {
                        cache.add(entityRow);
                    }
                }
            }
        }
        return cache;
    }

    /**
     * Сохраняем изменения в таблицу. т.е Поле "Состояние" было пусто, после отправки оно стало
     * "отправленно", записываем это в нашу xlsx таблицу
     * @param entityRows список сущностей которые хотим сохранить
     * @throws IOException в случае проблем с открытием или закрытием xlsx файла
     */

    public synchronized void commitCache(List<EntityRow> entityRows) throws IOException {
        FileInputStream fis = new FileInputStream(new File(this.fileIn));
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        Sheet sheet1 = wb.getSheetAt(0);
        for (EntityRow er : entityRows) {
            for (Cell cell : sheet1.getRow(0)) {
                String key = cell.getRichStringCellValue().getString();
                String value = er.getElement().get(key);
                int indexColumn = cell.getColumnIndex();
                Row r = sheet1.getRow(er.getIndex());
                Cell c = r.getCell(indexColumn);
                if (c == null) {
                    c = r.createCell(indexColumn);
                }
                c.setCellValue(value);
            }
        }

        fis.close();
        FileOutputStream fos = new FileOutputStream(new File(this.fileOut));
        wb.write(fos);
        fos.close();

    }

}
