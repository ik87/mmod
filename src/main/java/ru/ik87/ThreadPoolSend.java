package ru.ik87;

import ru.ik87.send.Send;
import ru.ik87.send.SendException;
import ru.ik87.xwpf.Docx;
import ru.ik87.xwpf.EntityRow;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Многопоточный обработчик, формирует файл
 * и производи его отправку.
 *
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 24.10.2020
 */
public class ThreadPoolSend implements Closeable {

    private ExecutorService pool = Executors.newCachedThreadPool();

    /**
     * Многопоточная обработка
     * @param docx класс с шаблоном
     * @param entityRows Список из сущностей на основе рядов таблицы
     * @param send способ отправки
     */
    public void sends(Docx docx, List<EntityRow> entityRows, Send send) {
        for (EntityRow entityRow : entityRows) {
            pool.submit(() ->
            {
                try {
                    byte[] bytes = docx.generateFromTemplate(entityRow);
                    send.send(bytes, entityRow);
                } catch (SendException e) {
                    entityRow.getElement().put("Состояние", e.getMessage());
                } catch (IOException e) {
                    entityRow.getElement().put("Состояние", "Ошибка шаблона");
                }
            });

        }
    }

    /**
     * Попытка закрыть обработчик
     */
    @Override
    public void close() {
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
