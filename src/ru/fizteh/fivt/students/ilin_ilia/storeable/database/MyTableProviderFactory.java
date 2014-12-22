package ru.fizteh.fivt.students.ilin_ilia.storeable.database;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.text.ParseException;


public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(final String dir) throws IOException {
        if (dir == null) {
            throw  new IllegalArgumentException();
        }
        try {
            return new MyTableProvider(dir);
        } catch (ClassNotFoundException | ParseException e) {
            throw new IOException(e.getMessage());
        }
    }
}
