package ru.fizteh.fivt.students.ilin_ilia.storeable.dbexceptions;

public class FileMapIOException extends RuntimeException {
    public FileMapIOException() {
    }

    public FileMapIOException(String s) {
        super(s);
    }

    public FileMapIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileMapIOException(Throwable cause) {
        super(cause);
    }
}
