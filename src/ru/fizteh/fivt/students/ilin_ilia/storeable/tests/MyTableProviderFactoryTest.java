package ru.fizteh.fivt.students.ilin_ilia.storeable.tests;

import org.junit.After;
import org.junit.Test;
import ru.fizteh.fivt.students.ilin_ilia.storeable.MyTableProviderFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class MyTableProviderFactoryTest {
    private MyTableProviderFactory myTableProviderFactory;
    Path testDir = Paths.get(System.getProperty("java.io.tmpdir"));

    @Test(expected = IllegalArgumentException.class)
    public void createNullMyTableProvider() {
        myTableProviderFactory = new MyTableProviderFactory();
        try {
            myTableProviderFactory.create(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void createNotNullMyTableProvider() {
        myTableProviderFactory = new MyTableProviderFactory();
        try {
            myTableProviderFactory.create("DbTest");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(testDir.resolve("DbTest").toFile().exists());
    }
    @After
    public void deleteTestDir() {
        testDir.resolve("DbTest").toFile().delete();
    }
}
