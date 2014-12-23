package ru.fizteh.fivt.students.ilin_ilia.parallel.commands;

import ru.fizteh.fivt.students.ilin_ilia.parallel.dbexceptions.StopInterpretationException;

public abstract class Command {
    private String name;
    private int argumentsAmount;
    private LambdaFunction<Object, String[]> callback;

    public Command(final String name, final int argumentsAmount,
                        final LambdaFunction<Object, String[]> callback) {
        this.name = name;
        this.argumentsAmount = argumentsAmount;
        this.callback = callback;
    }

    public Command() {
    }

    public String getName() {
        return name;
    }

    public abstract void execute(Object object, String[] parameters) throws StopInterpretationException;
}
