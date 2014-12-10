package ru.fizteh.fivt.students.ilin_ilia.storeable;


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
            } else if (columnIndex > 6) {
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
        if (columnIndex > 6) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return keys[columnIndex];
        }
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (containsTable.getColumnType(columnIndex) != Integer.class) {
            throw new ColumnFormatException("Types mismatching in method getIntAt.");
        } else if (columnIndex > 6) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return (Integer) keys[columnIndex];
        }
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (containsTable.getColumnType(columnIndex) != Long.class) {
            throw new ColumnFormatException("Types mismatching in method getIntAt.");
        } else if (columnIndex > 6) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return (Long) keys[columnIndex];
        }
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (containsTable.getColumnType(columnIndex) != Byte.class) {
            throw new ColumnFormatException("Types mismatching in method getIntAt.");
        } else if (columnIndex > 6) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return (Byte) keys[columnIndex];
        }
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (containsTable.getColumnType(columnIndex) != Float.class) {
            throw new ColumnFormatException("Types mismatching in method getIntAt.");
        } else if (columnIndex > 6) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return (Float) keys[columnIndex];
        }
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (containsTable.getColumnType(columnIndex) != Double.class) {
            throw new ColumnFormatException("Types mismatching in method getDoubleAt.");
        } else if (columnIndex > 6) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return (Double) keys[columnIndex];
        }
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (containsTable.getColumnType(columnIndex) != Boolean.class) {
            throw new ColumnFormatException("Types mismatching in method getBooleanAt.");
        } else if (columnIndex > 6) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return (Boolean) keys[columnIndex];
        }
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (containsTable.getColumnType(columnIndex) != String.class) {
            throw new ColumnFormatException("Types mismatching in method getStringAt.");
        } else if (columnIndex > 6) {
            throw new IndexOutOfBoundsException("Wrong index.");
        } else {
            return (String) keys[columnIndex];
        }
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
