package ntut.csie.sslab.ddd.entity.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateProvider {
	private static Date date = null;
	
	public static Date now() {
		if(date == null) {
			return new Date();
		}
		return date;
	}
	
	public static void setDate(Date now) {
		date = now;
	}
	
	public static void resetDate() {
		date = null;
	}

	public static Date parse(String date){
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
