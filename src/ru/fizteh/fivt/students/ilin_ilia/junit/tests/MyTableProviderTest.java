package ru.fizteh.fivt.students.ilin_ilia.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.ilin_ilia.junit.database.MyTableProvider;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MyTableProviderTest {
    private MyTableProvider myTableProvider;
    private Path testDir = Paths.get(System.getProperty("java.io.tmpdir"));

    @Before
    public void beforeTest() {
        myTableProvider = new MyTableProvider(testDir.toString() + "\\DbTest");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableTest() {
        myTableProvider.createTable(null);
    }

    @Test
    public void createNotNullTableTest() {
        myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString());
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
    public void getExistsTableTest() {
        myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString());
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
    public void removeExistsTableTest() {
        myTableProvider.createTable(testDir.resolve("DbTest").resolve("t1").toString());
        myTableProvider.removeTable(testDir.resolve("DbTest").resolve("t1").toString());
        assertTrue(!testDir.resolve("DbTest").resolve("t1").toFile().exists());
    }

    @After
    public void deleteTestDir() {

        for (String dir : new File(testDir.resolve("DbTest").toString()).list()) {
            testDir.resolve("DbTest").resolve(dir).toFile().delete();
        }
        testDir.resolve("DbTest").toFile().delete();
    }

}
