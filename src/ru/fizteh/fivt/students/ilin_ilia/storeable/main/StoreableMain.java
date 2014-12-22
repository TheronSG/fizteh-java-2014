package ru.fizteh.fivt.students.ilin_ilia.storeable.main;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.ilin_ilia.storeable.commands.DataBaseCommand;
import ru.fizteh.fivt.students.ilin_ilia.storeable.commands.LambdaFunction;
import ru.fizteh.fivt.students.ilin_ilia.storeable.database.*;
import ru.fizteh.fivt.students.ilin_ilia.storeable.dbexceptions.StopInterpretationException;
import ru.fizteh.fivt.students.ilin_ilia.storeable.dbexceptions.TableException;
import ru.fizteh.fivt.students.ilin_ilia.storeable.interpreter.DBInterpreter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StoreableMain {
    private static Map<String, String> classesMap = new HashMap<>();

    public static void main(final String [] args) {
        try {
            String dbDirPath = System.getProperty("fizteh.db.dir");
            if (dbDirPath == null) {
                System.err.println("You must specify fizteh.db.dir via -Dfizteh.db.dir JVM parametr.");
                System.exit(-1);
            }
            TableProviderFactory tableProviderFactory = new MyTableProviderFactory();
            TableProvider tableProvider = tableProviderFactory.create(dbDirPath);
            WorkingTableProvider workingTableProvider = new WorkingTableProvider(tableProvider);
            classesMap.put("int", "Integer");
            classesMap.put("boolean", "Boolean");
            classesMap.put("long", "Long");
            classesMap.put("double", "Double");
            classesMap.put("float", "Float");
            classesMap.put("byte", "Byte");
            run(workingTableProvider, args);
        } catch (TableException e) {
            System.err.println(e.getMessage());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void run(WorkingTableProvider workingTableProvider, String[]args) {
        new DBInterpreter(workingTableProvider, new DataBaseCommand[]{
                new DataBaseCommand("put", 2, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                        try {
                            if (workingTableProvider.getCurrentTable() != null) {
                                Table table = workingTableProvider.getCurrentTable();
                                Storeable storeable = workingTableProvider.getTableProvider().deserialize(table,
                                        arguments[1]);
                                MyStoreable myStoreable = (MyStoreable) workingTableProvider.getCurrentTable()
                                        .put(arguments[0], storeable);
                                if (myStoreable == null) {
                                    System.out.println("new");
                                } else {
                                    System.out.println("overwrite");
                                    TableProvider tableProvider = workingTableProvider.getTableProvider();
                                    System.out.println(tableProvider.serialize(table, myStoreable));
                                }
                            } else {
                                System.out.println("no table");
                            }
                        } catch (ParseException | IllegalStateException | IllegalArgumentException e) {
                            System.err.println("wrong type (" + e.getMessage() + ")");
                        }
                }),
                new DataBaseCommand("get", 1, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    try {
                        if (workingTableProvider.getCurrentTable() != null) {
                            if (workingTableProvider.getCurrentTable().get(arguments[0]) == null) {
                                System.out.println("not found");
                            } else {
                                System.out.println("found");
                                MyStoreable myStoreable = (MyStoreable) workingTableProvider.getCurrentTable()
                                        .get(arguments[0]);
                                Table table = myStoreable.getContainsTable();
                                TableProvider tableProvider = workingTableProvider.getTableProvider();
                                System.out.println(tableProvider.serialize(table, myStoreable));
                            }
                        } else {
                            System.out.println("no table");
                        }
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.err.println("wrong type (" + e.getMessage() + ")");
                    }
                }),
                new DataBaseCommand("remove", 1, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    try {
                        if (workingTableProvider.getCurrentTable() != null) {
                            MyStoreable myStoreable = (MyStoreable) workingTableProvider.getCurrentTable()
                                    .get(arguments[0]);
                            if (workingTableProvider.getCurrentTable().remove(arguments[0]) == null) {
                                System.out.println("not found");
                            } else {
                                System.out.println("removed");
                                Table table = myStoreable.getContainsTable();
                                TableProvider tableProvider = workingTableProvider.getTableProvider();
                                System.out.println(tableProvider.serialize(table, myStoreable));
                            }
                        } else {
                            System.out.println("no table");
                        }
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.err.println("wrong type (" + e.getMessage() + ")");
                    }
                }),
                new DataBaseCommand("list", 0, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    if (workingTableProvider.getCurrentTable() != null) {
                        System.out.println(String.join(", ", workingTableProvider.getCurrentTable().list()));
                    } else {
                        System.out.println("no table");
                    }
                }),
                new DataBaseCommand("drop", 1, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    try {
                        if (workingTableProvider.getCurrentTable() != null) {
                            if (workingTableProvider.getCurrentTable().getName().equals(arguments[0])) {
                                workingTableProvider.setCurrentTable(null);
                            }
                        }
                        workingTableProvider.getTableProvider().removeTable(arguments[0]);
                        System.out.println("dropped");
                    } catch (IOException | RuntimeException e) {
                        e.printStackTrace();
                    }
                }),
                new DataBaseCommand("create", -1, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    String className = "";
                    try {
                        List<Class<?>> classes = new LinkedList<>();
                        if (arguments[1].charAt(0) != '(') {
                            throw new IllegalArgumentException("Wrong data format! \'(\' is missed.");
                        }
                        if (arguments[arguments.length - 1].charAt(arguments[arguments.length - 1].length() -1)
                                != ')') {
                            throw new IllegalArgumentException("Wrong data format! \')\' is missed.");
                        }
                        if (arguments.length != 2) {
                            className = arguments[1].substring(1, arguments[1].length());
                            int i = 2;
                            if (className.equals("")) {
                                i = 3;
                                className = arguments[2];
                            }
                            if (classesMap.containsKey(className)) {
                                className = classesMap.get(className);
                            }
                            classes.add(Class.forName("java.lang." + className));
                            for (; i < arguments.length - 1; i++) {
                                className = arguments[i];
                                if (className.equals(")") || className.equals("(")) {
                                    continue;
                                }
                                if (classesMap.containsKey(className)) {
                                    className = classesMap.get(className);
                                }
                                classes.add(Class.forName("java.lang." + className));
                            }
                            className = arguments[i].substring(0, arguments[i].length() - 1);
                            if (classesMap.containsKey(className)) {
                                className = classesMap.get(className);
                            }
                            if (!className.equals("")) {
                                classes.add(Class.forName("java.lang." + className));
                            }
                        } else  {
                            className = arguments[1].substring(1, arguments[1].length() - 1);
                            if (classesMap.containsKey(className)) {
                                className = classesMap.get(className);
                            }
                            classes.add(Class.forName("java.lang." + className));
                        }
                        if (workingTableProvider.getTableProvider().createTable(arguments[0], classes) == null) {
                            System.out.println(arguments[0] + " exists");
                        } else {
                            System.out.println("created");
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error! Class \"" + className + "\" doesn't exist.");
                    } catch (IOException | IllegalArgumentException e) {
                        System.err.println(e.getMessage());
                    }
                }),
                new DataBaseCommand("size", 0, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    if (workingTableProvider.getCurrentTable() != null) {
                        System.out.println(workingTableProvider.getCurrentTable().size());
                    } else {
                        System.out.println("no table");
                    }
                }),
                new DataBaseCommand("commit", 0, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    try {
                        if (workingTableProvider.getCurrentTable() != null) {
                            System.out.println("commit");
                            System.out.println(workingTableProvider.getCurrentTable().commit());
                        } else {
                            System.out.println("no table");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }),
                new DataBaseCommand("rollback", 0, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    if (workingTableProvider.getCurrentTable() != null) {
                        System.out.println("rollback");
                        System.out.println(workingTableProvider.getCurrentTable().rollback());
                    } else {
                        System.out.println("no table");
                    }
                }),
                new DataBaseCommand("use", 1, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    int unsavedChanges = 0;
                    boolean isChanges = false;
                    if (workingTableProvider.getCurrentTable() != null) {
                        if (workingTableProvider.getCurrentTable() ==
                                workingTableProvider.getTableProvider().getTable(arguments[0])) {
                            System.out.println("Table has been already used.");
                            return;
                        }
                        MyTable myTable = (MyTable) workingTableProvider.getCurrentTable();
                        if (myTable.getNumberOfUncommittedChanges() != 0) {
                            isChanges = true;
                            unsavedChanges = myTable.getNumberOfUncommittedChanges();
                        }
                    }
                    if (workingTableProvider.getTableProvider().getTable(arguments[0]) != null) {
                        workingTableProvider.setCurrentTable(workingTableProvider.getTableProvider()
                                .getTable(arguments[0]));
                        MyTable myTable = (MyTable) workingTableProvider.getCurrentTable();
                        myTable.setIsInvitatedTrue();
                        System.out.println("using " + arguments[0]);
                    } else  {
                        System.out.println(arguments[0] + " not exists");
                    }
                    if (isChanges) {
                        System.err.println(unsavedChanges + " unsaved changes");
                    }
                }),
                new DataBaseCommand("show tables", 0, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                    MyTableProvider myTableProvider = (MyTableProvider) workingTableProvider.getTableProvider();
                    List<String> tableList = myTableProvider.getTableNames();
                    if (tableList.size() != 0) {
                        for (String tableName : tableList) {
                            Table table = workingTableProvider.getTableProvider().getTable(tableName);
                            System.out.println(tableName + " " + table.size());
                        }
                    } else {
                        System.out.println();
                    }

                }),
                new DataBaseCommand("exit", 0, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {

                        if (workingTableProvider != null) {
                            MyTableProvider myTableProvider = (MyTableProvider) workingTableProvider.getTableProvider();
                            try {
                                myTableProvider.saveDb();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        throw new StopInterpretationException();
                })
        }).run(args);
    }
}
