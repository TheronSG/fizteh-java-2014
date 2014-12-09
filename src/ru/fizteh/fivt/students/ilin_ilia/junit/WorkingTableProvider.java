package ru.fizteh.fivt.students.ilin_ilia.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class WorkingTableProvider {
    private TableProvider tableProvider;
    private Table currentTable;

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
