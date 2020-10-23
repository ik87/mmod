package ru.ik87.send;

import ru.ik87.xwpf.EntityRow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Сохраняем сформированный docx на жесткий диск в папку out/
 *
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 24.10.2020
 */
public class ToFile implements Send {
    private static final String PATH = "out/";

    public ToFile() throws IOException {
        Files.createDirectories(Paths.get(PATH));
    }

    @Override
    public void send(byte[] bytes, EntityRow entityRow) throws SendException {
        FileOutputStream fos = null;
        Map<String, String> element = entityRow.getElement();
        String emailTo = element.get("Почта");
       try {
           System.out.println(PATH + entityRow.getIndex() + "_" + emailTo + ".docx" + " записано");
           fos = new FileOutputStream(PATH + entityRow.getIndex() + "_" + emailTo + ".docx");
           fos.write(bytes);
           entityRow.getElement().put("Состояние", "записано");
       }catch (Exception e) {
           System.out.println(PATH + entityRow.getIndex() + "_" + emailTo + ".docx" + " ошибка записи");
           throw new SendException("ошибка записи docx");
       } finally {
           if(fos != null) {
               try {
                   fos.close();
               }catch (IOException e) {
                   System.out.println(PATH + entityRow.getIndex() + "_" + emailTo + ".docx" + " ошибка закрытия");
                   throw new SendException("ошибка закрытия docx");
               }
           }
       }
    }
}
