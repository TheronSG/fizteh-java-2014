package ru.fizteh.fivt.students.ilin_ilia.junit;

import java.util.function.BiConsumer;

public class Command {
    private String name;
    private int argumentsAmount;
    private BiConsumer<WorkingTableProvider, String[]> callback;

    public Command(final String name, final int argumentsAmount,
                        final BiConsumer<WorkingTableProvider, String[]> callback) {
        this.name = name;
        this.argumentsAmount = argumentsAmount;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public void execute(WorkingTableProvider workingTableProvider, String[] parameters) {
        if (parameters.length != argumentsAmount) {
            Utils.interpreterError("Invalid number of arguments: " + argumentsAmount + " expected, " + parameters.length
                    + " found.");
        } else {
            callback.accept(workingTableProvider, parameters);
        }
    }
}
