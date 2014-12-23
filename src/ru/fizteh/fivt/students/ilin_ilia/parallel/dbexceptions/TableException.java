package ru.fizteh.fivt.students.ilin_ilia.parallel.dbexceptions;

public class TableException extends RuntimeException {

    public TableException(String s) {
        super(s);
    }

    public TableException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableException(Throwable cause) {
        super(cause);
    }
}
