package ru.ik87.xwpf;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.ik87.ThreadPoolSend;
import ru.ik87.send.Send;
import ru.ik87.send.SendException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SendTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    @Ignore
    @Test
    public void whenLineSendThenOK() throws Exception {
        File fileOut = testFolder.newFile();
        File fileIn = new File(getClass().getClassLoader().getResource("big_table.xlsx").getFile());
        String fip = fileIn.getPath();
        String fop = fileOut.getPath();

        String template = getClass().getClassLoader().getResource("template.docx").getPath();

        Docx docx = new Docx(template);
        Xlsx xlsx = new Xlsx(fip, fop);

        List<EntityRow> entityRows = xlsx.cached(x-> {
            String state = x.getElement().get("Состояние");
            return state == null || state.isEmpty() || state.contains("ошибка");
        });

        Send send = new Send() {
            @Override
            public void send(byte[] bytes, EntityRow entityRow) throws SendException {
                try {
                    entityRow.getElement().put("Состояние", "отправлено");
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        sends(docx, entityRows, send);
       // System.out.println(entityRows);
    }
    @Ignore
    @Test
    public void whenThreadPoolSendThenOK() throws Exception {
        File fileOut = testFolder.newFile();
        File fileIn = new File(getClass().getClassLoader().getResource("big_table.xlsx").getFile());
        String fip = fileIn.getPath();
        String fop = fileOut.getPath();

        String template = getClass().getClassLoader().getResource("template.docx").getPath();

        Docx docx = new Docx(template);
        Xlsx xlsx = new Xlsx(fip, fop);

        List<EntityRow> entityRows = xlsx.cached(x-> {
            String state = x.getElement().get("Состояние");
            return state == null || state.isEmpty() || state.contains("ошибка");
        });

        Send send = new Send() {
            @Override
            public void send(byte[] bytes, EntityRow entityRow) throws SendException {
                try {
                    entityRow.getElement().put("Состояние", "отправлено");
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolSend threadPoolSend = new ThreadPoolSend();
        threadPoolSend.sends(docx, entityRows, send);
        threadPoolSend.close();
      //  System.out.println(entityRows);
    }


    void sends(Docx docx, List<EntityRow> entityRows, Send send) {
        for (EntityRow entityRow : entityRows) {
            try {
                byte[] bytes = docx.generateFromTemplate(entityRow);
                send.send(bytes, entityRow);
            } catch (SendException e) {
                entityRow.getElement().put("Состояние", e.getMessage());
            } catch (IOException e) {
                entityRow.getElement().put("Состояние", "Ошибка шаблона");
            }
        }
    }

}