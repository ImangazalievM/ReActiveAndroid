package com.reactiveandroid.query;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;

/**
 * Generates a DELETE query
 */
public final class Delete extends QueryBase {

	private Delete() {
		super(null, null);
	}

	public static <T extends Model> From from(Class<T> table) {
		return new From<>(new Delete(), table);
	}

	@Override
	public String getPartSql() {
		return "DELETE";
	}

	public static final class From<T extends Model> extends DeleteQueryBase<T> {

		private From(Query parent, Class<T> table) {
			super(parent, table);
		}

		public Where<T> where(String where) {
			return where(where, (Object[]) null);
		}

		public Where<T> where(String where, Object... args) {
			return new Where<>(this, table, where, args);
		}

		@Override
		public String getPartSql() {
			return "FROM " + ReActiveAndroid.getTableName(table);
		}

	}

	public static final class Where<T extends Model> extends DeleteQueryBase<T> {

		private String where;
		private Object[] whereArgs;

		public Where(Query parent, Class<T> table, String where, Object[] args) {
			super(parent, table);
			this.where = where;
			whereArgs = args;
		}

		@Override
		public String getPartSql() {
			return "WHERE " + where;
		}

		@Override
		public String[] getPartArgs() {
			return toStringArray(whereArgs);
		}

	}

}
