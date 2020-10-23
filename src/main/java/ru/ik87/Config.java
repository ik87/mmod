package ru.ik87;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Утилитный класс, необходим для загрузки параметров в правельном предствлении кирилицы
 *
 * @author Kosolapov Ilya (d_dexter@mail.ru)
 * @version 1.0
 * @since 1.10.2020
 */
public class Config {
    private final Properties values = new Properties();

    public Config(String fileConfig) {
        try (InputStream in = new FileInputStream(new File(fileConfig))) {
            values.load(new InputStreamReader(in, Charset.forName("UTF-8")));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Properties getProperties() {
        return values;
    }
}
