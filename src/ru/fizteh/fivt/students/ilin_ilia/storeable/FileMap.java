package ru.fizteh.fivt.students.ilin_ilia.storeable;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class FileMap {
    private Map<String, Storeable> map;
    Map<String, String> mapString;
    private String name;
    private File fil;
    private File containsDir;
    private Table containsTable;
    private TableProvider containsTableProvider;
    public static final String CODING_TYPE = "UTF-8";

    FileMap(final String pathToFile, final String pathToContainsDir, Table table, TableProvider tableProvider) throws ParseException {
        containsTable = table;
        containsTableProvider = tableProvider;
        map = new HashMap<>();
        mapString = new HashMap<>();
        fil = new File(pathToFile + ".dat");
        containsDir = new File(pathToContainsDir);
        name = pathToFile + ".dat";
        if (fil.exists()) {
            try {
                getFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Storeable put(final String key, final Storeable value) {
        return map.put(key, value);
    }

    public Storeable get(final String key) {
        return map.get(key);
    }

    public Storeable remove(final String key) {
        return map.remove(key);
    }

    public Set<String> list() {
        return map.keySet();
    }

    public void exit() throws IOException {
        try {
            fil.createNewFile();
        } catch (SecurityException e) {
            System.err.println("Can't create the following file: \"" + fil.getName() + "\"");
        }
        putFile();
    }

    public void delete() {
        new File(name).delete();
    }

    public void getFile() throws IOException, ParseException {
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
            keys.add((buf.toString(CODING_TYPE)));
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
                    mapString.put(keyIter.next(), buf.toString(CODING_TYPE));
                    buf.reset();
                } else {
                    file.close();
                    throw new RuntimeException("Buffer is empty. Incorrect reading of file.");
                }
            }
        } catch (IOException e) {
            System.err.println("Can't read db file");
            e.printStackTrace();
            file.close();
            System.exit(-1);
        } catch (RuntimeException e) {
            System.err.println("Wrong input file");
            e.printStackTrace();
            System.exit(-1);
        }
        try {
            buf.close();
            file.close();
        } catch (IOException e) {
            System.err.println("Can't close db file");
            e.printStackTrace();
            System.exit(-1);
        }
        for (String key : mapString.keySet()) {
            map.put(key, containsTableProvider.deserialize(containsTable, mapString.get(key)));
        }
        mapString.clear();
    }

    public void putFile() throws FileNotFoundException {
        try {
            containsDir.mkdir();
            fil.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RandomAccessFile file = new RandomAccessFile(name, "rw");
        for (String key : map.keySet()) {
            mapString.put(key, containsTableProvider.serialize(containsTable, map.get(key)));
        }
        try {
            if (mapString.size() == 0) {
                file.close();
                new File(name).delete();
                new File(containsDir.toString()).delete();
                return;
            }
            file.setLength(0);
            Set<String> keys = mapString.keySet();
            List<Integer> offsetsPos = new LinkedList<>();
            for (String cur : keys) {
                file.write(cur.getBytes(CODING_TYPE));
                file.write('\0');
                offsetsPos.add(((int) file.getFilePointer()));
                file.writeInt(0);
            }
            List<Integer> offsets = new LinkedList<>();
            for (String cur : keys) {
                offsets.add((int) file.getFilePointer());
                file.write(mapString.get(cur).getBytes(CODING_TYPE));
            }
            Iterator<Integer> offIter = offsets.iterator();
            for (int pos :offsetsPos) {
                file.seek(pos);
                file.writeInt(offIter.next());
            }
            file.close();
        } catch (IOException e) {
            System.err.println("Can't write into a db file");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public boolean exists() {
        return new File(name).exists();
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public Table getContainsTable() {
        return  containsTable;
    }

    public int size() {
        return map.size();
    }
}
