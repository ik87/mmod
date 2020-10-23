package ru.ik87.send;

import ru.ik87.xwpf.EntityRow;

/**
 * Общий интерфейс отправителя
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 24.10.2020
 */
public interface Send {
    /**
     * @param bytes файл, полученный из шаблона и таблицы
     * @param entityRow ряд из таблицы, соответствующий файлу (предыдущему параметру)
     * @throws SendException в случае проблем при записи
     */
  public  void send(byte[] bytes, EntityRow entityRow) throws SendException;
}
