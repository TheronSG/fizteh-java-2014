package ru.fizteh.fivt.students.ilin_ilia.storeable.database;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class WorkingTableProvider {
    private TableProvider tableProvider;
    private Table currentTable;

    public WorkingTableProvider(TableProvider tableProvider) {
        this.tableProvider = tableProvider;
        currentTable = null;
    }

    public WorkingTableProvider() {
        // Only for tests.
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
