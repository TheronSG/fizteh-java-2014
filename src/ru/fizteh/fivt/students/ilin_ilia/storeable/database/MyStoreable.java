package ru.fizteh.fivt.students.ilin_ilia.storeable.database;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.List;

public class MyStoreable implements Storeable {
    private Object[] keys;
    private Table containsTable;

    public MyStoreable(List<?> keys, Table table) {
        containsTable = table;
        if (keys != null) {
            this.keys = new Object[keys.size()];
            for (int i = 0; i < keys.size(); i++) {
                if (table.getColumnType(i) == keys.get(i).getClass()) {
                    this.keys[i] = keys.get(i);
                } else {
                    throw new ColumnFormatException("Can not cast \"" + keys.get(i) + "\" to "
                            + table.getColumnType(i) + ".");
                }
            }
        } else {
            this.keys = new Object[table.getColumnsCount()];
        }
    }


    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null) {
            if (value.getClass() != containsTable.getColumnType(columnIndex)) {
                throw new ColumnFormatException("Types mismatching in method setColumnAt.");
            } else if (columnIndex > keys.length) {
                throw new IndexOutOfBoundsException("Wrong index.");
            } else {
                keys[columnIndex] = value;
            }
        } else {
            keys[columnIndex] = null;
        }

    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex > keys.length) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return keys[columnIndex];
        }
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Integer.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Long.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Byte.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Float.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Double.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Boolean.class.cast(getColumnAt(columnIndex));
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return String.class.cast(getColumnAt(columnIndex));
    }

    public boolean equals(Storeable store) {
        for (int i = 0; i < keys.length; i++) {
            if (!keys[i].equals(store.getColumnAt(i))) {
                return false;
            }
        }
        return true;
    }

    int size() {
        return keys.length;
    }

    public Table getContainsTable() {
        return containsTable;
    }
}
