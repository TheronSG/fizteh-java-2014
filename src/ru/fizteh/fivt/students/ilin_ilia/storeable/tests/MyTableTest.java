package ru.fizteh.fivt.students.ilin_ilia.storeable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
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
import static org.junit.Assert.assertNull;

public class MyTableTest {
    private MyTableProvider myTableProvider;
    private Path testDir = Paths.get(System.getProperty("java.io.tmpdir"));
    private MyTable myTable;

    @Before
    public void beforeTest() throws ParseException, IOException, ClassNotFoundException {
        myTableProvider = new MyTableProvider(testDir.toString() + "\\DbTest");
        List<Class<?>> list = new LinkedList<>();
        list.add(String.class);
        list.add(Integer.class);
        myTable = (MyTable) myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString(), list);
    }

    @Test
    public void removeExistedKeyTest() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        myTable.put("k1", testStoreable);
        myTable.commit();
        assertEquals(myTable.remove("k1"), testStoreable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKeyTest() {
        myTable.remove(null);
    }

    @Test
    public void removeNotExistedKeyTest() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        myTable.put("k1", testStoreable);
        assertNull(myTable.remove("k1"));
    }

    @Test
    public void getExistedKeyTest() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        myTable.put("k1", testStoreable);
        myTable.commit();
        assertEquals(myTable.get("k1"), testStoreable);
    }

    @Test
    public void getNotExistedKeyTest() {
        assertNull(myTable.get("k1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKeyTest() {
        myTable.get(null);
    }

    @Test
    public void putNormalKeyWithoutOverwritingTest() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        assertNull(myTable.put("k1", testStoreable));
    }

    @Test
    public void putNormalKeyWithOverwritingTest() {
        List<Object> values1 = new LinkedList<>();
        values1.add(0, "Five");
        values1.add(1, 5);
        Storeable testStoreable1 = new MyStoreable(values1, myTable);
        myTable.put("k1", testStoreable1);
        List<Object> values2 = new LinkedList<>();
        values2.add(0, "Four");
        values2.add(1, 4);
        Storeable testStoreable2 = new MyStoreable(values2, myTable);
        assertEquals(myTable.put("k1", testStoreable2), testStoreable1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKeyTest() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        myTable.put(null, testStoreable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullValueTest() {
        myTable.put("k1", null);
    }

    @Test
    public void getExistsNameTest() {
        assertEquals(myTable.getName(), testDir.resolve("DbTest").resolve("t1").toString());
    }

    @Test
    public void sizeNullTest1() {
        assertEquals(myTable.size(), 0);
    }

    @Test
    public void sizeNullTest2() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        myTable.put("k1", testStoreable);
        assertEquals(myTable.size(), 0);
    }

    @Test
    public void sizeNotNullTest() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        myTable.put("k1", testStoreable);
        myTable.commit();
        assertEquals(myTable.size(), 1);
    }

    @Test
    public void commitNullTest() {
        assertEquals(myTable.commit(), 0);
    }

    @Test
    public void commitNotNullTest() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        myTable.put("k1", testStoreable);
        assertEquals(myTable.commit(), 1);
    }

    @Test
    public void rollbackNullTest() {
        assertEquals(myTable.rollback(), 0);
    }

    @Test
    public void rollbackNotNullTest() {
        List<Object> values = new LinkedList<>();
        values.add(0, "Five");
        values.add(1, 5);
        Storeable testStoreable = new MyStoreable(values, myTable);
        myTable.put("k1", testStoreable);
        myTable.commit();
        assertEquals(myTable.rollback(), 1);
    }

    @Test
    public void listTest() {
        List<Object> values1 = new LinkedList<>();
        values1.add(0, "Five");
        values1.add(1, 5);
        Storeable testStoreable1 = new MyStoreable(values1, myTable);
        List<Object> values2 = new LinkedList<>();
        values2.add(0, "Four");
        values2.add(1, 4);
        Storeable testStoreable2 = new MyStoreable(values2, myTable);
        myTable.put("k1", testStoreable1);
        myTable.put("k2", testStoreable2);
        myTable.commit();
        List<String> keysList = myTable.list();
        assertEquals(String.join(", ", keysList), "k1, k2");
    }

    @Test
    public void getNameTest() {
        assertEquals(myTable.getName(), testDir.resolve("DbTest").resolve("t1").toString());
    }

    @Test
    public void getColumnsCount() {
        assertEquals(2, myTable.getColumnsCount());
    }

    @Test
    public void getNumberOfUncommittedChangesTest() {
        List<Object> values1 = new LinkedList<>();
        values1.add(0, "Five");
        values1.add(1, 5);
        Storeable testStoreable1 = new MyStoreable(values1, myTable);
        List<Object> values2 = new LinkedList<>();
        values2.add(0, "Four");
        values2.add(1, 4);
        Storeable testStoreable2 = new MyStoreable(values2, myTable);
        myTable.put("k1", testStoreable1);
        myTable.put("k2", testStoreable2);
        assertEquals(2, myTable.getNumberOfUncommittedChanges());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnTypeExceptionTest() {
        myTable.getColumnType(3);
    }

    @Test
    public void getColumnTypeTest() {
        assertEquals(myTable.getColumnType(0), String.class);
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
