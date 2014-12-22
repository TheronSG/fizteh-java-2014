package ru.fizteh.fivt.students.ilin_ilia.junit.database;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;


public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(final String dir) {
        if (dir == null) {
            throw  new IllegalArgumentException();
        }
            return new MyTableProvider(dir);
    }
}
