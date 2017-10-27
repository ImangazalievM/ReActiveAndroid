package com.reactiveandroid.query;

import com.reactiveandroid.Model;
import com.reactiveandroid.utils.QueryUtils;

abstract class DeleteQueryBase<T extends Model> extends ExecutableQueryBase<T> {

	DeleteQueryBase(Query parent, Class<T> table) {
		super(parent, table);
	}

	@Override
	public void execute() {
		QueryUtils.execSQL(table, getSql(), getArgs());
	}

}
