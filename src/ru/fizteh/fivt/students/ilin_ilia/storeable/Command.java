package ru.fizteh.fivt.students.ilin_ilia.storeable;

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
        /**
         * if argumentsAmount == -1 it means that function may have different amount of parameters.
         */
        if (argumentsAmount != -1) {
            if (parameters.length != argumentsAmount) {
                Utils.interpreterError("Invalid number of arguments: " + argumentsAmount + " expected, " + parameters.length
                        + " found.");
            } else {
                callback.accept(workingTableProvider, parameters);
            }
        } else {
            if (name.equals("create") && parameters.length > 1) {
                callback.accept(workingTableProvider, parameters);
            } else {
                Utils.interpreterError("Invalid number of arguments: expected at least 2, but " + parameters.length
                        + " found.");
            }

        }
    }
}
