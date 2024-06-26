/*
 * Copyright 2023 CeresDB Project Authors. Licensed under Apache-2.0.
 */
package org.apache.horaedb.sql;

import java.util.List;

import org.apache.horaedb.util.Utils;
import org.apache.horaedb.common.parser.SqlParser;
import org.apache.horaedb.common.util.internal.ThrowUtil;

/**
 * Parse SQL.
 *
 */
public class CeresDBParser implements SqlParser {

    private final String sql;

    private boolean             parsed;
    private StatementType       statementType = StatementType.Unknown;
    private CeresDBSqlStatement stmt;

    public CeresDBParser(String sql) {
        this.sql = sql;
    }

    @Override
    public StatementType statementType() {
        parse();

        return this.statementType;
    }

    @Override
    public List<String> tableNames() {
        parse();

        return this.stmt.getTables();
    }

    @Override
    public List<Column> createColumns() {
        parse();

        return Utils.unsupported("`%s` unsupported yet!", "createColumns");
    }

    private void parse() {
        if (this.parsed) {
            return;
        }

        this.parsed = true;

        try {
            final CeresDBSqlStatement stmt = CeresDBSqlParser.parse(this.sql)[0];

            switch (stmt.getStatementType()) {
                case SELECT:
                    this.statementType = StatementType.Select;
                    break;
                case CREATE:
                    this.statementType = StatementType.Create;
                    break;
                case ALTER:
                case ALTER_DELETE:
                case ALTER_UPDATE:
                    this.statementType = StatementType.Alter;
                    break;
                case DESCRIBE:
                    this.statementType = StatementType.Describe;
                    break;
                case SHOW:
                    this.statementType = StatementType.Show;
                    break;
                case DROP:
                    this.statementType = StatementType.Drop;
                    break;
                case INSERT:
                    this.statementType = StatementType.Insert;
                    break;
                case EXISTS:
                    this.statementType = StatementType.Exists;
                    break;
                default:
                    this.statementType = StatementType.Unknown;
            }

            this.stmt = stmt;
        } catch (final Exception e) {
            ThrowUtil.throwException(e);
        }
    }
}
