package ru.fizteh.fivt.students.ilin_ilia.parallel.interpreter;

import ru.fizteh.fivt.students.ilin_ilia.parallel.commands.Command;
import ru.fizteh.fivt.students.ilin_ilia.parallel.dbexceptions.StopInterpretationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class Interpreter {
    public static final String PROMPT = "$ ";

    private final Map<String, Command> commands;
    private InputStream in;
    private PrintStream out;

    public Interpreter(Command[] commands) {
        this(commands, System.in, System.out);
    }

    public Interpreter(Command[] commands,
                       InputStream in, PrintStream out) {
        if (in == null) {
            throw new IllegalArgumentException("Null InputSteam");
        }
        if (out == null) {
            throw new IllegalArgumentException("Null PrintSteam");
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
            // Stop program.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void interactiveMode() throws StopInterpretationException, IOException {
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

    public void batchMode(final String[] args) throws StopInterpretationException, IOException {
        executeLine(String.join(" ", args));
    }
    public abstract void executeLine(String line) throws StopInterpretationException, IOException;

    public Map<String, Command> getCommands() {
        return  commands;
    }
}
