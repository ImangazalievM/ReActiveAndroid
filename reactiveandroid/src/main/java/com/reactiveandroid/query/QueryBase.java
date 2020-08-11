package com.reactiveandroid.query;

import android.support.annotation.NonNull;

public abstract class QueryBase<TableClass> implements Query {

	protected Query parent;
	protected Class<TableClass> table;

	public QueryBase(Query parent, Class<TableClass> table) {
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
			Object argument = array[i];
			if (argument instanceof Boolean) {
				Boolean booleanArgument = (Boolean) argument;
				transformedArray[i] = booleanArgument ? "1" : "0";
			} else {
				transformedArray[i] = String.valueOf(argument);
			}

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
