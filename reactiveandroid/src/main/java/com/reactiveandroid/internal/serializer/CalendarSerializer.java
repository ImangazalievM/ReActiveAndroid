package com.reactiveandroid.internal.serializer;

import android.support.annotation.Nullable;

import java.util.Calendar;

public final class CalendarSerializer extends TypeSerializer<Calendar, Long> {

	@Nullable
    public Long serialize(@Nullable Calendar data) {
		if (data == null) return null;
		return data.getTimeInMillis();
	}

	@Nullable
	public Calendar deserialize(@Nullable Long data) {
		if (data == null) return null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(data);
		return calendar;
	}

}