package ru.ik87.xwpf;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class XlsxTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    //  private ParserSqlRu instance;


    @Test
    public void whenCachedFileThenGotListEntity() throws IOException, InvalidFormatException {
        String fileIn = getClass().getClassLoader().getResource("testin.xlsx").getPath();
        Xlsx xlsx = new Xlsx(fileIn, null);
        Predicate<EntityRow> predicate = x -> {
            String state = x.getElement().get("Состояние");
            return state == null || state.isEmpty() || "ошибка".equals(state);
        };
        List<EntityRow> entityRows = xlsx.cached(predicate);
        assertThat(entityRows.size(), is(8));

    }

    @Test
    public void whenSaveEntityToFileThenOK() throws IOException, InvalidFormatException {
        File fileOut = testFolder.newFile();
        File fileIn = new File(getClass().getClassLoader().getResource("testin.xlsx").getFile());
        String fip = fileIn.getPath();
        String fop = fileOut.getPath();
        Xlsx xlsx = new Xlsx(fip, fop);
        Xlsx read = new Xlsx(fop, null);

        Predicate<EntityRow> predicate = x -> {
            String state = x.getElement().get("Состояние");
            return state == null || state.isEmpty() || "ошибка".equals(state);
        };
        List<EntityRow> entityRows = xlsx.cached(predicate);
        assertThat(entityRows.size(), is(8));

        for (EntityRow state : entityRows) {
            state.element.put("Состояние", "отправлено");
        }
        xlsx.commitCache(entityRows);

        entityRows = read.cached(x -> "отправлено".equals(x.element.get("Состояние")));

        assertThat(entityRows.size(), is(16));
    }
}