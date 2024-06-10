/*
 * @project: Numberplate Scanner - https://gitlab.com/obrymec/number_plate_scanner
 * @fileoverview: Provides some additional methods for dates/times.
 * @author: Obrymec - obrymecsprinces@gmail.com
 * @file: DateTime.java
 * @created: 2024-04-30
 * @updated: 2024-05-11
 * @supported: ANDROID
 * @version: 0.0.2
 */

/// Package name.
package org.cacybernet.numberplatescanner.utils;

/// Java dependencies.
import java.time.LocalDate;
import java.time.LocalTime;

/// Android dependencies.
import androidx.annotation.Nullable;

/**
 * Provides additional methods for dates and times.
 */
@SuppressWarnings ("unused")
public final class DateTime {
	/// Attributes.
	private static DateTime instance = null;

	/**
	 * Blocks any object instantiation.
	 */
	private DateTime () {}

	/**
	 * Returns a single instance of this class.
	 * @return DateTime
	 */
	@SuppressWarnings("all")
	public static DateTime getInstance () {
		// Whether there are no instance.
		if (instance == null) instance = new DateTime();
		// Sends that unique instance.
		return instance;
	}

	/**
	 * Converts a local date into string.
	 * @param date The date to parse.
	 * @return String
	 */
	@Nullable
	public String parseDate (LocalDate date) {
		// Whether the date is not defined.
		if (date != null) {
			// The month of year.
			String month = Integer.toString(date.getMonthValue());
			// The day of month.
			String day = (Integer.toString(date.getDayOfMonth()));
			// Corrects the month.
			month = (month.length() < 2 ? ("0" + month) : month);
			// The year value.
			String year = (Integer.toString(date.getYear()));
			// Corrects the year.
			year = (year.length() < 2 ? ("0" + year) : year);
			// Corrects the day.
			day = (day.length() < 2 ? ("0" + day) : day);
			// Sends result.
			return (day + "/" + month + "/" + year);
		}
		// Sends for others cases.
		return null;
	}

	/**
	 * Converts a local time into string.
	 * @param time The time to parse.
	 * @return String
	 */
	@Nullable
	public String parseTime (LocalTime time) {
		// Whether the time is not defined.
		if (time != null) {
			// The second value.
			String second = (Integer.toString(time.getSecond()));
			// The minute value.
			String minute = (Integer.toString(time.getMinute()));
			// The hour value.
			String hour = (Integer.toString(time.getHour()));
			// Corrects second.
			second = (second.length() < 2 ? ("0" + second) : second);
			// Corrects minute.
			minute = (minute.length() < 2 ? ("0" + minute) : minute);
			// Corrects hour.
			hour = (hour.length() < 2 ? ("0" + hour) : hour);
			// Sends result.
			return (hour + "h:" + minute + "min:" + second + "s");
		}
		// Sends for others cases.
		return null;
	}
}
