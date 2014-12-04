package ru.fizteh.fivt.students.theronsg.storeable.tests;

import org.junit.Test;
import ru.fizteh.fivt.students.theronsg.storeable.Command;
import ru.fizteh.fivt.students.theronsg.storeable.Interpreter;
import ru.fizteh.fivt.students.theronsg.storeable.StopInterpretationException;
import ru.fizteh.fivt.students.theronsg.storeable.WorkingTableProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {
    private Interpreter interpreter;

    @Test
    public void interactiveTest1() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        interpreter = new Interpreter(new WorkingTableProvider(null), new Command[]{
                new Command("test", 0, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        ps.print("TEST\n");
                    }
                })}, new ByteArrayInputStream("test".getBytes()), ps);
        interpreter.run(new String[]{});
        assertEquals("$ TEST\n$ ", baos.toString());
    }

    @Test(expected = StopInterpretationException.class)
    public void batchTest1() throws IOException, StopInterpretationException {
        interpreter = new Interpreter(null, new Command[]{
                new Command("test", 0, new BiConsumer<WorkingTableProvider, String[]>() {
                    @Override
                    public void accept(WorkingTableProvider workingTableProvider, String[] arguments) {
                        // Nothing to do.
                    }
                })});
        interpreter.batchMode(new String[]{"exit"});
    }

}
