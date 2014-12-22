package ru.fizteh.fivt.students.ilin_ilia.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.MyTable;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.MyTableProvider;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MyTableTest {
    private MyTableProvider myTableProvider;
    private Path testDir = Paths.get(System.getProperty("java.io.tmpdir"));
    private MyTable myTable;

    @Before
    public void beforeTest() {
        myTableProvider = new MyTableProvider(testDir.toString() + "\\DbTest");
        myTable = (MyTable) myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString());
    }

    @Test
    public void removeExistedKeyTest() {
        myTable.put("k1", "1");
        myTable.commit();
        assertEquals(myTable.remove("k1"), "1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKeyTest() {
        myTable.remove(null);
    }

    @Test
    public void removeNotExistedKeyTest() {
        myTable.put("k1", "1");
        myTable.rollback();
        assertNull(myTable.remove("k1"));
    }

    @Test
    public void getExistedKeyTest() {
        myTable.put("k1", "1");
        myTable.commit();
        assertEquals(myTable.get("k1"), "1");
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
        assertNull(myTable.put("k1", "1"));
    }

    @Test
    public void putNormalKeyWithOverwritingTest() {
        myTable.put("k1", "1");
        assertEquals(myTable.put("k1", "2"), "1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKeyTest() {
        myTable.put(null, "1");
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
        myTable.put("k1", "1");
        myTable.rollback();
        assertEquals(myTable.size(), 0);
    }

    @Test
    public void sizeNotNullTest() {
        myTable.put("k1", "1");
        myTable.commit();
        assertEquals(myTable.size(), 1);
    }

    @Test
    public void commitNullTest() {
        assertEquals(myTable.commit(), 0);
    }

    @Test
    public void commitNotNullTest() {
        myTable.put("k1", "1");
        assertEquals(myTable.commit(), 1);
    }

    @Test
    public void rollbackNullTest() {
        assertEquals(myTable.rollback(), 0);
    }

    @Test
    public void rollbackNotNullTest() {
        myTable.put("k1", "1");
        myTable.commit();
        assertEquals(myTable.rollback(), 1);
    }

    @Test
    public void listTest() {
        myTable.put("k1", "1");
        myTable.put("k2", "2");
        myTable.commit();
        List<String> keysList = myTable.list();
        assertEquals(String.join(", ", keysList), "k1, k2");
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
