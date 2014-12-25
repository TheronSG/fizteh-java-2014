package ru.fizteh.fivt.students.ilin_ilia.parallel.interpreter;

import ru.fizteh.fivt.students.ilin_ilia.parallel.commands.Command;
import ru.fizteh.fivt.students.ilin_ilia.parallel.database.WorkingTableProvider;
import ru.fizteh.fivt.students.ilin_ilia.parallel.dbexceptions.StopInterpretationException;
import ru.fizteh.fivt.students.ilin_ilia.parallel.utils.Utils;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class DBInterpreter extends Interpreter {
    WorkingTableProvider workingTableProvider;

    public DBInterpreter(WorkingTableProvider workingTableProvider, Command[] commands) {
        super(commands);
        this.workingTableProvider = workingTableProvider;
    }

    public DBInterpreter(WorkingTableProvider workingTableProvider,
                         Command[] commands, InputStream in, PrintStream out) {
        super(commands, in, out);
        this.workingTableProvider = workingTableProvider;
    }

    @Override
    public void executeLine(String line) throws StopInterpretationException {
        CommandSeparator commandSeparator =  new DBCommandSeparator();
        String[] statements = commandSeparator.separateLine(line);
        for (String statement : statements) {
            String[] chunks = commandSeparator.setCommand(statement);
            String commandName = chunks[0];
            Command command = getCommands().get(commandName);
            String [] params = Arrays.copyOfRange(chunks, 1, chunks.length);
            if (command == null) {
                Utils.interpreterError("Unknown command: " + String.join(" ", chunks));
            } else {
                command.execute(new Object(), params);
            }
        }
    }
}
