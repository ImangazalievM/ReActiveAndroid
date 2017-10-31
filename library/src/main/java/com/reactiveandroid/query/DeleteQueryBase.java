package com.reactiveandroid.query;

import com.reactiveandroid.internal.utils.QueryUtils;

abstract class DeleteQueryBase<T> extends ExecutableQueryBase<T> {

	DeleteQueryBase(Query parent, Class<T> table) {
		super(parent, table);
	}

	@Override
	public void execute() {
		QueryUtils.execSQL(table, getSql(), getArgs());
	}

}
