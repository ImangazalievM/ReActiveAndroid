package com.reactiveandroid.internal.serializer;

import java.util.Calendar;

public final class CalendarSerializer extends TypeSerializer<Calendar, Long> {

	public Long serialize(Calendar data) {
		return data.getTimeInMillis();
	}

	public Calendar deserialize(Long data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(data);

		return calendar;
	}

}