package ru.fizteh.fivt.students.ilin_ilia.junit.commands;

import ru.fizteh.fivt.students.ilin_ilia.junit.utils.Utils;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.WorkingTableProvider;
import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.StopInterpretationException;

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
        if (parameters.length != argumentsAmount) {
            Utils.interpreterError("Invalid number of arguments: " + argumentsAmount + " expected, " + parameters.length
                    + " found.");
        } else {
            callback.accept(workingTableProvider, parameters);
        }
    }
}
