package ru.fizteh.fivt.students.theronsg.junit.tests;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import ru.fizteh.fivt.students.theronsg.junit.MyTableProviderFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MyTableProviderFactoryTest {
    private MyTableProviderFactory myTableProviderFactory;
    Path testDir = Paths.get(System.getProperty("java.io.tmpdir"));

    @Test(expected = IllegalArgumentException.class)
    public void createNullMyTableProvider() {
        myTableProviderFactory = new MyTableProviderFactory();
        myTableProviderFactory.create(null);
    }
    @Test
    public void createNotNullMyTableProvider() {
        myTableProviderFactory = new MyTableProviderFactory();
        myTableProviderFactory.create("DbTest");
        assertTrue(testDir.resolve("DbTest").toFile().exists());
    }
    @After
    public void deleteTestDir() {
        testDir.resolve("DbTest").toFile().delete();
    }
}
