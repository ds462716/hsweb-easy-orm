package org.hsweb.ezorm.rdb.render.support.oracle;

import org.hsweb.commons.StringUtils;
import org.hsweb.ezorm.rdb.executor.BindSQL;
import org.hsweb.ezorm.rdb.meta.RDBColumnMetaData;
import org.hsweb.ezorm.rdb.meta.RDBTableMetaData;
import org.hsweb.ezorm.rdb.executor.SQL;
import org.hsweb.ezorm.rdb.render.SqlAppender;
import org.hsweb.ezorm.rdb.render.SqlRender;
import org.hsweb.ezorm.rdb.render.support.simple.SimpleSQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OracleMetaCreateRender implements SqlRender<Object> {
    @Override
    public SQL render(RDBTableMetaData metaData, Object param) {
        SqlAppender createBody = new SqlAppender();
        List<String> comments = new ArrayList<>();
        Set<RDBColumnMetaData> RDBColumnMetaDatas = metaData.getColumns();
        if (RDBColumnMetaDatas.isEmpty()) throw new UnsupportedOperationException("未指定任何字段");
        createBody.add("\nCREATE TABLE ", metaData.getName(), "(");
        RDBColumnMetaDatas.forEach(column -> {
            createBody.add("\n\t", column.getName(), " ").add(column.getDataType());
            if (column.isNotNull()
                    || column.isPrimaryKey()) {
                createBody.add(" NOT NULL ");
            }
            if (column.isPrimaryKey())
                createBody.add("PRIMARY KEY ");
            //注释
            if (!StringUtils.isNullOrEmpty(column.getComment())) {
                comments.add(String.format("COMMENT ON COLUMN %s IS '%s'", column.getFullName(), column.getComment()));
            } else {
                comments.add(String.format("COMMENT ON COLUMN %s IS '%s'", column.getFullName(), column.getAlias()));

            }
            createBody.add(",");
        });
        comments.add(String.format("COMMENT ON TABLE %s IS '%s'", metaData.getName(), metaData.getComment()));
        createBody.removeLast();
        createBody.add("\n)");
        SimpleSQL simpleSQL = new SimpleSQL(createBody.toString(), param);
        if (!metaData.getPrimaryKeys().isEmpty()) {
            String pkList = metaData.getPrimaryKeys().stream().reduce((s, s2) -> s + "," + s2).get();
            String pkStr = String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY(%s)",
                    metaData.getName(), metaData.getName() + "_pk", pkList);
            comments.add(pkStr);
        }
        List<BindSQL> bindSQLs = comments.stream().map(s -> {
            BindSQL sql = new BindSQL();
            sql.setSql(new SimpleSQL(s, param));
            return sql;
        }).collect(Collectors.toList());
        simpleSQL.setBindSQLs(bindSQLs);
        return simpleSQL;
    }
}
