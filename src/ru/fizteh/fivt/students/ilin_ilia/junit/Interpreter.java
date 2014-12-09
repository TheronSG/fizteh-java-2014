package ru.fizteh.fivt.students.ilin_ilia.junit;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String STATEMENT_DELIMITER = ";";
    public static final String PARAM_REGEXP = "\\S+";
    private final Map<String, Command> commands;
    private WorkingTableProvider myWorkingTableProvider;
    private InputStream in;
    private PrintStream out;

    public Interpreter(WorkingTableProvider workingTableProvider, Command[] commands) {
        this(workingTableProvider, commands, System.in, System.out);
    }

    public Interpreter(WorkingTableProvider workingTableProvider, Command[] commands,
                       InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
        this.commands = new HashMap<>();
        this.myWorkingTableProvider = workingTableProvider;
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
            String[] chunks = Utils.findAll(PARAM_REGEXP, statement);

            String commandName;
            String[] params;
            if (chunks.length > 1 && chunks[0].equals("show") && chunks[1].equals("tables")) {
                params = Arrays.copyOfRange(chunks, 2, chunks.length);
                commandName = "show tables";
            } else {
                commandName = chunks[0];
                params = Arrays.copyOfRange(chunks, 1, chunks.length);
            }
            Command command = commands.get(commandName);
            if (commandName.equals("exit")) {
                MyTableProvider myTableProvider = (MyTableProvider) myWorkingTableProvider.getTableProvider();
                myTableProvider.saveDb();
                throw new StopInterpretationException();
            }
            if (command == null) {
                Utils.interpreterError("Unknown command: " + String.join(" ", chunks));
            } else {
                command.execute(myWorkingTableProvider, params);
            }
        }
    }
}
