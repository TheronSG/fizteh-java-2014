package ru.fizteh.fivt.students.ilin_ilia.junit;


import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.util.List;
import java.util.function.BiConsumer;

public class JUnitMain {

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
                        if (workingTableProvider.getCurrentTable() != null) {
                            workingTableProvider.getCurrentTable().put(arguments[0], arguments[1]);
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("get", 1, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        if (workingTableProvider.getCurrentTable() != null) {
                            workingTableProvider.getCurrentTable().get(arguments[0]);
                        } else {
                            System.out.println("no table");
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        if (workingTableProvider.getCurrentTable() != null) {
                            workingTableProvider.getCurrentTable().remove(arguments[0]);
                        } else {
                            System.out.println("no table");
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
                        if (workingTableProvider.getCurrentTable() != null) {
                            if (workingTableProvider.getCurrentTable().getName().equals(arguments[0])) {
                                workingTableProvider.setCurrentTable(null);
                            }
                        }
                        workingTableProvider.getTableProvider().removeTable(arguments[0]);
                        System.out.println("dropped");
                    }
                }),
                new Command("create", 1, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        workingTableProvider.getTableProvider().createTable(arguments[0]);
                        System.out.println("created");
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
                        if (workingTableProvider.getCurrentTable() != null) {
                            System.out.println("commit");
                            System.out.println(workingTableProvider.getCurrentTable().commit());
                        } else {
                            System.out.println("no table");
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
                        List<String> tableList = myTableProvider.getTableList();
                        if (tableList != null) {
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
