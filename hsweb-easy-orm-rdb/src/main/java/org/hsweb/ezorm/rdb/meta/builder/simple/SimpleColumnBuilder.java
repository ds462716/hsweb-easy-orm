package org.hsweb.ezorm.rdb.meta.builder.simple;

import org.hsweb.ezorm.rdb.meta.RDBColumnMetaData;
import org.hsweb.ezorm.rdb.meta.RDBTableMetaData;
import org.hsweb.ezorm.rdb.meta.builder.ColumnBuilder;
import org.hsweb.ezorm.rdb.meta.builder.TableBuilder;

import java.sql.JDBCType;

/**
 * @author zhouhao
 * @TODO
 */
public class SimpleColumnBuilder implements ColumnBuilder {
    RDBColumnMetaData columnMetaData;
    TableBuilder      tableBuilder;
    RDBTableMetaData  tableMetaData;

    public SimpleColumnBuilder(RDBColumnMetaData columnMetaData, TableBuilder tableBuilder, RDBTableMetaData tableMetaData) {
        this.columnMetaData = columnMetaData;
        this.tableBuilder = tableBuilder;
        this.tableMetaData = tableMetaData;
    }

    @Override
    public ColumnBuilder name(String name) {
        columnMetaData.setName(name);
        return this;
    }
    @Override
    public ColumnBuilder alias(String name) {
        columnMetaData.setAlias(name);
        return this;
    }

    @Override
    public ColumnBuilder dataType(String dataType) {
        columnMetaData.setDataType(dataType);
        return this;
    }

    @Override
    public ColumnBuilder jdbcType(JDBCType jdbcType) {
        columnMetaData.setJdbcType(jdbcType);
        return this;
    }

    @Override
    public ColumnBuilder javaType(Class javaType) {
        columnMetaData.setJavaType(javaType);
        return this;
    }

    @Override
    public ColumnBuilder comment(String comment) {
        columnMetaData.setComment(comment);
        return this;
    }

    @Override
    public ColumnBuilder notNull() {
        columnMetaData.setNotNull(true);
        return this;
    }

    @Override
    public ColumnBuilder primaryKey() {
        columnMetaData.setPrimaryKey(true);
        return this;
    }

    @Override
    public ColumnBuilder length(int len) {
        columnMetaData.setLength(len);
        return this;
    }

    @Override
    public ColumnBuilder length(int precision, int scale) {
        columnMetaData.setLength(precision);
        columnMetaData.setScale(scale);
        columnMetaData.setPrecision(precision);
        return this;
    }

    @Override
    public ColumnBuilder property(String propertyName, Object value) {
        columnMetaData.setProperty(propertyName, value);
        return this;
    }

    @Override
    public TableBuilder commit() {
        if (columnMetaData.getDataType() == null) {
            String dataType = tableMetaData.getDatabaseMetaData().getDialect().buildDataType(columnMetaData);
            columnMetaData.setDataType(dataType);
        }
        tableMetaData.addColumn(columnMetaData);
        return tableBuilder;
    }
}
