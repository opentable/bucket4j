package com.github.bucket4j.jdbc;

import java.sql.SQLException;

public interface JdbcAdapter<K, T> {

    void createInitialState(K primaryKey, long[] state) throws SQLException;

    T beginTransaction() throws SQLException;

    long[] selectForUpdate(K primaryKey, T transactionContext) throws SQLException;

    void update(K primaryKey, long[] state) throws SQLException;

    void endTransaction(T transactionContext, boolean commit) throws SQLException;


}
