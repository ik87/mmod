package ru.ik87.send;

public class SendException extends Exception {
    public SendException(String msg) {
        super(msg);
    }

    public SendException() {
    }
}
