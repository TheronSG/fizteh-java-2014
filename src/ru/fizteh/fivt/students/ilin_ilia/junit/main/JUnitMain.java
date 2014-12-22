package ru.fizteh.fivt.students.ilin_ilia.junit.main;


import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.ilin_ilia.junit.commands.DataBaseCommand;
import ru.fizteh.fivt.students.ilin_ilia.junit.commands.LambdaFunction;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.MyTable;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.MyTableProvider;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.MyTableProviderFactory;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.WorkingTableProvider;
import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.StopInterpretationException;
import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.TableException;
import ru.fizteh.fivt.students.ilin_ilia.junit.interpreter.DBInterpreter;

import java.util.List;

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
                                String old = workingTableProvider.getCurrentTable().put(arguments[0], arguments[1]);
                                if (old == null) {
                                    System.out.println("new");
                                } else {
                                    System.out.println("overwrite");
                                    System.out.println(old);
                                }
                            } else {
                                System.out.println("no table");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            System.err.println(e.getMessage());
                        }
                }),
                new DataBaseCommand("get", 1, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                        try {
                            if (workingTableProvider.getCurrentTable() != null) {
                                String value = workingTableProvider.getCurrentTable().get(arguments[0]);
                                if (value == null) {
                                    System.out.println("not found");
                                } else {
                                    System.out.println("found");
                                    System.out.println(value);
                                }
                            } else {
                                System.out.println("no table");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            System.err.println(e.getMessage());
                        }
                }),
                new DataBaseCommand("remove", 1, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                        try {
                            if (workingTableProvider.getCurrentTable() != null) {
                                String value = workingTableProvider.getCurrentTable().remove(arguments[0]);
                                if (value == null) {
                                    System.out.println("not found");
                                } else {
                                    System.out.println("removed");
                                    System.out.println(value);
                                }
                            } else {
                                System.out.println("no table");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            System.err.println(e.getMessage());
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
                        } catch (RuntimeException e) {
                            System.err.println(e.getMessage());
                        }
                }),
                new DataBaseCommand("create", -1, workingTableProvider, (LambdaFunction<WorkingTableProvider, String[]>)
                        (WorkingTableProvider workingTableProvider1, String[] arguments) -> {
                        try {
                            if (workingTableProvider.getTableProvider().createTable(arguments[0]) == null) {
                                System.out.println(arguments[0] + " exists");
                            } else {
                                System.out.println("created");
                            }
                        } catch (IllegalArgumentException e) {
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
                        if (workingTableProvider.getCurrentTable() != null) {
                            System.out.println("commit");
                            System.out.println(workingTableProvider.getCurrentTable().commit());
                        } else {
                            System.out.println("no table");
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
                            MyTable myTable = (MyTable) workingTableProvider.getCurrentTable();
                            if (myTable.getChangesBeforeCommit() != 0) {
                                isChanges = true;
                                unsavedChanges = myTable.getChangesBeforeCommit();
                            }
                        }
                        if (workingTableProvider.getTableProvider().getTable(arguments[0]) != null) {
                            workingTableProvider.setCurrentTable(workingTableProvider.getTableProvider()
                                    .getTable(arguments[0]));
                            MyTable myTable = (MyTable)workingTableProvider.getCurrentTable();
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
                        List<String> tableList = myTableProvider.getTableList();
                        if (tableList != null) {
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
                                myTableProvider.saveDb();
                            }
                            throw new StopInterpretationException();
                        })
        }).run(args);
    }
}
