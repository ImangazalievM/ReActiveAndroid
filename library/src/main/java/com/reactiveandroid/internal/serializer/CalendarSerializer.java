package com.reactiveandroid.internal.serializer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

public final class CalendarSerializer extends TypeSerializer<Calendar, Long> {

	@Nullable
    public Long serialize(@NonNull Calendar data) {
		return data.getTimeInMillis();
	}

	@Nullable
	public Calendar deserialize(@NonNull Long data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(data);
		return calendar;
	}

}