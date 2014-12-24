package ru.fizteh.fivt.students.ilin_ilia.parallel.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ilin_ilia.parallel.dbexceptions.TableException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class MyTableProvider implements TableProvider {
    private static final String PROHIBITED_SYMBOLS = "[~#@*+%{}<>\\[\\]\"_^]?/:*|";
    private Map<String, Table> tables;
    private Path currentFactory;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

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
                throw new TableException("Can't create the following directory: \""
                        + curTableProvider.getName() + "\"");
            }
        }
    }

    @Override
    public Table getTable(final String name) {
        lock.readLock().lock();
        try {
            if (name == null) {
                throw new IllegalArgumentException("Can't get table. Empty name is impossible for it.");
            } else if (!checkNameCorrection(name)) {
                throw new IllegalArgumentException("Can't get table. " + "\"" + name + "\" has inadmissible symbols");
            } else {
                return tables.get(name);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        lock.readLock().lock();
        try {
            if (name == null) {
                throw new IllegalArgumentException("Can't create table. Empty name is impossible for it.");
            } else if (!checkNameCorrection(name)) {
                throw new IllegalArgumentException("Can't create table. " + "\"" + name + "\" has inadmissible symbols");
            } else {
                if (tables.containsKey(name)) {
                    return null;
                } else {
                    try {
                        tables.put(name, new MyTable(currentFactory.resolve(name).toString(), columnTypes, this));
                    } catch (ParseException | ClassNotFoundException e) {
                        throw new IOException(e.getMessage());
                    }
                    return tables.get(name);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) {
        lock.writeLock().lock();
        try {
            if (name == null) {
                throw new IllegalArgumentException("Can't remove table. Empty name is impossible for it.");
            } else if (!checkNameCorrection(name)) {
                throw new IllegalArgumentException("Can't remove table. " + "\"" + name + "\" has inadmissible symbols");
            } else {
                if (!new File(currentFactory.resolve(name).toString()).exists()) {
                    throw new IllegalStateException(name + " not exists");
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
                MyTable myTable = (MyTable) tables.remove(name);
                myTable.setInvalidTrue();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        lock.readLock().lock();
        try {
            value = value.trim();
            if (value.charAt(0) != '[') {
                throw new ParseException("Error! String \"" + value + "\" doesn't match the JSON format array. "
                        + "\'[\' must be on the first place.", 0);
            }
            if (value.charAt(value.length() - 1) != ']') {
                throw new ParseException("Error! String \"" + value + "\" doesn't match the JSON format array. "
                        + "\']\' must be at the end of the string.", value.length() - 1);
            }
            if (value.substring(1, value.length() - 1).trim().equals("")) {
                throw new ParseException("Error! Empty value.", 1);
            }
            boolean isNullString = false;
            String buff = "";
            List<String> parts = new LinkedList<>();
            value = value.substring(1, value.length() - 1);
            for (int index = 0; index < value.length(); index++) {
                isNullString = false;
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
                            isNullString = true;
                        }
                        index++;
                    }
                }
                if (isNullString) {
                    buff += 'l';
                }
                parts.add(buff);
                buff = "";
            }
            Storeable storeable = createFor(table);
            try {
                for (int i = 0; i < table.getColumnsCount(); i++) {
                    if (parts.get(i).equals("null")) {
                        storeable.setColumnAt(i, null);
                    } else if (parts.get(i).equals("nulll")) {
                        storeable.setColumnAt(i, "null");
                    } else {
                        if (table.getColumnType(i) == Boolean.class) {
                            storeable.setColumnAt(i, Boolean.valueOf(parts.get(i)));
                        } else if (table.getColumnType(i) == String.class) {
                            storeable.setColumnAt(i, parts.get(i).substring(0, parts.get(i).length() - 1));
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
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        lock.readLock().lock();
        try {
            MyStoreable myValue = (MyStoreable) value;
            for (int i = 0; i < myValue.size(); i++) {
                if (value.getColumnAt(i) != null) {
                    if (value.getColumnAt(i).getClass() != table.getColumnType(i)) {
                        throw new ColumnFormatException("\"" + i
                                + "\" column doesn't match the column format of the table \""
                                + table.getName() + "\"");
                    }
                }
            }
            MyStoreable myStoreable = (MyStoreable) value;
            String resultString = "[";
            for (int index = 0; index < myStoreable.size(); index++) {
                if (value.getColumnAt(index) != null) {
                    if (table.getColumnType(index) == String.class) {
                        resultString += "\"" + value.getStringAt(index) + "\", ";
                    } else {
                        resultString += value.getColumnAt(index).toString() + ", ";
                    }
                } else {
                    resultString += "null, ";
                }
            }
            resultString = resultString.substring(0, resultString.length() - 2);
            resultString += "]";
            return resultString;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table) {
        lock.readLock().lock();
        try {
            return new MyStoreable(null, table);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        lock.readLock().lock();
        try {
            return new MyStoreable(values, table);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<String> getTableNames() {
        lock.readLock().lock();
        try {
            Set<String> tableNames = tables.keySet();
            return tableNames.stream().collect(Collectors.toCollection(() -> new LinkedList<>()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean checkNameCorrection(final String name) {
        Matcher matcher = Pattern.compile(PROHIBITED_SYMBOLS).matcher(name);
        return matcher.find();
    }

    public void saveDb() throws IOException {
        for (Table table: tables.values()) {
            MyTable myTable = (MyTable) table;
            myTable.saveDB();
        }
        System.out.println("All unsaved changes are rolled back.");
    }
}
