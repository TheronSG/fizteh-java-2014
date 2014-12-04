package ru.fizteh.fivt.students.theronsg.storeable;

import ru.fizteh.fivt.storage.structured.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class StoreableMain {

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
            run(workingTableProvider, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void run(WorkingTableProvider workingTableProvider, String[]args) {
        new Interpreter(workingTableProvider, new Command[]{
                new Command("put", 2, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
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
                        } catch (ParseException | ColumnFormatException e) {
                            System.err.println("wrong type (" + e.getMessage() + ")");
                        }
                    }
                }),
                new Command("get", 1, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
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
                        } catch (ColumnFormatException e) {
                            System.err.println("wrong type (" + e.getMessage() + ")");
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
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
                        } catch (ColumnFormatException e) {
                            System.err.println("wrong type (" + e.getMessage() + ")");
                        }
                    }
                }),
                new Command("list", 0, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        if (workingTableProvider.getCurrentTable() != null) {
                            workingTableProvider.getCurrentTable().list();
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("drop", 1, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
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
                    }
                }),
                new Command("create", -1, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        String clas = "";
                        try {
                            List<Class<?>> classes = new LinkedList<>();
                            if (arguments.length != 2) {
                                clas = arguments[1].substring(1, arguments[1].length());
                                classes.add(Class.forName("java.lang." + clas));
                                int i;
                                for (i = 2; i < arguments.length - 1; i++) {
                                    clas = arguments[i];
                                    classes.add(Class.forName("java.lang." + arguments[i]));
                                }
                                clas = arguments[i].substring(0, arguments[i].length() - 1);
                                classes.add(Class.forName("java.lang." + clas));
                            } else  {
                                clas = arguments[1].substring(1, arguments[1].length() - 1);
                                classes.add(Class.forName("java.lang." + clas));
                            }
                            workingTableProvider.getTableProvider().createTable(arguments[0], classes);
                            System.out.println("created");
                        } catch (ClassNotFoundException e) {
                            System.err.println("Error! Class \"" + clas + "\" doesn't exist.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }),
                new Command("size", 0, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        if (workingTableProvider.getCurrentTable() != null) {
                            System.out.println(workingTableProvider.getCurrentTable().size());
                        } else {
                            System.out.println("no table");
                        }
                    }

                }),
                new Command("commit", 0, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
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
                    }
                }),
                new Command("rollback", 0, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        if (workingTableProvider.getCurrentTable() != null) {
                            System.out.println("rollback");
                            System.out.println(workingTableProvider.getCurrentTable().rollback());
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("use", 1, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        int unsavedChanges = 0;
                        boolean isChanges = false;
                        if (workingTableProvider.getCurrentTable() != null) {
                            MyTable myTable = (MyTable) workingTableProvider.getCurrentTable();
                            if (myTable.getChangesBeforeCommit() != 0) {
                                isChanges = true;
                                unsavedChanges = myTable.getChangesBeforeCommit();
                            }
                        }
                        if (workingTableProvider.getTableProvider().getTable(arguments[0]) != null) {
                            workingTableProvider.setCurrentTable(workingTableProvider.getTableProvider()
                                    .getTable(arguments[0]));
                            System.out.println("using " + arguments[0]);
                        } else  {
                            System.out.println(arguments[0] + " not exists");
                        }
                        if (isChanges) {
                            System.err.println(unsavedChanges + " unsaved changes");
                        }
                    }
                }),
                new Command("show tables", 0, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
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
                    }
                })
        }).run(args);
    }
}
