package ru.fizteh.fivt.students.ilin_ilia.junit;

import javafx.util.Pair;
import ru.fizteh.fivt.storage.strings.Table;

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
     * changingFiles contains keys and values which will add during the programme.
     *
     * At the beginning of the programme all of them contain the same keys and values.
     *
     */
    private HashMap<Pair<Integer, Integer>, FileMap> inputFiles;
    private HashMap<Pair<Integer, Integer>, FileMap> changingFiles;
    public static final String CODING_TYPE = "UTF-8";
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
                        Pair<Integer, Integer> pair = new Pair<>(Integer.parseInt(dir), Integer.parseInt(fileName));
                        inputFiles.put(pair, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
                                this.name.resolve(dir).toString()));
                        changingFiles.put(pair, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
                                this.name.resolve(dir).toString()));
                    }
                }
            }
        } else {
            try {
                curTable.mkdir();
            } catch (SecurityException e) {
                System.err.println("Can't create the following directory: \"" + curTable.getName() + "\"");
            }
        }
    }

    public Pair<Integer, Integer> convertIntoHashRule(final String key) {
        byte byt = 0;
        try {
            byt = key.getBytes(CODING_TYPE)[0];
        } catch (UnsupportedEncodingException e) {
            System.out.println("Can't decode key to UTF-8");
            System.exit(-1);
        }
        int ndir = byt % MAX_DIRECTORIES_AMOUNT;
        int nfile = (byt / MAX_FILES_AMOUNT) % MAX_FILES_AMOUNT;
        return new Pair<>(ndir, nfile);
    }

    @Override
    public String getName() {
        return name.toString();
    }

    @Override
    public String get(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Can't get value. Empty key is impossible.");
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
        Pair<Integer, Integer> pair = convertIntoHashRule(key);
        if (changingFiles.containsKey(pair)) {
            if (changingFiles.get(pair).containsKey(key)) {
                if (!changingFiles.get(pair).get(key).equals(value)) {
                    if (!commitKeys.contains(key)) {
                        commitKeys.add(key);
                    }
                }
            } else {
                if (!commitKeys.contains(key)) {
                    commitKeys.add(key);
                }
            }
            return changingFiles.get(pair).put(key, value);
        } else {
            if (!commitKeys.contains(key)) {
                commitKeys.add(key);
            }
            changingFiles.put(pair,
                    new FileMap(name.resolve(pair.getKey().toString()).resolve(pair.getValue().toString()).toString(),
                            name.resolve(pair.getKey().toString()).toString()));
            return changingFiles.get(pair).put(key, value);
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
        Pair<Integer, Integer> pair = convertIntoHashRule(key);
        if (changingFiles.containsKey(pair)) {
            return changingFiles.get(convertIntoHashRule(key)).remove(key);
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        int summ = 0;
        for (FileMap fileMap : changingFiles.values()) {
            summ += fileMap.size();
        }
        return summ;
    }

    @Override
    public int commit() {
        inputFiles.clear();
        for (Pair<Integer, Integer> key : changingFiles.keySet()) {
            inputFiles.put(key,changingFiles.get(key));
        }
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
        for (Pair<Integer, Integer> key : inputFiles.keySet()) {
            changingFiles.put(key, inputFiles.get(key));
        }
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
                fileMap.putFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(commitKeys.size() != 0 && isInvitated) {
            System.err.println(commitKeys.size() + " unsaved changes");
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
