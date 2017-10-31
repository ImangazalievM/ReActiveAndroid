package com.reactiveandroid.query;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.reactiveandroid.ReActiveAndroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a SELECT query
 */
public final class Select<T> extends QueryBase<T> {

    private Select() {
        super(null, null);
    }

    public static <T> Qualifier<T> all() {
        return new Qualifier<>(new Select(), false);
    }

    public static <T> Qualifier<T> distinct() {
        return new Qualifier<>(new Select(), true);
    }

    public static <T> Columns<T> columns(String... columns) {
        return new Columns<>(new Select(), null, columns);
    }

    public static <T> From<T> from(Class<T> table) {
        return new Columns<>(new Select(), table, null).from(table);
    }

    public static <T> Columns<T> count() {
        return columns("COUNT(*)");
    }

    public static <T> Columns<T> avg(String columnName) {
        return columns("AVG(" + columnName + ")");
    }

    public static <T> Columns<T> max(String columnName) {
        return columns("MAX(" + columnName + ")");
    }

    public static <T> Columns<T> min(String columnName) {
        return columns("MIN(" + columnName + ")");
    }

    public static <T> Columns<T> sum(String columnName) {
        return columns("SUM(" + columnName + ")");
    }

    @NonNull
    @Override
    public String getPartSql() {
        return "SELECT";
    }

    public static final class Qualifier<T> extends QueryBase<T> {

        private boolean distinct;

        public Qualifier(Query parent, boolean distinct) {
            super(parent, null);
            this.distinct = distinct;
        }

        public Columns<T> columns(String... columns) {
            return new Columns<>(this, null, columns);
        }

        public From<T> from(Class<T> table) {
            return new Columns<>(this, table, null).from(table);
        }

        @NonNull
        @Override
        public String getPartSql() {
            return distinct ? "DISTINCT" : "ALL";
        }
    }

    public static final class Columns<T> extends QueryBase<T> {

        private String[] columns;

        public Columns(Query parent, Class<T> table, String[] columns) {
            super(parent, table);
            this.columns = columns;
        }

        public <T> From<T> from(Class<T> table) {
            return new From<>(this, table);
        }

        @NonNull
        @Override
        public String getPartSql() {
            StringBuilder builder = new StringBuilder();
            if (columns != null && columns.length > 0) {
                builder.append(TextUtils.join(", ", columns)).append(" ");
            } else {
                builder.append(" * ");
            }
            return builder.toString();
        }

    }

    public static final class From<T> extends ResultQueryBase<T> {

        private String alias;
        private List<Join> joins = new ArrayList<>();

        private From(Query parent, Class<T> table) {
            super(parent, table);
        }

        public From<T> as(String alias) {
            this.alias = alias;
            return this;
        }

        public <E> Join<T, E> join(Class<E> table) {
            return addJoin(table, Join.Type.JOIN);
        }

        public <E> Join<T, E> leftJoin(Class<E> table) {
            return addJoin(table, Join.Type.LEFT);
        }

        public <E> Join<T, E> leftOuterJoin(Class<E> table) {
            return addJoin(table, Join.Type.LEFT_OUTER);
        }

        public <E> Join<T, E> innerJoin(Class<E> table) {
            return addJoin(table, Join.Type.INNER);
        }

        public <E> Join<T, E> crossJoin(Class<E> table) {
            return addJoin(table, Join.Type.CROSS);
        }

        public <E> Join<T, E> naturalJoin(Class<E> table) {
            return addJoin(table, Join.Type.NATURAL_JOIN);
        }

        public <E> Join<T, E> naturalLeftJoin(Class<E> table) {
            return addJoin(table, Join.Type.NATURAL_LEFT);
        }

        public <E> Join<T, E> naturalLeftOuterJoin(Class<E> table) {
            return addJoin(table, Join.Type.NATURAL_LEFT_OUTER);
        }

        public <E> Join<T, E> naturalInnerJoin(Class<E> table) {
            return addJoin(table, Join.Type.NATURAL_INNER);
        }

        public <E> Join<T, E> naturalCrossJoin(Class<E> table) {
            return addJoin(table, Join.Type.NATURAL_CROSS);
        }

        public Where<T> where(String where) {
            return new Where<>(this, table, where, null);
        }

        public Where<T> where(String where, Object... args) {
            return new Where<>(this, table, where, args);
        }

        public GroupBy<T> groupBy(String groupBy) {
            return new GroupBy<>(this, table, groupBy);
        }

        public OrderBy<T> orderBy(String orderBy) {
            return new OrderBy<>(this, table, orderBy);
        }

        public Limit<T> limit(String limit) {
            return new Limit<>(this, table, limit);
        }

        public Offset<T> offset(String offset) {
            return new Offset<>(this, table, offset);
        }

        private <E> Join<T, E> addJoin(Class<E> table, Join.Type type) {
            Join<T, E> join = new Join<>(this, table, type);
            joins.add(join);
            return join;
        }

        @NonNull
        @Override
        public String getPartSql() {
            StringBuilder builder = new StringBuilder("FROM ");
            builder.append(ReActiveAndroid.getTableName(table)).append(" ");

            if (alias != null ) {
                builder.append("AS ").append(alias).append(" ");
            }


            for (Join join : joins) {
                builder.append(join.getPartSql()).append(" ");
            }

            return builder.toString();
        }
    }

    public static final class Join<P, T> extends QueryBase<T> {

        public enum Type {

            JOIN("JOIN"),
            LEFT("LEFT JOIN"),
            LEFT_OUTER("LEFT OUTER JOIN"),
            INNER("INNER JOIN"),
            CROSS("CROSS JOIN"),
            NATURAL_JOIN("NATURAL JOIN"),
            NATURAL_LEFT("NATURAL LEFT JOIN"),
            NATURAL_LEFT_OUTER("NATURAL LEFT OUTER JOIN"),
            NATURAL_INNER("NATURAL INNER JOIN"),
            NATURAL_CROSS("NATURAL CROSS JOIN");

            private String keyword;

            Type(String keyword) {
                this.keyword = keyword;
            }

            public String getKeyword() {
                return keyword;
            }
        }

        private Type type;
        private String constraint;
        private String alias;

        private Join(From<P> parent, Class<T> table, Type type) {
            super(parent, table);
            this.type = type;
        }

        public Join<P, T> as(String alias) {
            this.alias = alias;
            return this;
        }

        public From<P> on(String constraint) {
            this.constraint = "ON " + constraint;
            return (From<P>) parent;
        }

        public From<P> using(String... columns) {
            constraint = "USING (" + TextUtils.join(", ", columns) + ")";
            return (From<P>) parent;
        }

        @NonNull
        @Override
        public String getPartSql() {
            StringBuilder builder = new StringBuilder(type.getKeyword());
            builder.append(" ").append(ReActiveAndroid.getTableName(table)).append(" ");
            if (alias != null ) {
                builder.append("AS ").append(alias).append(" ");
            }
            builder.append(constraint);
            return builder.toString();
        }
    }

    public static final class Where<T> extends ResultQueryBase<T> {

        private String where;
        private Object[] whereArgs;

        private Where(Query parent, Class<T> table, String where, Object[] args) {
            super(parent, table);
            this.where = where;
            this.whereArgs = args;
        }

        public GroupBy<T> groupBy(String groupBy) {
            return new GroupBy<>(this, table, groupBy);
        }

        public OrderBy<T> orderBy(String orderBy) {
            return new OrderBy<>(this, table, orderBy);
        }

        public Limit<T> limit(String limits) {
            return new Limit<>(this, table, limits);
        }

        public Offset<T> offset(String offset) {
            return new Offset<>(this, table, offset);
        }

        @NonNull
        @Override
        public String getPartSql() {
            return "WHERE " + where;
        }

        @NonNull
        @Override
        public String[] getPartArgs() {
            return toStringArray(whereArgs);
        }

    }

    public static final class GroupBy<T> extends ResultQueryBase<T> {

        private String groupBy;

        private GroupBy(Query parent, Class<T> table, String groupBy) {
            super(parent, table);
            this.groupBy = groupBy;
        }

        public Having<T> having(String having) {
            return new Having<>(this, table, having);
        }

        public OrderBy<T> orderBy(String orderBy) {
            return new OrderBy<>(this, table, orderBy);
        }

        public Limit<T> limit(String limits) {
            return new Limit<>(this, table, limits);
        }

        @NonNull
        @Override
        public String getPartSql() {
            return "GROUP BY " + groupBy;
        }

    }

    public static final class Having<T> extends ResultQueryBase<T> {

        private String having;

        private Having(Query parent, Class<T> table, String having) {
            super(parent, table);
            this.having = having;
        }

        public OrderBy<T> orderBy(String orderBy) {
            return new OrderBy<>(this, table, orderBy);
        }

        public Limit<T> limit(String limits) {
            return new Limit<>(this, table, limits);
        }

        public Offset<T> offset(String offset) {
            return new Offset<>(this, table, offset);
        }

        @NonNull
        @Override
        public String getPartSql() {
            return "HAVING " + having;
        }

    }

    public static final class OrderBy<T> extends ResultQueryBase<T> {

        private String orderBy;

        private OrderBy(Query parent, Class<T> table, String orderBy) {
            super(parent, table);
            this.orderBy = orderBy;
        }

        public Limit<T> limit(String limits) {
            return new Limit<>(this, table, limits);
        }

        public Offset<T> offset(String offset) {
            return new Offset<>(this, table, offset);
        }

        @NonNull
        @Override
        public String getPartSql() {
            return "ORDER BY " + orderBy;
        }

    }

    public static final class Limit<T> extends ResultQueryBase<T> {

        private String limit;

        private Limit(Query parent, Class<T> table, String limit) {
            super(parent, table);
            this.limit = limit;
        }

        public Offset<T> offset(String offset) {
            return new Offset<>(this, table, offset);
        }

        @NonNull
        @Override
        public String getPartSql() {
            return "LIMIT " + limit;
        }
    }

    public static final class Offset<T> extends ResultQueryBase<T> {

        private String offset;

        private Offset(Query parent, Class<T> table, String offset) {
            super(parent, table);
            this.offset = offset;
        }

        @NonNull
        @Override
        protected String getPartSql() {
            return "OFFSET " + offset;
        }

    }

}
