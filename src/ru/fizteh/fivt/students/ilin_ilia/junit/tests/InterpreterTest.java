package ru.fizteh.fivt.students.ilin_ilia.junit.tests;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import ru.fizteh.fivt.students.theronsg.junit.*;

public class InterpreterTest {
    private Interpreter interpreter;

    @Test
    public void test() {
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
}
