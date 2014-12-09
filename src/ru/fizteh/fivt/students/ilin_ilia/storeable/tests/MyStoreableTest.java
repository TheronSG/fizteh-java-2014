package ru.fizteh.fivt.students.ilin_ilia.storeable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.ilin_ilia.storeable.MyStoreable;
import ru.fizteh.fivt.students.ilin_ilia.storeable.MyTable;
import ru.fizteh.fivt.students.ilin_ilia.storeable.MyTableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MyStoreableTest {
    private MyTableProvider myTableProvider;
    private Path testDir = Paths.get(System.getProperty("java.io.tmpdir"));
    private MyTable myTable;
    private MyStoreable myStoreable;

    @Before
    public void beforeTest() throws ParseException, IOException, ClassNotFoundException {
        myTableProvider = new MyTableProvider(testDir.toString() + "\\DbTest");
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Integer.class);
        myTable = (MyTable) myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
        myStoreable = (MyStoreable) myTableProvider.createFor(myTable);
    }

    @Test
    public void setColumnAtWithoutExceptionTest() {
        boolean isException = false;
        try {
            myStoreable.setColumnAt(0, "No Exception");
            myStoreable.setColumnAt(1, 42);
            assertTrue(true);
        } catch (Exception e) {
            isException = true;
        }
        if(isException) {
            assertTrue(false);
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnAtIndexOutOfBoundExceptionTest() {
        myStoreable.setColumnAt(3, "No Exception");
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnAtColumnFormatExceptionTest() {
        myStoreable.setColumnAt(1, "No Exception");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnAtWithExceptionTest() {
        myStoreable.setColumnAt(0, "No Exception");
        myStoreable.setColumnAt(1, 42);
        myStoreable.getColumnAt(2);
    }

    @Test
    public void getColumnAtWithoutExceptionTest() {
        myStoreable.setColumnAt(0, "No Exception");
        myStoreable.setColumnAt(1, 42);
        assertEquals(myStoreable.getColumnAt(1), 42);
    }

    @Test
    public void getStringAtTest() {
        myStoreable.setColumnAt(0, "No Exception");
        myStoreable.setColumnAt(1, 42);
        assertEquals(myStoreable.getColumnAt(0), "No Exception");
    }

    @After
    public void deleteTestDir() {

        for (String dir : new File(testDir.resolve("DbTest").toString()).list()) {
            for (String file : new File(testDir.resolve("DbTest").resolve(dir).toString()).list()) {
                testDir.resolve("DbTest").resolve(dir).resolve(file).toFile().delete();
            }
            testDir.resolve("DbTest").resolve(dir).toFile().delete();
        }
        testDir.resolve("DbTest").toFile().delete();
    }
}
