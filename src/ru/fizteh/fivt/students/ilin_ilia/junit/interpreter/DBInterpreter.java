package ru.fizteh.fivt.students.ilin_ilia.junit.interpreter;

import ru.fizteh.fivt.students.ilin_ilia.junit.commands.Command;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.WorkingTableProvider;

import java.io.InputStream;
import java.io.PrintStream;

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
}
