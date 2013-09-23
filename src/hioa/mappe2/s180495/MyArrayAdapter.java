/*
 * Charlotte Sjøthun, s180495
 * Klassen tar ett og ett person objekt fra listen og legger personens informasjon i et listView.
 */

package hioa.mappe2.s180495;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<Person>
{
	Context con;
	List<Person> personList;
	Person person;
	Calendar calendarCurrent;
	Calendar calendarBirthday;
	
	public MyArrayAdapter(Context context, int resource, int textViewResourceId, List<Person> objects)
	{
		super(context, resource, textViewResourceId, objects);
		con = context;
		personList = objects;
	} // End of constructor
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.list_layout, parent,false); 
		
		TextView firstName = (TextView)row.findViewById(R.id.firstname);
		TextView lastName = (TextView)row.findViewById(R.id.lastname);
		TextView date = (TextView)row.findViewById(R.id.date);
		
		person = personList.get(position);
		
		firstName.setText(person.getFirstName());
		lastName.setText(person.getLastName());
		
		calendarCurrent = Calendar.getInstance();
		calendarBirthday = person.getBirthday();

		date.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendarBirthday.getTime()));
		
		int currentYear = calendarCurrent.get(Calendar.YEAR);
		int currentMonth = calendarCurrent.get(Calendar.MONTH);
		int currentDay = calendarCurrent.get(Calendar.DAY_OF_MONTH);
		
		int birthdayYear = calendarBirthday.get(Calendar.YEAR);
		int birthdayMonth = calendarBirthday.get(Calendar.MONTH);
		int birthdayDay = calendarBirthday.get(Calendar.DAY_OF_MONTH);
		
		setTurnsView(row, currentYear, currentMonth, currentDay, birthdayYear, birthdayMonth, birthdayDay);
		setDaysUntilView(row, currentYear, currentMonth, currentDay, birthdayYear, birthdayMonth, birthdayDay);
		
		return row;
	} // End of method getView(...)

	
	/* Hjelpemetode som regner ut hvor mange år det er mellom dagens dato og bursdags datoen 
	 * og setter teksten på det aktuelle viewet.
	 */
	private void setTurnsView(View row, int currentYear, int currentMonth, int currentDay, int birthdayYear, int birthdayMonth, int birthdayDay)
	{
		TextView turns = (TextView)row.findViewById(R.id.turns);
		
		int turn = 0;
		
		if (currentYear < birthdayYear)
			turn = 0;
		else if ((currentYear == birthdayYear && currentMonth > birthdayMonth) 
			|| currentYear == birthdayYear && currentMonth == birthdayMonth && currentDay > birthdayDay
			|| currentMonth > birthdayMonth 
			|| (currentMonth == birthdayMonth && currentDay > birthdayDay))
			turn = currentYear - birthdayYear + 1;
		else
			turn = currentYear - birthdayYear;
		
		if (turn == 0)
		{
			TextView turnsTextView = (TextView)row.findViewById(R.id.turnsTextView);
			turnsTextView.setText("");
			turns.setText(con.getString(R.string.born));
		}
		else
			turns.setText(turn + "");
	} // End of method setTurnsView(...)
	
	
	/* Hjelpemetode som regner ut hvor mange dager det er til bursdagsdagen 
	 * og setter teksten på det aktuelle viewet.
	 */
	private void setDaysUntilView(View row, int currentYear, int currentMonth, int currentDay, int birthdayYear, int birthdayMonth, int birthdayDay)
	{
		TextView daysUntil = (TextView)row.findViewById(R.id.daysUntil);
		
		int daysBetween = 0;
		
		if (currentYear < birthdayYear)
			daysBetween = daysBetween(calendarCurrent.getTime(), calendarBirthday.getTime());
		/* Denne testen skjekker først om måneden vi er i er større en bursdagsmåneden (for da har bursdagen for det året vært)
		 * Så hvis den ikke er sann må vi teste om vi er i samme månede som burdagen og hvis vi er det må vi sjekke om 
		 */
		else if (currentMonth > birthdayMonth || (currentMonth == birthdayMonth && currentDay > birthdayDay))
		{
			Calendar dummy = Calendar.getInstance(); // Må bruke en dummy for å ikke endre datoen i person objektet.
			dummy.setTime(calendarBirthday.getTime());
			dummy.set(Calendar.YEAR, currentYear + 1);
			
			daysBetween = daysBetween(calendarCurrent.getTime(), dummy.getTime());
		}
		else
		{
			Calendar dummy = Calendar.getInstance(); // Må bruke en dummy for å ikke endre datoen i person objektet.
			dummy.setTime(calendarBirthday.getTime());
			dummy.set(Calendar.YEAR, currentYear);
			daysBetween = daysBetween(calendarCurrent.getTime(), dummy.getTime());
		}
		
		TextView daysUntilView = (TextView)row.findViewById(R.id.daysUntilView);
		
		if (daysBetween == 0)
		{
			TextView in = (TextView)row.findViewById(R.id.in);
			
			in.setText("");
			daysUntil.setText(con.getString(R.string.today));
			daysUntilView.setText("");
		}
		else if (daysBetween == 1)
		{
			daysUntil.setText(daysBetween + "");
			daysUntilView.setText(con.getString(R.string.day));
		}
		else
			daysUntil.setText(daysBetween + "");
	} // End of method setDaysUntilView(...)
	
	
	// Hjelpemetode som returnerer antall dager mellom d1 og d2
	private int daysBetween(Date d1, Date d2)
	{
		double thousand = 1000.0; // Settes til en double variabel slik at resultatet bli double så vi kan bruke Math.ceil for å runde opp.
		return (int)Math.ceil(( (d2.getTime() - d1.getTime()) / (thousand * 60 * 60 * 24)));
	} // End of method daysBetween(...)
} // End of class MyArrayAdapter