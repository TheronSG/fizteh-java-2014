package ru.fizteh.fivt.students.ilin_ilia.junit.interpreter;

import ru.fizteh.fivt.students.ilin_ilia.junit.utils.Utils;
import ru.fizteh.fivt.students.ilin_ilia.junit.commands.Command;
import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.StopInterpretationException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String STATEMENT_DELIMITER = ";";

    private final Map<String, Command> commands;
    private InputStream in;
    private PrintStream out;

    public Interpreter(Command[] commands) {
        this(commands, System.in, System.out);
    }

    public Interpreter(Command[] commands,
                       InputStream in, PrintStream out) {
        if (in == null) {
            throw new IllegalArgumentException("Null InputSteam.");
        }
        if (out == null) {
            throw new IllegalArgumentException("Null PrintSteam.");
        }
        this.in = in;
        this.out = out;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void run(String[] args) {
        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                batchMode(args);
            }
        } catch (StopInterpretationException e) {
            // Stop programme.
        }
    }

    public void interactiveMode() throws StopInterpretationException {
        Scanner in = new Scanner(this.in);
        while (true) {
            out.print(PROMPT);
            try {
                String line = in.nextLine();
                executeLine(line);
            } catch (NoSuchElementException e) {
                break;
            }
        }
    }

    public void batchMode(final String[] args) throws StopInterpretationException {
        executeLine(String.join(" ", args));
    }

    private void executeLine(String line) throws StopInterpretationException {
        String[] statements = line.split(STATEMENT_DELIMITER);
        for (String statement : statements) {
            String[] chunks = CommandSeparator.setCommand(statement);
            String commandName = chunks[0];
            String[] params = Arrays.copyOfRange(chunks, 1, chunks.length);
            Command command = commands.get(commandName);

            if (command == null) {
                Utils.interpreterError("Unknown command: " + String.join(" ", chunks));
            } else {
                command.execute(new Object(), params);
            }
        }
    }
}
