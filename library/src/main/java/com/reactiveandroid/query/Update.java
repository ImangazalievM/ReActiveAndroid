package com.reactiveandroid.query;

import android.support.annotation.NonNull;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;

/**
 * Generates a UPDATE query
 */
public final class Update extends QueryBase {

	private Update() {
		super(null, null);
	}

	public static <T extends Model> Table<T> table(Class<T> table) {
		return new Table<>(new Update(), table);
	}

	@NonNull
    @Override
	public String getPartSql() {
		return "UPDATE";
	}

	public static final class Table<T extends Model> extends QueryBase<T> {

		public Table(Query parent, Class<T> table) {
			super(parent, table);
		}

		public Set set(String set) {
			return set(set, (Object[]) null);
		}

		public Set set(String set, Object... args) {
			return new Set(this, table, set, args);
		}

		@NonNull
        @Override
		protected String getPartSql() {
			return ReActiveAndroid.getTableName(table);
		}

	}

	public static final class Set<T extends Model> extends ExecutableQueryBase<T> {

		private String set;
		private Object[] setArgs;

		private Set(Query parent, Class<T> table, String set, Object... args) {
			super(parent, table);
			this.set = set;
			this.setArgs = args;
		}

		public Where<T> where(String where) {
			return where(where, (Object[]) null);
		}

		public Where<T> where(String where, Object... args) {
			return new Where(this, table, where, args);
		}

		@NonNull
        @Override
		public String getPartSql() {
			return "SET " + set;
		}

		@NonNull
		@Override
		public String[] getPartArgs() {
			return toStringArray(setArgs);
		}

	}

	public static final class Where<T extends Model> extends ExecutableQueryBase<T> {

		private String where;
		private Object[] whereArgs;

		private Where(Query parent, Class<T> table, String where, Object[] args) {
			super(parent, table);
			this.where = where;
			whereArgs = args;
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

}
