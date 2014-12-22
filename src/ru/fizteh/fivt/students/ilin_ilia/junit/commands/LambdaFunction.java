package ru.fizteh.fivt.students.ilin_ilia.junit.commands;


import ru.fizteh.fivt.students.ilin_ilia.junit.dbexceptions.StopInterpretationException;

@FunctionalInterface
public interface LambdaFunction<T, R> {
    void accept(T t, R r) throws StopInterpretationException;
}
