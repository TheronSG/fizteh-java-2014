package ru.fizteh.fivt.students.ilin_ilia.storeable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ilin_ilia.storeable.MyStoreable;
import ru.fizteh.fivt.students.ilin_ilia.storeable.MyTableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class MyTableProviderTest {
    private MyTableProvider myTableProvider;
    private Path testDir = Paths.get(System.getProperty("java.io.tmpdir"));

    @Before
    public void beforeTest() {
        try {
            myTableProvider = new MyTableProvider(testDir.toString() + "\\DbTest");
        } catch (IOException | ParseException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

   @Test(expected = IllegalArgumentException.class)
    public void createNullTableTest() throws IOException {
        myTableProvider.createTable(null, new LinkedList<Class<?>>());
    }

    @Test
    public void createNotNullTableTest() throws IOException {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Byte.class);
        myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
        assertTrue(testDir.resolve("DbTest").resolve("t1").toFile().exists());
    }

    @Test
    public void getNotExistsTableTest() {
        assertNull(myTableProvider.getTable("NotExistsTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTableTest() {
        myTableProvider.getTable(null);
    }

    @Test
    public void getExistsTableTest() throws IOException {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Byte.class);
        myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
        assertNotNull(myTableProvider.getTable(testDir.resolve("DbTest").resolve("t1").toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTableTest() {
        myTableProvider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void removeNotExistsTableTest() {
        myTableProvider.removeTable("NotExistsTable");
    }

    @Test
    public void removeExistsTableTest() throws IOException {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Integer.class);
        myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
        myTableProvider.removeTable(testDir.resolve("DbTest").resolve("t1").toString());
        assertTrue(!testDir.resolve("DbTest").resolve("t1").toFile().exists());
    }

    @Test
    public void deserializeTrueTest() {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Byte.class);
        try {
        Table testTable = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
            myTableProvider.deserialize(testTable, "[\"abracadabra\", 3]");
        } catch (ParseException | IOException e) {
            assertTrue(false);
        }
        /**
         * If something will go wrong the programme throw exception and fail the test.
         * Otherwise programme works correctly.
         */
        assertTrue(true);
    }

    @Test
    public void deserializeFalseTest() {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Boolean.class);
        try {
            Table testTable = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
            myTableProvider.deserialize(testTable, "[\"abracadabra\", \"ada\"]");
        } catch (ParseException | IOException e) {
            assertTrue(false);
        }
        /**
         * If something will go wrong the programme throw exception and fail the test.
         * Otherwise programme works correctly.
         */
        assertTrue(true);
    }

    @Test
    public void serializeTrueTest() {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(String.class);
        List<Object> listOfValues = new LinkedList<>();
        listOfValues.add("Something");
        listOfValues.add("strange");
        try {
            Table testTable = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
            Storeable testStoreable =  new MyStoreable(listOfValues, testTable);
            testStoreable.setColumnAt(0, "Buda!");
            testStoreable.setColumnAt(1, "pesht");
            myTableProvider.serialize(testTable, testStoreable);
        } catch (ColumnFormatException | IOException e) {
            assertTrue(false);
        }
        /**
         * If something will go wrong the programme throw exception and fail the test.
         * Otherwise programme works correctly.
         */
        assertTrue(true);
    }

    @Test
    public void serializeFalseTest() {
        List<Class<?>> list1 = new LinkedList<>();
        List<Class<?>> list2 = new LinkedList<>();
        list1.add(String.class);
        list1.add(Boolean.class);

        list1.add(String.class);
        list1.add(String.class);
        boolean isException = false;
        try {
            Table testTable1 = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list1);
            Storeable testStoreable =  new MyStoreable(list1, testTable1);
            testStoreable.setColumnAt(0, "Buda!");
            testStoreable.setColumnAt(1, "pesht");
            Table testTable2 = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list2);
            myTableProvider.serialize(testTable2, testStoreable);
        } catch (ColumnFormatException | IOException e) {
            assertTrue(true);
            isException = true;
        }
        /**
         * If something will go wrong the programme throw exception and fail the test.
         * Otherwise programme works correctly.
         */
        if (!isException) {
            assertTrue(false);
        }
    }

    @Test
    public void createForShortConstructorTest() throws IOException {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Integer.class);
        Table testTable = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
        assertNotNull(myTableProvider.createFor(testTable));
    }

    @Test
    public void createForLongConstructorTrueTest() throws IOException {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Integer.class);
        List<Object> listOfValues = new LinkedList<>();
        listOfValues.add("Something");
        listOfValues.add(42);
        Table testTable = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
        assertNotNull(myTableProvider.createFor(testTable, listOfValues));
    }

    @Test(expected = ColumnFormatException.class)
    public void createForLongConstructorFalseTest() throws IOException {
        List<Class<?>> list1 = new LinkedList<>();
        list1.add(String.class);
        list1.add(Integer.class);
        List<Object> list2 = new LinkedList<>();
        list2.add(false);
        list2.add("Hello!");
        Table testTable = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list1);
        myTableProvider.createFor(testTable, list2);
    }

    @Test
    public void getTableNamesTest() throws IOException {
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Integer.class);
        Table testTable = myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
        assertEquals(testDir.resolve("DbTest").resolve("t1").toString(), testTable.getName());
    }


    @After
    public void deleteTestDir() {

        for (String dir : new File(testDir.resolve("DbTest").toString()).list()) {
            testDir.resolve("DbTest").resolve(dir).toFile().delete();
        }
        testDir.resolve("DbTest").toFile().delete();
    }
}
