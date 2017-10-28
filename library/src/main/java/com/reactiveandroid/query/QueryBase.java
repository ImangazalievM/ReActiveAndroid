package com.reactiveandroid.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.Model;
import com.reactiveandroid.query.Query;

public abstract class QueryBase<T extends Model> implements Query {

	protected Query parent;
	protected Class<T> table;

	public QueryBase(Query parent, Class<T> table) {
		this.parent = parent;
		this.table = table;
	}

	@NonNull
	@Override
	public final String getSql() {
		if (parent != null) {
			return parent.getSql() + " " + getPartSql().trim();
		}
		return getPartSql().trim();
	}

	@NonNull
	@Override
	public final String[] getArgs() {
		if (parent != null) {
			return join(parent.getArgs(), getPartArgs());
		}
		return getPartArgs();
	}

	@NonNull
	protected String getPartSql() {
		return "";
	}

	@NonNull
	protected String[] getPartArgs() {
		return new String[]{};
	}

	@NonNull
	protected final String[] toStringArray(final Object[] array) {
		if (array == null) {
			return null;
		}
		String[] transformedArray = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			transformedArray[i] = String.valueOf(array[i]);
		}
		return transformedArray;
	}

	private final String[] join(final String[] array1, final String... array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		}
		String[] joinedArray = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}

	private final String[] clone(final String[] array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

}
