package ru.fizteh.fivt.students.ilin_ilia.junit.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.ilin_ilia.junit.commands.DataBaseCommand;
import ru.fizteh.fivt.students.ilin_ilia.junit.commands.LambdaFunction;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.WorkingTableProvider;
import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.StopInterpretationException;
import ru.fizteh.fivt.students.ilin_ilia.junit.interpreter.DBInterpreter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {
    private DBInterpreter dbInterpreter;
    private final String newLine = System.getProperty("line.separator");
    private final String testCommand = "test";
    private final String testOutput = "TEST";
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;
    private LambdaFunction<Object, String[]> printerCommand = (object, arguments)
            -> printStream.println(testOutput);

    @Before
    public void beforeTest() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }


    @Test
    public void interactiveTest1() {
        final PrintStream ps = new PrintStream(outputStream);

        dbInterpreter = new DBInterpreter(new WorkingTableProvider(null), new DataBaseCommand[]{
                new DataBaseCommand("test", 0, new WorkingTableProvider(),
                        (workingTableProvider, arguments) -> {
                            ps.print(testOutput + newLine);
                        })}, new ByteArrayInputStream(testCommand.getBytes()), ps);
        dbInterpreter.run(new String[]{});
        assertEquals("$ " + testOutput + newLine + "$ ", outputStream.toString());
    }

    @Test(expected = StopInterpretationException.class)
    public void batchTest1() throws IOException, StopInterpretationException {
        dbInterpreter = new DBInterpreter(null, new DataBaseCommand[]{
                new DataBaseCommand("exit", 0, new WorkingTableProvider(),
                        (workingTableProvider, arguments) -> {
                            throw new StopInterpretationException();
                        })});
        dbInterpreter.batchMode(new String[]{"exit"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterpreterThrowsExceptionConstructedForNullStream() {
        new DBInterpreter(null, new DataBaseCommand[] {}, null, null);
    }
}