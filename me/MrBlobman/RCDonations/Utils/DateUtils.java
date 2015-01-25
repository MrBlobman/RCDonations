package me.MrBlobman.RCDonations.Utils;

import java.util.Calendar;

public class DateUtils {
	
	public static Day getDay(){
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case 0:
			return Day.SUNDAY;
		case 1:
			return Day.MONDAY;
		case 2:
			return Day.TUESDAY;
		case 3:
			return Day.WEDNESDAY;
		case 4:
			return Day.THRUSDAY;
		case 5:
			return Day.FRIDAY;
		case 6:
			return Day.SATURDAY;
		default:
			return Day.SUNDAY;
		}
	}
	
	/**
	 * Format expected is 10H:3s:5m, order doesnt matter, only one letter per : and case insensitive
	 * @param str string to convert
	 * @return the Long time represented by the string
	 * @throws NumberFormatException if could not parse a section
	 */
	public static Long stringTimeToLong(String str) throws NumberFormatException{
		String[] parts = str.split(":");
		Long miliSec = 0L;
		for (String time : parts){
			time = time.toUpperCase();
			if (time.contains("D")){
				miliSec = miliSec + (Long.parseLong(time.replace("D", ""))*86400000L);
			}else if (time.contains("H")){
				miliSec = miliSec + (Long.parseLong(time.replace("H", ""))*3600000L);
			}else if (time.contains("M")){
				miliSec = miliSec + (Long.parseLong(time.replace("M", ""))*60000L);
			}else if (time.contains("S")){
				miliSec = miliSec + (Long.parseLong(time.replace("S", ""))*1000L);
			}
		}
		return miliSec;
	}
	
	/**
	 * Converts a Long time to a string.
	 * @param time to convert
	 * @return the String representation of the time given
	 */
	public static String longTimeToString(Long time){
		String str = "";
		while (time != 0L){
			if (time/86400000L >= 1){
				str = str +(int)(time/86400000L)+"D:";
				time = time%86400000L;
			}else if (time/3600000L >= 1){
				str = str +(int)(time/3600000L)+"H:";
				time = time%3600000L;
			}else if (time/60000L >= 1){
				str = str +(int)(time/60000L)+"M:";
				time = time%60000L;
			}else if (time/1000L >= 1){
				str = str +(int)(time/1000L)+"S:";
				time = time%1000L;
			}else{
				//Less than a second is not important so we just dump that data and escape the loop
				break;
			}
		}
		if (str.length() >= 1){
			//Take off the trailing ':'
			str = str.substring(0, str.length()-1);
		}
		return str;
	}
	
	public static String getFormattedTimeRemaining(Long timeRemaining) {
		String toReturn = "";
		if (timeRemaining >= 86400000){
			toReturn = toReturn + timeRemaining/86400000 + "d ";
			timeRemaining = timeRemaining%86400000;
		}
		if (timeRemaining >= 3600000){
			toReturn = toReturn + timeRemaining/3600000 + "h ";
			timeRemaining = timeRemaining%3600000;
		}
		if (timeRemaining >= 60000){
			toReturn = toReturn + timeRemaining/60000 + "m ";
			timeRemaining = timeRemaining%60000;
		}
		if (timeRemaining >= 1000){
			toReturn = toReturn + timeRemaining/1000 + "s";
			timeRemaining = timeRemaining%1000;
		}
		toReturn = toReturn.length() == 0 ? "Now" : toReturn;
		return toReturn;
	}
}
