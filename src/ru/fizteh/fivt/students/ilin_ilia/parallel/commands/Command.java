package ru.fizteh.fivt.students.ilin_ilia.parallel.commands;

import ru.fizteh.fivt.students.ilin_ilia.parallel.dbexceptions.StopInterpretationException;

public abstract class Command {
    private String name;
    private int argumentsAmount;
    private DataBaseCommandsExecutor<Object, String[]> callback;

    public Command(final String name, final int argumentsAmount,
                        final DataBaseCommandsExecutor<Object, String[]> callback) {
        this.name = name;
        this.argumentsAmount = argumentsAmount;
        this.callback = callback;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void execute(Object object, String[] parameters) throws StopInterpretationException;

    public int getArgumentsAmount() {
        return argumentsAmount;
    }

    public void setArgumentsAmount(int argumentsAmount) {
        this.argumentsAmount = argumentsAmount;
    }

    public DataBaseCommandsExecutor<Object, String[]> getCallback() {
        return callback;
    }

    public void setCallback(DataBaseCommandsExecutor<Object, String[]> callback) {
        this.callback = callback;
    }
}
