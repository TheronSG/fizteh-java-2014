package ru.fizteh.fivt.students.ilin_ilia.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTableProvider implements TableProvider {
    private Map<String, Table> tables;
    private Path currentFactory;

    public MyTableProvider(final String dir) {
        currentFactory = Paths.get(dir);
        File curTableProvider = currentFactory.toFile();
        tables = new HashMap<String, Table>();
        if (curTableProvider.exists()) {
            for (String file : curTableProvider.list()) {
                tables.put(file, new MyTable(currentFactory.resolve(file).toString()));
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

    public boolean checkNameCorrection(final String name) {
        Matcher matcher = Pattern.compile("[~#@*+%{}<>\\[\\]\"\\_^]?/:*|").matcher(name);
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
    }
}
