package ru.ik87.xwpf;

import net.jcip.annotations.ThreadSafe;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
/**
 * Обработчик docx файла, суть взять шаблон, и сохранить его ввиде байтов
 * далее, если нужно сгенерировать нужный docx помещяем сущность EntityRow
 * в generateFromTemplate
 *
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 24.10.2020
 */
@ThreadSafe
public class Docx {
    /**
     * Загруженный шаблонный файл, на основе которого будет генерироваться docx
     */
    private final byte[] template;

    public Docx(String templateDocxFile) throws IOException {
        File file = new File(templateDocxFile);
        this.template =  Files.readAllBytes(file.toPath());
    }

    /**
     * Генератор готового docx на основнании ряда таблицы и файла шаблона
     * @param entityRow ряд таблицы
     * @return массив байтов сгенерированного docx файла
     * @throws IOException в случае проблемм с файлом (например если его нет)
     */
    public byte[] generateFromTemplate(EntityRow entityRow) throws IOException {
        InputStream bis = new ByteArrayInputStream(template);
        XWPFDocument doc = new XWPFDocument(bis);
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    replace(r, entityRow.element);
                }
            }
        }
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            replace(r, entityRow.element);
                        }
                    }
                }
            }
        }

        bis.close();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        doc.write(buffer);
        byte[] bytes = buffer.toByteArray();
        buffer.close();
        doc.close();

        return bytes;
    }


    private void replace(XWPFRun r, Map<String, String> variables) {
        String text = r.getText(0);
        if (text != null) {
            for (var variable : variables.entrySet()) {
                if (text.contains(variable.getKey())) {
                    text = text.replace(variable.getKey(), variable.getValue());
                    r.setText(text, 0);
                }
            }
        }
    }

}
