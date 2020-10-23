#  Массовая Рассылка Документов
**mmod - Mass Mailing of documents**

### Версии
### [1.0] - 24-10-2020
- Поддержка многопоточности

## Использованные средства
* [Open JDK 11.02](https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_windows-x64_bin.zip.sha256) - компилятор\интерпритатор
* [Maven](http://maven.apache.org/index.html) сборка и управление проектом
* [Apache Commons Email](https://commons.apache.org/proper/commons-email/) - библиотека почтового клиента
* [Apache POI - the Java API for Microsoft Documents](https://poi.apache.org/) - библиотека для работы с документами Microsoft Office (xlsx, docx)

## Компиляция
- Во избежание проблем установите JDK не ниже 11.02 скчать можно [тут](https://jdk.java.net/archive/),
- Так же необходимо установить Maven, скачать можно [тут](http://maven.apache.org/download.cgi#files)

```sh
$ cd mmod
$ mvn package
```
Появится папка **target**, в ней файл **mmod.jar**, скопируем его в отдельную папку.<br>
туда же скопировать:

- *template.docx* 
- *table.xlsx*
- *config.properties*

Это пример файлов, т.е всё делаем по аналогии.

## Запуск
**Перед запуском, нужно выйти из открытых xlsx и docx**
```sh
$ java -Dfile.encoding="UTF-8" -jar mmod.jar -Ttemplate.docx -Ltable.xlsx -f -m
```
обязательные параметры:
- **-Т** - шаблон docx, файл должен лежать в тойже папке
- **-L** - таблица xlsx, файл должен лежать в тойже папке 
- **-f** - сохранить в файлы (в папку **out** рядом с запускаемым файлом)
- **-m** - отправить по почте, см. config.properties

Лучше всего заранее создать батники.

- для массового сохранения на диск
```sh
$ echo java -Dfile.encoding="UTF-8" -jar mmod.jar -Ttemplate.docx -Ltable.xlsx -f >> save.bat
```
- для массовой отправки
```sh
$ echo java -Dfile.encoding="UTF-8" -jar mmod.jar -Ttemplate.docx -Ltable.xlsx -m >> send.bat
```

Настройка почты происходит в **config.properties**, файл должен быть сохраненн в **UTF-8**
c таким содержимом, в проекте лежит пример.
```txt
############# email settings ################
#названия хоста smtp сервера почты
email_host_name=mail.ru
##email_smtp_port=2525
##логин и пароль от почты
email_auth_login=xyz@mail.ru
email_auth_pass=pass
#с какой почты отправить, обычно соответствуется email_auth_login
email_from=xyz@mail.ru
#от кого письмо, будет видно вместо названия почты
email_from_name=ООО "РОГА И КОПЫТА"
#от кого письмо
email_subject=От ген. директора ООО <РОГА И КОПЫТА>, предложение
#внутреннее содержание письма в формате html
email_html_msg=<p>С уважением,<br /> Иванова Татьяна Ивановна<br /> Помощник генерального директора<br />ООО &laquo;РОГА И КОПЫТА&raquo;<br /> Тел./Факс: <br />(499) 111-11-11<br /> (499) 222-22-22<br /> (499) 333-33-33<br /> E-mail: <a href='mailto:xyz@mail.ru'>xyz@mail.ru</a></p>
#внутреннее содержание письма в текстовом формате, если email_html_msg откланен
email_text_msg=С уважением\n\rИванова Татьяна Ивановна\n\rПомощник генерального директора\n\rООО "РОГА И КОПЫТА"\n\rТел./Факс:\n\r(499) 111-11-11\n\r(499) 222-22-22\n\r(499) 333-33-33\n\rE-mail: xyz@mail.ru
#название файла который будет прикреплен
email_attach_file=Комерческое предложение.docx
#описание файла который будет прикреплен
email_attach_descr_file=Комерческое предложение
```