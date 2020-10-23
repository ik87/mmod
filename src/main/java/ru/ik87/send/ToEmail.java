package ru.ik87.send;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.mail.HtmlEmail;
import ru.ik87.xwpf.EntityRow;

import javax.mail.util.ByteArrayDataSource;
import java.util.Map;
import java.util.Properties;

/**
 * Отправка через почту
 * Синхронная отправка файла на заданную почту
 * все настройки почты берутся из config.properties
 *
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 24.10.2020
 */
@ThreadSafe
public class ToEmail implements Send {
    private final String emailAttachFile;
    private final String emailAttachDecrFile;
    private final Integer smtp;
    private final String emailFrom;
    private final String hostName;
    private final String emailLogin;
    private final String emailPass;
    private final String from_name;
    private final String subject;
    private final String html_msg;
    private final String text_msg;

    public ToEmail(Properties config) {
        this.emailAttachFile = config.getProperty("email_attach_file");
        this.emailAttachDecrFile = config.getProperty("email_attach_descr_file");
        this.smtp = Integer.valueOf(config.getProperty("email_smtp_port", "2525"));
        this.emailFrom = config.getProperty("email_from");
        this.hostName = config.getProperty("email_host_name");
        this.emailLogin = config.getProperty("email_auth_login");
        this.emailPass = config.getProperty("email_auth_pass");
        this.from_name = config.getProperty("email_from_name");
        this.subject = config.getProperty("email_subject");
        this.html_msg = config.getProperty("email_html_msg");
        this.text_msg = config.getProperty("email_text_msg");
    }

    /**
     * Каждый раз необходимо инициализировать фремворк, т.е одна инициализация
     * одна отправка
     *
     * @throws SendException
     */
    private HtmlEmail initEmail() throws SendException {
        HtmlEmail email;
        try {
            email = new HtmlEmail();
            email.setSmtpPort(smtp);
            email.setFrom(emailFrom, from_name);
            email.setHostName(hostName);
            email.setAuthentication(emailLogin, emailPass);
            email.setSubject(subject);
            email.setHtmlMsg(html_msg);
            email.setTextMsg(text_msg);
            email.setCharset("utf-8");
            email.setSSLOnConnect(false);
        } catch (Exception e) {
            throw new SendException("ошибка при инициализации почты");
        }
        return email;
    }

    @Override
    public void send(byte[] bytes, EntityRow entityRow) throws SendException {
        Map<String, String> element = entityRow.getElement();
        String emailTo = element.get("Почта");
        try {
            //инициализируем почту
            HtmlEmail email = initEmail();
            //чистим название почты
            emailTo = emailTo.replaceAll("[^\\w\\d-@_.]+", "");
            //устанваливаем почту назначения
            email.addTo(emailTo);
            //задаем MIME тип для прикрепленного файла, данная запись соответствует docx
            var source = new ByteArrayDataSource(bytes, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            //прикрепляем файл
            email.attach(source, emailAttachFile, emailAttachDecrFile);
            //отправляем файл
            email.send();
            //в случае успеха записываем в поле - Состояние - "отправлено"
            entityRow.getElement().put("Состояние", "отправлено");
            System.out.println(emailTo + " отправлено");
        } catch (Exception e) {
            System.out.println(emailTo + " ошибка отправки");
            throw new SendException("ошибка отправки");
        }
    }
}
