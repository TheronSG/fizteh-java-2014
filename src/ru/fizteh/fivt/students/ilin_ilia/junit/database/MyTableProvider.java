package ru.fizteh.fivt.students.ilin_ilia.junit.database;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.TableException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTableProvider implements TableProvider {
    private static final String PROHIBITED_SYMBOLS = "[~#@*+%{}<>\\[\\]\"_^]?/:*|";
    private Map<String, Table> tables;
    private Path currentFactory;

    public MyTableProvider(final String dir) {
        currentFactory = Paths.get(dir);
        File curTableProvider = currentFactory.toFile();
        tables = new HashMap<>();
        if (curTableProvider.exists()) {
            for (String file : curTableProvider.list()) {
                tables.put(file, new MyTable(currentFactory.resolve(file).toString()));
            }
        } else {
            try {
                curTableProvider.mkdir();
            } catch (SecurityException e) {
                throw new TableException("Can't create the following directory: \"" +
                        curTableProvider.getName() + "\"");
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
            return tables.get(name);
        }
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Can't create table. Empty name is impossible for it.");
        } else if (!checkNameCorrection(name)) {
            throw new IllegalArgumentException("Can't create table. " + "\"" + name + "\" has inadmissible symbols");
        } else {
            if (tables.containsKey(name)) {
                return null;
            } else {
                new File(currentFactory.resolve(name).toString()).mkdir();
                tables.put(name, new MyTable(currentFactory.resolve(name).toString()));
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
        }
        tables.remove(name);
    }

    public boolean checkNameCorrection(final String name) {
        Matcher matcher = Pattern.compile(PROHIBITED_SYMBOLS).matcher(name);
        return matcher.find();
    }

    public List<String> getTableList() {
        Set<String> tableNames = tables.keySet();
        List<String> tableList = new LinkedList<>();
        for (String name : tableNames) {
            tableList.add(name);
        }
        return tableList;
    }

    public void saveDb() {
        for (Table table: tables.values()) {
            MyTable myTable = (MyTable) table;
            myTable.saveDB();
        }
        System.out.println("All unsaved changes are rolled back.");
    }
}
