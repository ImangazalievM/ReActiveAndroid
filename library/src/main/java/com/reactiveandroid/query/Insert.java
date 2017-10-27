package com.reactiveandroid.query;

import android.text.TextUtils;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;

/**
 * Generates a INSERT query
 */
public final class Insert extends QueryBase {

	private Insert() {
		super(null, null);
	}

	public static <T extends Model> Into<T> into(Class<T> table) {
		return new Into<>(new Insert(), table);
	}

	@Override
	public String getPartSql() {
		return "INSERT";
	}

	public static final class Into<T extends Model> extends QueryBase<T> {

		private Into(Query parent, Class<T> table) {
			super(parent, table);
		}

		public Columns<T> columns(String... columns) {
			return new Columns<>(this, table, columns);
		}

		public Values<T> values(Object... args) {
			return new Values<>(this, table, args);
		}

		@Override
		protected String getPartSql() {
			StringBuilder builder = new StringBuilder();
			builder.append("INTO ");
			builder.append(ReActiveAndroid.getTableName(table));
			return builder.toString();
		}

	}

	public static final class Columns<T extends Model> extends QueryBase<T> {

		private String[] mColumns;

		public Columns(Query parent, Class<T> table, String[] columns) {
			super(parent, table);
			mColumns = columns;
		}

		public Values<T> values(Object... args) {
			if (mColumns.length != args.length) {
				throw new MalformedQueryException("Number of columns does not match number of values.");
			}
			return new Values<>(this, table, args);
		}

		@Override
		protected String getPartSql() {
			StringBuilder builder = new StringBuilder();
			if (mColumns != null && mColumns.length > 0) {
				builder.append("(").append(TextUtils.join(", ", mColumns)).append(")");
			}
			return builder.toString();
		}

	}

	public static final class Values<T extends Model> extends ExecutableQueryBase<T> {

		private Object[] valuesArgs;

		private Values(Query parent, Class<T> table, Object[] args) {
			super(parent, table);
			valuesArgs = args;
		}

		@Override
		protected String getPartSql() {
			StringBuilder builder = new StringBuilder();
			builder.append("VALUES(");
			for (int i = 0; i < valuesArgs.length; i++) {
				if (i > 0) {
					builder.append(", ");
				}
				builder.append("?");
			}
			builder.append(")");
			return builder.toString();
		}

		@Override
		protected String[] getPartArgs() {
			return toStringArray(valuesArgs);
		}

	}

}
