package com.reactiveandroid.internal.serializer;

import java.util.Date;

public final class UtilDateSerializer extends TypeSerializer<Date, Long> {

	public Long serialize(Date data) {
		if (data == null) {
			return null;
		}

		return data.getTime();
	}

	public Date deserialize(Long data) {
		if (data == null) {
			return null;
		}

		return new Date(data);
	}

}