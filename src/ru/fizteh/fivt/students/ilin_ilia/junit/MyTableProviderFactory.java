package ru.fizteh.fivt.students.ilin_ilia.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTableProviderFactory implements TableProviderFactory {

    public MyTableProviderFactory() {
        // Just empty constructor.
    }
    @Override
    public TableProvider create(final String dir) {
        if (dir == null) {
            throw  new IllegalArgumentException();
        }
            return new MyTableProvider(dir);
    }

    public boolean checkNameCorrection(final String name) {
        Matcher matcher = Pattern.compile("[~#@*+%{}<>\\[\\]\"\\_^]?/:*|").matcher(name);
        return matcher.find();
    }
}
