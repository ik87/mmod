package ru.ik87;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import ru.ik87.xwpf.*;
import ru.ik87.send.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Автоматическое сохранение и\или отправка письма на указанную почту
 *
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 24.10.2020
 */
public class Program {
    public static void main(String[] args) {
        //проверка на верность аргументов args
        if (!check(args)) {
            printError();
            System.exit(0);
        }
        try {
            /**
             * фильтрация по полю "Состояние" т.е
             * если оно имеет значение пустое значение или
             * "записано" или "ошибка ..."
             * то данные из этого ряда (row) будут взяты
             * иначе поля пропускаются
             */
            Predicate<EntityRow> predicate = x -> {
                String state = x.getElement().get("Состояние");
                return state == null
                        || state.isEmpty()
                        || state.contains("записано")
                        || state.contains("ошибка");
            };
            //инициализация обработчика отправки/записи
            ThreadPoolSend threadPoolSend = new ThreadPoolSend();
            //получаем обертку над properties (исправление кирилицы)
            Config config = new Config("config.properties");
            //инициализуем обработчик docx файла, передаем в него шаблон
            Docx docx = new Docx(param(args, "-T"));
            String table = param(args, "-L");
            //инициализуем обработчик xlsx на входи и на выход вставляем один и тот же файл
            Xlsx xlsx = new Xlsx(table, table);
            //получаем из xlsx файла наши сущности (позиции и все элементы ряда) ввиде списка
            List<EntityRow> entityRows = xlsx.cached(predicate);

            //если в args стоит флаг -f то записываем всё в /out (асинхронно)
            if (Arrays.asList(args).contains("-f")) {
                threadPoolSend.sends(docx, entityRows, new ToFile());
            }
            //если в args стоит флаг -m то отправляем всё на почты (синхронно)
            if (Arrays.asList(args).contains("-m")) {
                threadPoolSend.sends(docx, entityRows, new ToEmail(config.getProperties()));
            }
            //ождаем остановки обработчика
            threadPoolSend.close();
            //сохраняем результаты обратно в xlsx
            xlsx.commitCache(entityRows);

        } catch (IOException | InvalidFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Проверка флагов
     *
     * @param args массив с аргументами
     * @return если всё хорошо, то true
     */
    static boolean check(String[] args) {
        boolean result = false;
        if (args.length == 3 || args.length == 4) {
            result = true;
            for (String el : args) {
                if (!el.matches("(-T.+\\.docx)|(-L.+\\.xlsx)|-m|-f")) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Вытаскиваем название файлов следующих за флагом
     * Например у нас есть флаг -Ttemplate.docx
     * следовательно у флага -T имяфайла будет template.xlsx
     *
     * @param args   массив аргументов
     * @param prefix флаг
     * @return файл, следующий за флагом
     */
    static String param(String[] args, String prefix) {
        String param = null;
        for (String el : args) {
            if (el.startsWith(prefix)) {
                param = el.substring(2);
                break;
            }
        }
        return param;
    }

    /**
     * Распечатка в случае каких то проблемм с флагами
     */
    static void printError() {
        System.out.println("sendmail.jar -Ttemplate.docx -Ltable.xlsx -m or/and -f");
        System.out.println("-m sending by email");
        System.out.println("-f save to file");
    }
}
