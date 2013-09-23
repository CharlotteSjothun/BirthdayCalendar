/*
 * Charlotte Sjøthun, s180495
 * Klassen er en service som starter en annen service med en alarmManager.
 */

package hioa.mappe2.s180495;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.format.DateFormat;

public class MyService extends Service
{
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{   
        Calendar calendarTimeFromPreferences = getTimeFromPreferences();
		
		Intent smsIntent = new Intent(this, SendSMSService.class); 
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, smsIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
		
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendarTimeFromPreferences.getTimeInMillis(), 86400000, pendingIntent);
			
		return Service.START_NOT_STICKY; 
	} // End of method onStartCommand(...)
	
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null; 
	} // End of method onBind(...)
	
	
	/* Hjelpemetode som henter tiden som er lagret i preferences og sjekker om en sjekk mot databasen har allerede
	 * har blitt kjørt den dagen.
	 */
	private Calendar getTimeFromPreferences()
	{
		SharedPreferences preferences = getSharedPreferences("hioa.mappe2.s180495_preferences", MODE_MULTI_PROCESS);
		
		String timeTest = preferences.getString("smsTime", "16:00");
		
		String[] timeParts = timeTest.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        Calendar calendarTimeFromPreferences = Calendar.getInstance();
		
		int hourConstantInt;
        
		if (DateFormat.is24HourFormat(this))
			hourConstantInt = Calendar.HOUR_OF_DAY;
		else
			hourConstantInt = Calendar.HOUR;
		
		DBHandler db = new DBHandler(this);
		Calendar dateOfLastBirthdayCheck = db.getBirthdayCheck();
		
		/* sjekker om det har blitt kjørt en sjekk mot databasen samme dag. hvis det har blitt kjørt så legges det til en 
		 * dag i millisekunder.
		 */
		if (dateOfLastBirthdayCheck != null 
			&& dateOfLastBirthdayCheck.get(Calendar.DAY_OF_MONTH) == calendarTimeFromPreferences.get(Calendar.DAY_OF_MONTH)
			&& dateOfLastBirthdayCheck.get(Calendar.MONTH) == calendarTimeFromPreferences.get(Calendar.MONTH))
		{
			calendarTimeFromPreferences.setTimeInMillis(calendarTimeFromPreferences.getTimeInMillis() + 86400000);
		}

		calendarTimeFromPreferences.set(hourConstantInt, hour);
		calendarTimeFromPreferences.set(Calendar.MINUTE, minute);
		calendarTimeFromPreferences.set(Calendar.SECOND, 0);
		
		return calendarTimeFromPreferences;
	} // End of method getTimeFromPreferences()
} // End of class MyService