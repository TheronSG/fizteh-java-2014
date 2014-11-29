package ru.fizteh.fivt.students.theronsg.junit;

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
    private int changesBeforeCommit;
    private int changesAfterCommit;
    /**
     * inputFiles contains files which have read from db.
     * changingFiles contains keys and values which will add during the programme.
     * commitFiles contains keys and values after commit command.
     *
     * At the beginning of the programme all of them contain the same keys and values.
     *
     *  isCommits shows us commit has been or no.
     */
    private HashMap<Pair<Integer, Integer>, FileMap> inputFiles;
    private HashMap<Pair<Integer, Integer>, FileMap> changingFiles;
    private HashMap<Pair<Integer, Integer>, FileMap> commitFiles;
    public static final String CODING_TYPE = "UTF-8";
    public static final int MAX_DIRECTORIES_AMOUNT  = 16;
    public static final int MAX_FILES_AMOUNT  = 16;
    private boolean isCommits;



    MyTable(final String name) {
        isCommits = false;
        this.name = Paths.get(name);
        inputFiles = new HashMap<Pair<Integer, Integer>, FileMap>();
        changingFiles = new HashMap<Pair<Integer, Integer>, FileMap>();
        commitFiles = new HashMap<Pair<Integer, Integer>, FileMap>();
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
                        commitFiles.put(pair, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
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
            byt = key.getBytes(CODING_TYPE  )[0];
        } catch (UnsupportedEncodingException e) {
            System.out.println("Can't decode key to UTF-8");
            System.exit(-1);
        }
        int ndir = byt % MAX_DIRECTORIES_AMOUNT;
        int nfile = (byt / MAX_FILES_AMOUNT) % MAX_FILES_AMOUNT;
        return new Pair<Integer, Integer>(ndir, nfile);
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
        if (commitFiles.containsKey(convertIntoHashRule(key))) {
            String fileKey  = commitFiles.get(convertIntoHashRule(key)).get(key);
            return fileKey;
        } else {
            System.out.println("not found");
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
                    changesBeforeCommit++;
                }
            } else {
                changesBeforeCommit++;
            }
            return changingFiles.get(pair).put(key, value);
        } else {
            changesBeforeCommit++;
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
        Pair<Integer, Integer> pair = convertIntoHashRule(key);
        if (commitFiles.containsKey(pair)) {
            String fileKey  = commitFiles.get(convertIntoHashRule(key)).remove(key);
            return fileKey;
        } else {
            System.out.println("not found");
            return null;
        }
    }

    @Override
    public int size() {
        int summ = 0;
        for (FileMap fileMap : commitFiles.values()) {
            summ += fileMap.size();
        }
        return summ;
    }

    @Override
    public int commit() {
        if (!isCommits) {
            isCommits = true;
            commitFiles = changingFiles;
        } else {
            inputFiles = commitFiles;
            commitFiles = changingFiles;
        }
        changesAfterCommit = changesBeforeCommit;
        changesBeforeCommit  = 0;
        return changesAfterCommit;
    }

    @Override
    public int rollback() {
        int returnValue = changesAfterCommit;
        changesAfterCommit = 0;
        changesBeforeCommit = 0;
        changingFiles = inputFiles;
        commitFiles = inputFiles;
        isCommits = false;
        return returnValue;
    }

    @Override
    public List<String> list() {
        List<String> keys = new LinkedList<>();
        for (FileMap fileMap : commitFiles.values()) {
            for (String listKey: fileMap.list()) {
                keys.add(listKey);
            }
        }
        System.out.println(String.join(", ", keys));
        return keys;
    }

    public int getChangesBeforeCommit() {
        return changesBeforeCommit;
    }

    public void saveDB() {
        for (FileMap fileMap : commitFiles.values()) {
            try {
                fileMap.putFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
