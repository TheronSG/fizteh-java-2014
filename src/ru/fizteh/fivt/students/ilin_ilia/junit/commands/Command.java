package ru.fizteh.fivt.students.ilin_ilia.junit.commands;

import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.StopInterpretationException;

import java.util.function.BiConsumer;

public abstract class Command {
    private String name;
    private int argumentsAmount;
    private BiConsumer<Object, String[]> callback;

    public Command(final String name, final int argumentsAmount,
                   final BiConsumer<Object, String[]> callback) {
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
