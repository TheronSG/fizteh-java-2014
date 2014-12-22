package ru.fizteh.fivt.students.ilin_ilia.junit.database;


import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.FileMapIOException;

import java.io.*;
import java.util.*;

public class FileMap {
    private Map<String, String> map;
    private String name;
    private File dbFile;
    private File dbRootDir;
    public static final String ENCODING = "UTF-8";

    FileMap(final String pathToFile, final String pathToContainingDir) {
        map = new HashMap<>();
        name = pathToFile + ".dat";
        dbFile = new File(name);
        dbRootDir = new File(pathToContainingDir);
        if (dbFile.exists()) {
            try {
                loadDataFromFile();
            } catch (IOException e) {
                throw new FileMapIOException(e.getMessage());
            }
        }
    }

    public String put(final String key, final String value) {
        return map.put(key, value);
    }

    public String get(final String key) {
        return map.get(key);
    }

    public String remove(final String key) {
        return map.remove(key);
    }

    public Set<String> list() {
        return map.keySet();
    }

    public void exit() throws IOException {
        try {
            dbFile.createNewFile();
        } catch (SecurityException e) {
            throw new FileMapIOException("Can't create the following file: \"" + dbFile.getName() + "\"");
        }
        dumpDataToFile();
    }

    public void delete() {
        new File(name).delete();
    }

    public void loadDataFromFile() throws IOException {
        RandomAccessFile file = new RandomAccessFile(name, "rw");
        if (file.length() == 0) {
            file.close();
            return;
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        List<Integer> offsets = new LinkedList<>();
        List<String> keys = new LinkedList<>();
        byte b;
        int bytesCounter = 0;
        int off = -1;
        do {
            while ((b = file.readByte()) != 0) {
                bytesCounter++;
                buf.write(b);
            }
            bytesCounter++;
            if (off == -1) {
                off = file.readInt();
            } else {
                offsets.add(file.readInt());
            }
            bytesCounter += 4;
            keys.add((buf.toString(ENCODING)));
            buf.reset();
        } while (bytesCounter < off);
        try {
            offsets.add((int) file.length());
            Iterator<String> keyIter = keys.iterator();
            for (int nextOffset : offsets) {
                while (bytesCounter < nextOffset) {
                    buf.write(file.read());
                    bytesCounter++;
                }
                if (buf.size() > 0) {
                    map.put(keyIter.next(), buf.toString(ENCODING));
                    buf.reset();
                } else {
                    file.close();
                    throw new FileMapIOException("Buffer is empty. Incorrect reading of file.");
                }
            }
        } catch (IOException e) {
            file.close();
            throw new FileMapIOException("Can't read db file. Reason:" + e.getMessage());
        }
        try {
            buf.close();
            file.close();
        } catch (IOException e) {
            throw new FileMapIOException("Can't close db file");
        }
    }

    public void dumpDataToFile() throws FileNotFoundException {
        try {
            dbRootDir.mkdir();
            dbFile.createNewFile();
        } catch (IOException e) {
            throw new FileMapIOException(e.getMessage());
        }
        RandomAccessFile file = new RandomAccessFile(name, "rw");
        try {
            if (map.size() == 0) {
                file.close();
                new File(name).delete();
                return;
            }
            file.setLength(0);
            Set<String> keys = map.keySet();
            List<Integer> offsetsPos = new LinkedList<>();
            for (String cur : keys) {
                file.write(cur.getBytes(ENCODING));
                file.write('\0');
                offsetsPos.add(((int) file.getFilePointer()));
                file.writeInt(0);
            }
            List<Integer> offsets = new LinkedList<>();
            for (String cur : keys) {
                offsets.add((int) file.getFilePointer());
                file.write(map.get(cur).getBytes(ENCODING));
            }
            Iterator<Integer> offIter = offsets.iterator();
            for (int pos :offsetsPos) {
                file.seek(pos);
                file.writeInt(offIter.next());
            }
            file.close();
        } catch (IOException e) {
            throw new FileMapIOException("Can't write into a db file");
        }
    }

    public boolean exists() {
        return new File(name).exists();
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }
}
