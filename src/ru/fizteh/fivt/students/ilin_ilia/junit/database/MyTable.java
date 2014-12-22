package ru.fizteh.fivt.students.ilin_ilia.junit.database;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.TableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyTable implements Table {
    private Path name;
    private int changesAfterCommit;
    private List<String> commitKeys;
    /**
     * inputFiles contains files which have read from db and won't change after commit.
     * changingFiles contains keys and values which will add during the program.
     *
     * At the beginning of the program all of them contain the same keys and values.
     *
     */
    private HashMap<DirFile, FileMap> inputFiles;
    private HashMap<DirFile, FileMap> changingFiles;
    public static final String ENCODING = "UTF-8";
    public static final int MAX_DIRECTORIES_AMOUNT  = 16;
    public static final int MAX_FILES_AMOUNT  = 16;
    /**
     * If there are some uncommitted changes and we stop the programme
     * this flag shows if we have printed the message about uncommitted
     * changes.
     * It's false if we printed it or there is no uncommitted changes.
     * isInvitated true in current table and only in it.
     */
    private boolean isInvitated;



    MyTable(final String name) {
        isInvitated = false;
        commitKeys = new LinkedList<>();
        this.name = Paths.get(name);
        inputFiles = new HashMap<>();
        changingFiles = new HashMap<>();
        File curTable = new File(name);
        if (curTable.exists()) {
            if (curTable.list() != null) {
                for (String dir: curTable.list()) {
                    File directory = new File(this.name.resolve(dir).toString());
                    for (String file : directory.list()) {
                        String fileName = file.substring(0, file.length() - 4);
                        DirFile dirFile = new DirFile(Integer.parseInt(dir), Integer.parseInt(fileName));
                        inputFiles.put(dirFile, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
                                this.name.resolve(dir).toString()));
                        changingFiles.put(dirFile, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
                                this.name.resolve(dir).toString()));
                    }
                }
            }
        } else {
            try {
                curTable.mkdir();
            } catch (SecurityException e) {
                throw new SecurityException("Can't create the following directory: \"" + curTable.getName() + "\"");
            }
        }
    }

    public DirFile convertIntoHashRule(final String key) {
        byte byt = 0;
        try {
            byt = key.getBytes(ENCODING)[0];
        } catch (UnsupportedEncodingException e) {
            throw new TableException("Can't decode key according to ENCODING.");
        }
        int ndir = byt % MAX_DIRECTORIES_AMOUNT;
        int nfile = (byt / MAX_FILES_AMOUNT) % MAX_FILES_AMOUNT;
        return new DirFile(ndir, nfile);
    }

    @Override
    public String getName() {
        return name.toString();
    }

    @Override
    public String get(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Can't get value. Empty key is not acceptable.");
        }
        if (changingFiles.containsKey(convertIntoHashRule(key))) {
            return changingFiles.get(convertIntoHashRule(key)).get(key);
        } else {
            return null;
        }
    }

    @Override
    public String put(final String key, final String value) {
        if (key == null) {
            throw new IllegalArgumentException("Can't put value for empty key.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Can't put empty value.");
        }
        DirFile dirFile = convertIntoHashRule(key);
        if (changingFiles.containsKey(dirFile)) {
            if (changingFiles.get(dirFile).containsKey(key)) {
                if (!changingFiles.get(dirFile).get(key).equals(value)) {
                    if (!commitKeys.contains(key)) {
                        commitKeys.add(key);
                    }
                }
            } else {
                if (!commitKeys.contains(key)) {
                    commitKeys.add(key);
                }
            }
            return changingFiles.get(dirFile).put(key, value);
        } else {
            if (!commitKeys.contains(key)) {
                commitKeys.add(key);
            }
            changingFiles.put(dirFile,
                    new FileMap(name.resolve(dirFile.getDir().toString()).
                            resolve(dirFile.getFile().toString()).toString(),
                            name.resolve(dirFile.getDir().toString()).toString()));
            return changingFiles.get(dirFile).put(key, value);
        }
    }

    @Override
    public String remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Can't remove value for empty key.");
        }
        if (!commitKeys.contains(key)) {
            commitKeys.remove(key);
        }
        DirFile dirFile = convertIntoHashRule(key);
        if (changingFiles.containsKey(dirFile)) {
            return changingFiles.get(convertIntoHashRule(key)).remove(key);
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        int sum = 0;
        for (FileMap fileMap : changingFiles.values()) {
            sum += fileMap.size();
        }
        return sum;
    }

    @Override
    public int commit() {
        inputFiles.clear();
        inputFiles = (HashMap<DirFile, FileMap>) changingFiles.clone();
        changesAfterCommit = commitKeys.size();
        commitKeys.clear();
        return changesAfterCommit;
    }

    @Override
    public int rollback() {
        int returnValue = changesAfterCommit;
        changesAfterCommit = 0;
        commitKeys.clear();
        changingFiles.clear();
        changingFiles = (HashMap<DirFile, FileMap>) inputFiles.clone();
        return returnValue;
    }

    @Override
    public List<String> list() {
        List<String> keys = new LinkedList<>();
        for (FileMap fileMap : changingFiles.values()) {
            for (String listKey: fileMap.list()) {
                keys.add(listKey);
            }
        }
        return keys;
    }


    public void saveDB() {
        for (FileMap fileMap : inputFiles.values()) {
            try {
                fileMap.dumpDataToFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (commitKeys.size() != 0 && isInvitated) {
            throw new TableException(commitKeys.size() + " unsaved changes");
        }
    }

    public int getChangesBeforeCommit() {
        isInvitated = false;
        return commitKeys.size();
    }

    public void setIsInvitatedTrue() {
        isInvitated = true;
    }
}
