package ru.fizteh.fivt.students.ilin_ilia.storeable.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ilin_ilia.storeable.utils.Utils;
import ru.fizteh.fivt.students.ilin_ilia.storeable.dbexceptions.TableException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
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
     * isExists shows us file has been created before the program or not.
     */
    private HashMap<DirFile, FileMap> inputFiles;
    private HashMap<DirFile, FileMap> changingFiles;
    public static final String ENCODING = "UTF-8";
    public static final int MAX_DIRECTORIES_AMOUNT  = 16;
    public static final int MAX_FILES_AMOUNT  = 16;
    private Class[] columnTypes;
    private boolean isExists;
    private TableProvider containsTableProvider;
    /**
     * If there are some uncommitted changes and we stop the program
     * this flag shows if we have printed the message about uncommitted
     * changes.
     * It's false if we printed it or there is no uncommitted changes.
     * isInvitated can be true only in current table.
     */
    private boolean isInvitated;

    MyTable(final String name, List<Class<?>> columnTypes, TableProvider tableProvider)
            throws IOException, ClassNotFoundException, ParseException {
        containsTableProvider = tableProvider;
        if (columnTypes != null) {
            this.columnTypes = new Class[columnTypes.size()];
            for (int i = 0; i < columnTypes.size(); i++) {
                this.columnTypes[i] = columnTypes.get(i);
            }
        }
        commitKeys = new LinkedList<>();
        this.name = Paths.get(name);
        inputFiles = new HashMap<>();
        changingFiles = new HashMap<>();
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
                            DirFile dirFile = new DirFile(Integer.parseInt(dir), Integer.parseInt(fileName));
                            inputFiles.put(dirFile, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
                                    this.name.resolve(dir).toString(), this, containsTableProvider));
                            changingFiles.put(dirFile, new FileMap(this.name.resolve(dir).resolve(fileName).toString(),
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
    public Storeable get(final String key) {
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
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
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
            try {
                changingFiles.put(dirFile,
                        new FileMap(name.resolve(dirFile.getDir().toString()).
                                resolve(dirFile.getFile().toString()).toString(),
                                name.resolve(dirFile.getDir().toString()).toString(), this, containsTableProvider));
            } catch (ParseException e) {
                throw new ColumnFormatException(e.getMessage());
            }
            return changingFiles.get(dirFile).put(key, value);
        }
    }

    @Override
    public Storeable remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Can't remove value for empty key.");
        }
        if (!commitKeys.contains(key)) {
            commitKeys.remove(key);
        }
        DirFile dirFile =  convertIntoHashRule(key);
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
    public int getNumberOfUncommittedChanges() {
        return commitKeys.size();
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
        for (FileMap fileMap : changingFiles.values()) {
            for (String listKey: fileMap.list()) {
                keys.add(listKey);
            }
        }
        return keys;
    }

    public void saveDB() throws IOException {
        if (!isExists) {
            try {
                File signature = name.resolve("signature.tsv").toFile();
                signature.createNewFile();
                RandomAccessFile fileSignature = new RandomAccessFile(signature, "rw");
                int count = 0;
                for (Class<?> clas : columnTypes) {
                    fileSignature.write((clas.toString()).getBytes(ENCODING));
                    count++;
                    if (count != columnTypes.length) {
                        fileSignature.write(" ".getBytes(ENCODING));
                    }
                }
                fileSignature.close();
            } catch (IOException e) {
                throw new IOException("Can't create \"signature.tsv\" file for table \"" + name.toString() + "\".");
            }
        }
        for (FileMap fileMap : inputFiles.values()) {
                fileMap.dumpDataToFile();
        }
        if (commitKeys.size() != 0 && isInvitated) {
            throw new TableException(commitKeys.size() + " unsaved changes");
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
        fileSignature.close();
    }

    public void setIsInvitatedTrue() {
        isInvitated = true;
    }
}
