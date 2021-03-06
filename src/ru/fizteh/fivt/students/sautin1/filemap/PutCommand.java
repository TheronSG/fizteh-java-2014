package ru.fizteh.fivt.students.sautin1.filemap;

import ru.fizteh.fivt.students.sautin1.filemap.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.filemap.shell.UserInterruptException;

/**
 * Put command.
 * Created by sautin1 on 10/12/14.
 */
public class PutCommand extends AbstractStringDatabaseCommand {

    public PutCommand() {
        super("put", 2, 2);
    }

    /**
     * Put new key and value to the active table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(StringDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        String value = state.getActiveTable().put(args[1], args[2]);
        if (value == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
    }
}
