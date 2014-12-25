package ru.fizteh.fivt.students.ilin_ilia.parallel.commands;

import ru.fizteh.fivt.students.ilin_ilia.parallel.utils.Utils;
import ru.fizteh.fivt.students.ilin_ilia.parallel.database.WorkingTableProvider;
import ru.fizteh.fivt.students.ilin_ilia.parallel.dbexceptions.StopInterpretationException;

public class DataBaseCommand extends Command {

    private WorkingTableProvider workingTableProvider;

    public DataBaseCommand(final String name, final int argumentsAmount,
                           final WorkingTableProvider workingTableProvider,
                   final DataBaseCommandsExecutor callback) {
        super(name, argumentsAmount, callback);
        this.workingTableProvider = workingTableProvider;
    }

    @Override
    public void execute(Object object, String[] parameters) throws StopInterpretationException {
        /**
         * if argumentsAmount == -1 it means that function may have different amount of parameters.
         */
        if (getArgumentsAmount() != -1) {
            if (parameters.length != getArgumentsAmount()) {
                Utils.interpreterError("Invalid number of arguments: "
                        + getArgumentsAmount() + " expected, " + parameters.length
                        + " found.");
            } else {
                getCallback().accept(workingTableProvider, parameters);
            }
        } else {
            if (getName().equals("create") && parameters.length > 1) {
                getCallback().accept(workingTableProvider, parameters);
            } else {
                Utils.interpreterError("Invalid number of arguments: expected at least 2, but " + parameters.length
                        + " found.");
            }

        }
    }
}
