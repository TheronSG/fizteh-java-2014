package ru.fizteh.fivt.students.ilin_ilia.storeable.commands;

import ru.fizteh.fivt.students.ilin_ilia.storeable.utils.Utils;
import ru.fizteh.fivt.students.ilin_ilia.storeable.database.WorkingTableProvider;
import ru.fizteh.fivt.students.ilin_ilia.storeable.dbexceptions.StopInterpretationException;

public class DataBaseCommand extends Command {

    private String name;
    private int argumentsAmount;
    private LambdaFunction callback;
    private WorkingTableProvider workingTableProvider;

    public DataBaseCommand(final String name, final int argumentsAmount,
                           final WorkingTableProvider workingTableProvider,
                   final LambdaFunction callback) {
        this.name = name;
        this.argumentsAmount = argumentsAmount;
        this.callback = callback;
        this.workingTableProvider = workingTableProvider;
    }

    public String getName() {
        return name;
    }

    @Override
    public void execute(Object object, String[] parameters) throws StopInterpretationException {
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
