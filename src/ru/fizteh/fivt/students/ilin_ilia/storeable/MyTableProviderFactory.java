package ru.fizteh.fivt.students.theronsg.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.text.ParseException;


public class MyTableProviderFactory implements TableProviderFactory {

    public MyTableProviderFactory() {
        // Just empty constructor.
    }
    @Override
    public TableProvider create(final String dir) throws IOException {
        if (dir == null) {
            throw  new IllegalArgumentException();
        }
        try {
            TableProvider table = null;
            try {
                table = new MyTableProvider(dir);
            } catch (ClassNotFoundException e) {
                throw new IOException(e.getMessage());
            } catch (ParseException e) {
                throw new IOException(e.getMessage());
            }
            return table;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
