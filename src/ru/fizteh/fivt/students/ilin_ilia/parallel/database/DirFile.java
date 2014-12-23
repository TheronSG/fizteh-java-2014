package ru.fizteh.fivt.students.ilin_ilia.parallel.database;

import java.util.Objects;

public class DirFile {
    private Integer dir;
    private Integer file;

    public DirFile(Integer dir, Integer file) {
        this.dir = dir;
        this.file = file;
    }

    public void setDirAndFile(Integer dir, Integer file) {
        this.dir = dir;
        this.file = file;
    }



    @Override
    public boolean equals(Object object) {
        return object instanceof DirFile && Objects.equals(((DirFile) object).getDir(), dir)
                && Objects.equals(((DirFile) object).getFile(), file);
    }

    @Override
    public int hashCode() {
        return dir * 1000 + file;
    }

    public Integer getDir() {
        return dir;
    }

    public Integer getFile() {
        return file;
    }
}
