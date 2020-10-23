package ru.ik87.xwpf;

import java.util.Map;
/**
 * Cущность, представляющая собой набор индекса в таблице
 * и карты с ключём которой является название колонки, например "Состояние" или "Почта"
 * значением является значение этой колонки по ряду соответствующий индексу (index)
 *
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 24.10.2020
 */
public class EntityRow {
    int index;
    Map<String,String> element;

    public EntityRow(int index ,Map<String, String> element) {
        this.element = element;
        this.index = index;
    }

    public Map<String, String> getElement() {
        return element;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return index + ") " + element + "\n";
    }
}
