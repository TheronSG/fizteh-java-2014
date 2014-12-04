package ru.fizteh.fivt.students.theronsg.storeable;

import javafx.util.Pair;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
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
     * isCommits shows us commit has been or no.
     *
     * isExists shows us file has been created before the programme or not.
     */
    private HashMap<Pair<Integer, Integer>, FileMap> inputFiles;
    private HashMap<Pair<Integer, Integer>, FileMap> changingFiles;
    private HashMap<Pair<Integer, Integer>, FileMap> commitFiles;
    public static final String CODING_TYPE = "UTF-8";
    public static final int MAX_DIRECTORIES_AMOUNT  = 16;
    public static final int MAX_FILES_AMOUNT  = 16;
    private boolean isCommits;
    private Class[] columnTypes;
    private boolean isExists;
    private TableProvider containsTableProvider;

    MyTable(final String name, List<Class<?>> columnTypes, TableProvider tableProvider)
            throws IOException, ClassNotFoundException, ParseException {
        containsTableProvider = tableProvider;
        if (columnTypes != null) {
            this.columnTypes = new Class[columnTypes.size()];
            for (int i = 0; i < columnTypes.size(); i++) {
                this.columnTypes[i] = columnTypes.get(i);
            }
        }
        isCommits = false;
        this.name = Paths.get(name);
        inputFiles = new HashMap<>();
        changingFiles = new HashMap<>();
        commitFiles = new HashMap<>();
        File curTable = new File(name);
        if (curTable.exists()) {
            isExists = true;
            readDB();
            if (curTable.list().length > 1) {
                for (String dir: curTable.list()) {
                    File directory = new File(this.name.resolve(dir).toString());
                    if (directory.isDirectory()) {
                        for (String file : directory.list()) {
                            String fileName = file.substring(0, file.length() - 4);
                            Pair<Integer, Integer> pair = new Pair<>(Integer.parseInt(dir), Integer.parseInt(fileName));
                            inputFiles.put(pair, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
                                    this.name.resolve(dir).toString(), this, containsTableProvider));
                            changingFiles.put(pair, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
                                    this.name.resolve(dir).toString(), this, containsTableProvider));
                            commitFiles.put(pair, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
                                    this.name.resolve(dir).toString(), this, containsTableProvider));
                        }
                    }
                }
            }
        } else {
            isExists = false;
            if (columnTypes != null) {
                this.columnTypes = new Class[columnTypes.size()];
                for (int i = 0; i < columnTypes.size(); i++) {
                    this.columnTypes[i] = columnTypes.get(i);
                }
            }
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
    public Storeable get(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Can't get value. Empty key is impossible.");
        }
        if (commitFiles.containsKey(convertIntoHashRule(key))) {
            return commitFiles.get(convertIntoHashRule(key)).get(key);
        } else {
            return null;
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
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
            try {
                changingFiles.put(pair,
                        new FileMap(name.resolve(pair.getKey().toString()).resolve(pair.getValue().toString()).toString(),
                                name.resolve(pair.getKey().toString()).toString(), this, containsTableProvider));
            } catch (ParseException e) {
                throw new ColumnFormatException(e.getMessage());
            }
            return changingFiles.get(pair).put(key, value);
        }
    }

    @Override
    public Storeable remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Can't remove value for empty key.");
        }
        Pair<Integer, Integer> pair = convertIntoHashRule(key);
        if (commitFiles.containsKey(pair)) {
            return commitFiles.get(convertIntoHashRule(key)).remove(key);
        } else {
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
    public int getNumberOfUncommittedChanges() {
        return changesBeforeCommit;
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.length;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex > columnTypes.length) {
            throw new IndexOutOfBoundsException("Wrong index.");
        }
        return columnTypes[columnIndex];
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

    public void saveDB() throws IOException {
        if (!isExists) {
            try {
                File signature = name.resolve("signature.tsv").toFile();
                signature.createNewFile();
                RandomAccessFile fileSignature = new RandomAccessFile(signature, "rw");
                int count = 0;
                for (Class<?> clas : columnTypes) {
                    fileSignature.write((clas.toString()).getBytes(CODING_TYPE));
                    count++;
                    if (count != columnTypes.length) {
                        fileSignature.write(" ".getBytes(CODING_TYPE));
                    }
                }
                fileSignature.close();
            } catch (IOException e) {
                throw new IOException("Can't create \"signature.tsv\" file for table \"" + name.toString() + "\"." );
            }
        }
        for (FileMap fileMap : commitFiles.values()) {
            try {
                fileMap.putFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public void readDB() throws IOException, ClassNotFoundException {
        File signature = name.resolve("signature.tsv").toFile();
        if (!signature.exists()) {
            throw new FileNotFoundException("Error! There is no signature file in table \"" + name.toString() + "\".");
        }
        RandomAccessFile fileSignature = new RandomAccessFile(signature, "rw");
        List<String> types = new LinkedList<>();
        String signatureString = fileSignature.readLine();
        String[] chunks = Utils.findAll("\\S+", signatureString);
        for (int i = 0; i < chunks.length; i++) {
            String chunk = chunks[i];
            i++;
            if (i < chunks.length) {
                types.add(chunks[i]);
            }
        }
        columnTypes = new Class[types.size()];
        for (int i = 0; i < types.size(); i++) {
            try {
                columnTypes[i] = Class.forName(types.get(i));
            } catch (ClassNotFoundException e) {
                throw new ClassNotFoundException("Convert error! Class \"" + types.get(i) + "\" doesn't exist.");
            }
        }
    }
}
