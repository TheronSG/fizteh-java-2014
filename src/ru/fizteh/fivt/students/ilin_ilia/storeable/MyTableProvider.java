package ru.fizteh.fivt.students.theronsg.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTableProvider implements TableProvider {
    private Map<String, Table> tables;
    private Path currentFactory;

    public MyTableProvider(final String dir) throws IOException, ClassNotFoundException, ParseException {
        currentFactory = Paths.get(dir);
        File curTableProvider = currentFactory.toFile();
        tables = new HashMap<>();
        if (curTableProvider.exists()) {
            for (String file : curTableProvider.list()) {
                tables.put(file, new MyTable(currentFactory.resolve(file).toString(), null, this));
            }
        } else {
            try {
                curTableProvider.mkdir();
            } catch (SecurityException e) {
                System.err.println("Can't create the following directory: \"" + curTableProvider.getName() + "\"");
            }
        }
    }

    @Override
    public Table getTable(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Can't get table. Empty name is impossible for it.");
        } else if (!checkNameCorrection(name)) {
            throw new IllegalArgumentException("Can't get table. " + "\"" + name + "\" has inadmissible symbols");
        } else {
            if (tables.containsKey(name)) {
                return tables.get(name);
            } else {
                return null;
            }
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Can't create table. Empty name is impossible for it.");
        } else if (!checkNameCorrection(name)) {
            throw new IllegalArgumentException("Can't create table. " + "\"" + name + "\" has inadmissible symbols");
        } else {
            if (tables.containsKey(name)) {
                return null;
            } else {
                try {
                    try {
                        tables.put(name, new MyTable(currentFactory.resolve(name).toString(), columnTypes, this));
                    } catch (ParseException e) {
                        throw new IOException(e.getMessage());
                    }
                } catch (ClassNotFoundException e) {
                    throw new IOException(e.getMessage());
                }
                return tables.get(name);
            }
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Can't remove table. Empty name is impossible for it.");
        } else if (!checkNameCorrection(name)) {
            throw new IllegalArgumentException("Can't remove table. " + "\"" + name + "\" has inadmissible symbols");
        } else {
            if (!new File(currentFactory.resolve(name).toString()).exists()) {
                throw new IllegalStateException("Can't remove the following table: " + "\"" + name
                        + "\". It doesn't exist.");
            }
            File fileName = new File(currentFactory.resolve(name).toString());
            if (fileName.isFile()) {
                if (!fileName.delete()) {
                    throw new RuntimeException("Can't remove the following table: " + "\"" + name + "\""
                            + "There is some IO problem.");
                }
            }
            if (fileName.isDirectory()) {
                if (fileName.list().length == 0) {
                    fileName.delete();
                } else {
                    for (String s : fileName.list()) {
                        removeTable(Paths.get(fileName.getAbsolutePath()).resolve(s).toString());
                    }
                    if (!fileName.delete()) {
                        throw new RuntimeException("Can't remove the following table: " + "\"" + name + "\""
                                + "There is some IO problem.");
                    }
                }
            }
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        value = value.trim();
        if (value.charAt(0) != '[') {
            throw new ParseException("Error! String \"" + value + "\" doesn't match the JSON format array. "
                    + "\'[\' must be on the first place.", 0);
        }
        if (value.charAt(value.length() - 1) != ']') {
            throw new ParseException("Error! String \"" + value + "\" doesn't match the JSON format array. "
                    + "\']\' must be at the end of the string.", 0);
        }
        String buff = "";
        List<String> parts = new LinkedList<>();
        value = value.substring(1, value.length() - 1);
        for (int index = 0; index < value.length(); index++) {
            while (index < value.length() && value.charAt(index) != ',') {
                if (value.charAt(index) != '"') {
                    if (value.charAt(index) == ' ') {
                        index++;
                    } else {
                        buff += value.charAt(index);
                        index++;
                    }
                } else {
                    index++;
                    while (value.charAt(index) != '"') {
                        buff += value.charAt(index);
                        index++;
                    }
                    index++;
                }
            }
            parts.add(buff);
            buff = "";
        }
        Storeable storeable = createFor(table);
        try {
            for (int i = 0; i < table.getColumnsCount(); i++) {
                if (parts.get(i).equals("null")) {
                    storeable.setColumnAt(i, null);
                } else {
                    if (table.getColumnType(i) == Boolean.class) {
                        storeable.setColumnAt(i, Boolean.valueOf(parts.get(i)));
                    } else if (table.getColumnType(i) == String.class) {
                        storeable.setColumnAt(i, parts.get(i));
                    } else if (table.getColumnType(i) == Integer.class) {
                        storeable.setColumnAt(i, Integer.valueOf(parts.get(i)));
                    } else if (table.getColumnType(i) == Long.class) {
                        storeable.setColumnAt(i, Long.valueOf(parts.get(i)));
                    } else if (table.getColumnType(i) == Byte.class) {
                        storeable.setColumnAt(i, Byte.valueOf(parts.get(i)));
                    } else if (table.getColumnType(i) == Float.class) {
                        storeable.setColumnAt(i, Float.valueOf(parts.get(i)));
                    } else if (table.getColumnType(i) == Double.class) {
                        storeable.setColumnAt(i, Double.valueOf(parts.get(i)));
                    } else {
                        // Can not be.
                    }
                }
            }
        } catch (ClassCastException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return storeable;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        MyStoreable myValue = (MyStoreable) value;
        for (int i = 0; i < myValue.size(); i++) {
            if (value.getColumnAt(i).getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException("\"" + i + "\" column doesn't match the column format of the table \""
                        + table.getName() + "\"");
            }
        }
        MyStoreable myStoreable = (MyStoreable) value;
        String resultString = "[";
        for (int index = 0; index < myStoreable.size(); index++) {
            if (table.getColumnType(index) == String.class) {
                resultString += "\"" + value.getStringAt(index) + "\",";
            } else {
                resultString += " " + value.getColumnAt(index).toString() + ",";
            }
        }
        resultString = resultString.substring(0, resultString.length() - 1);
        resultString += "]";
        return resultString;
    }

    @Override
    public Storeable createFor(Table table) {
        return new MyStoreable(null, table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return new MyStoreable(values, table);
    }

    @Override
    public List<String> getTableNames() {
        Set<String> tableNames = tables.keySet();
        List<String> tableList = new LinkedList<>();
        for (String name : tableNames) {
            tableList.add(name);
        }
        return tableList;
    }

    public boolean checkNameCorrection(final String name) {
        Matcher matcher = Pattern.compile("[~#@*+%{}<>\\[\\]\"_^]?/:*|").matcher(name);
        return matcher.find();
    }

    public void saveDb() throws IOException {
        for (Table table: tables.values()) {
            MyTable myTable = (MyTable) table;
            myTable.saveDB();
        }
    }
}
