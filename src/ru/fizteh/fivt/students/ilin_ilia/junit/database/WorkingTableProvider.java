package ru.fizteh.fivt.students.ilin_ilia.junit.database;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class WorkingTableProvider {
    private TableProvider tableProvider;
    private Table currentTable;

    public WorkingTableProvider() {
        // Only for tests.
    }

    public WorkingTableProvider(TableProvider tableProvider) {
        this.tableProvider = tableProvider;
        currentTable = null;
    }
    public void setCurrentTable(Table table) {
        currentTable = table;
    }

    public Table getCurrentTable() {
        return currentTable;
    }
    public TableProvider getTableProvider() {
        return tableProvider;
    }
}
