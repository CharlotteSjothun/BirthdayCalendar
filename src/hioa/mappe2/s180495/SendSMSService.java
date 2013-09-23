/*
 * Charlotte Sjøthun, s180495
 * Klassen er en service som skjekker i databasen om noen har bursdag i dag,
 * hvis det er det sender den en sms til de som har bursdag hvis sms funksjonen er skrudd på.
 */

package hioa.mappe2.s180495;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

public class SendSMSService extends Service
{
	DBHandler db;
	SharedPreferences preferences;
	ArrayList<Person> personsWidthBirthdayToday;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		db = new DBHandler(this);
		preferences = getSharedPreferences("hioa.mappe2.s180495_preferences", MODE_MULTI_PROCESS);
		
		personsWidthBirthdayToday = db.getPersonWidthBirthdayToday();
		
		addDateOfLastBirthdayCheck();
		
		if (personsWidthBirthdayToday != null)
		{
			boolean smsIsSendt = sendSMS();
			makeNotification(smsIsSendt);
		}
		
		return Service.START_NOT_STICKY; 
	} // End of method onStartCommand(...)

	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	} // End of method onBind(...)
	
	
	// Hjelpemetode som lagrer dato for siste bursdags sjekk.
	private void addDateOfLastBirthdayCheck()
	{
		Calendar dateOfLastBirthdayCheck = db.getBirthdayCheck();
		
		if (dateOfLastBirthdayCheck == null)
			db.addBirthdayCheck(Calendar.getInstance());
		else
			db.updateBirthdayCheck(Calendar.getInstance());
	}
	
	
	// Hjelpemetode som sender sms hvis den funksjonen er på.
	private boolean sendSMS()
	{
		boolean smsEnablet = preferences.getBoolean("isSMSEnabled", true);
		
		if (smsEnablet)
		{
			String message = preferences.getString("birthdayMessage", getString(R.string.edit_text_preference_default_message));
			SmsManager sms = SmsManager.getDefault();
			
			for (int i = 0; i < personsWidthBirthdayToday.size(); i++)
			{
				Person person = personsWidthBirthdayToday.get(i);
				
				sms.sendTextMessage(String.valueOf(person.getPhoneNum()), null, message, null, null);
			}
		}
		
		return smsEnablet;
	} // End of method sendSMS()
	
	
	// Hjelpemetode som lager notifikasjonen.
	private void makeNotification(boolean smsIsSendt)
	{
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle(getString(R.string.app_name));
		
		int numOfBirthdays = personsWidthBirthdayToday.size();
		
		for (int i = 0; i < numOfBirthdays; i++)
		{
			Person person = personsWidthBirthdayToday.get(i);
			inboxStyle.addLine(person.getFirstName()  + " " + person.getLastName());
		}
		
		inboxStyle.addLine((numOfBirthdays == 1 ? getString(R.string.notificationHasBirthdayText) : getString(R.string.notificationHaveBirthdayText)));
		
		String aboutSMS;
		
		if (smsIsSendt)
			aboutSMS = getString(R.string.notificationSMSsendt);
		else
			aboutSMS = getString(R.string.notificationSMSnotSendt);
		
		Context ctx = getBaseContext();
		Intent notaficationIntent = new Intent(ctx, ShowBirthdays.class); 
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 1, notaficationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationManager nm = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
		builder.setContentIntent(contentIntent);
		builder.setTicker(numOfBirthdays + " " + (numOfBirthdays == 1 ? getString(R.string.notificationHasBirthdayText) : getString(R.string.notificationHaveBirthdayText)));
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);
		builder.setContentTitle(""); // Må være med selv om denne blir satt i inboxStyle ellers blir ikke notifikasjonen vist
		builder.setContentText(""); // Må være med selv om denne blir satt i inboxStyle ellers blir ikke notifikasjonen vist
		
		inboxStyle.setSummaryText(aboutSMS);
		
		builder.setStyle(inboxStyle);
		
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		builder.setSound(soundUri);
		
		Notification n = builder.build();
		nm.notify(1, n);
	} // End of method makeNotification(...)
} // End of class SendSMSService