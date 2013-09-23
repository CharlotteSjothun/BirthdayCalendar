/*
 * Charlotte Sjøthun, s180495
 * Klassen oppretter en database og har metoder for å utføre sql til den.
 */

package hioa.mappe2.s180495;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper
{
	private static final String DB_NAME = "BirthdayDB";
	private static final int VERSION = 1;
	
	public static final String TABLE_PERSON = "Person";
	private static final String COLUMN_NAME_ID = "_id";
	private static final String COLUMN_NAME_FIRSTNAME = "Firstname";
	private static final String COLUMN_NAME_LASTNAME = "Lastname";
	private static final String COLUMN_NAME_PHONE = "Phonenumber";
	private static final String COLUMN_NAME_BIRTHDAY = "Birthday";
	
	private static final String TABLE_BIRTHDAY_CHECKED = "BirthdayChecked";
	private static final String COLUMN_NAME_DATE_LAST_CHECKED = "LastChecked";
	
	private SQLiteDatabase myDB;
	
	public DBHandler(Context context)
	{
		super(context, DB_NAME, null, VERSION);
		myDB = getWritableDatabase(); 
	} // End of constructor

	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String createPerson = "CREATE TABLE " + TABLE_PERSON + "(" + COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " + COLUMN_NAME_FIRSTNAME + " TEXT, "
							  + COLUMN_NAME_LASTNAME + " TEXT, " + COLUMN_NAME_PHONE + " INTEGER, " + COLUMN_NAME_BIRTHDAY + " DATE);";
		db.execSQL(createPerson);
		
		String createBirthdayChecked = "CREATE TABLE " + TABLE_BIRTHDAY_CHECKED + "(" + COLUMN_NAME_DATE_LAST_CHECKED + " DATE);";
		db.execSQL(createBirthdayChecked);
	} // End of method onCreate(...)

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIRTHDAY_CHECKED);
		onCreate(db);
	} // End of method onUpgrade(...)
	
	
	// Metode som legger inn personobjektet (som kommer inn via parameter) i databasen.
	public void addBirthday(Person person)
	{
		myDB = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(COLUMN_NAME_FIRSTNAME, person.getFirstName());
		values.put(COLUMN_NAME_LASTNAME, person.getLastName());
		values.put(COLUMN_NAME_PHONE, person.getPhoneNum());
		values.put(COLUMN_NAME_BIRTHDAY, new SimpleDateFormat("yyyy-MM-dd").format(person.getBirthday().getTime()));
		
		myDB.insert(TABLE_PERSON, null, values);
		myDB.close();
	} // End of method addBirthday(...)
	
	
	// Metode som henter ut alle personene i databasen sortert etter måned og så dag.
	public ArrayList<Person> getListSortByBirthday()
	{
		ArrayList<Person> list = new ArrayList<Person>();
		
		SQLiteDatabase rDB = getReadableDatabase();
		Cursor cursor = rDB.query(TABLE_PERSON, new String[] {"*"}, null, null,null, null, 
								  "strftime('%m', " + COLUMN_NAME_BIRTHDAY + "), strftime('%d', " + COLUMN_NAME_BIRTHDAY + ")" 
								+ " ASC", null);
		
		if (cursor.moveToFirst()) 
		{ 
			do 
			{
				int id = Integer.parseInt(cursor.getString(0));
				String firstname = cursor.getString(1);
				String lastname = cursor.getString(2);
				int phone = Integer.parseInt(cursor.getString(3));
				Calendar birthday = Calendar.getInstance();
				
				try
				{
					birthday.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(4)));
				} 
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				
				Person person = new Person(id, firstname, lastname, phone, birthday);
				list.add(person);
			} while (cursor.moveToNext());
		}
		
		rDB.close();
		return list;
	} // End of method getListSortByBirthday()
	
	
	// Henter personene som har bursdag i dag fra databasen.
	public ArrayList<Person> getPersonWidthBirthdayToday()
	{
		ArrayList<Person> list = new ArrayList<Person>();
		
		SQLiteDatabase rDB = getReadableDatabase();	
		Cursor cursor = rDB.query(TABLE_PERSON, new String[] {"*"}, "strftime('%m-%d', " + COLUMN_NAME_BIRTHDAY + 
								 ") = strftime('%m-%d', 'now')", null, null, null, null, null);
		
		if (cursor.moveToFirst()) 
		{ 
			do
			{
				int id = Integer.parseInt(cursor.getString(0));
				String firstname = cursor.getString(1);
				String lastname = cursor.getString(2);
				int phone = Integer.parseInt(cursor.getString(3));
				Calendar birthday = Calendar.getInstance();
				
				try
				{
					birthday.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(4)));
				} 
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				
				Person person = new Person(id, firstname, lastname, phone, birthday);
				list.add(person);
			} while (cursor.moveToNext());
		}
		else
			list = null;
		rDB.close();
		
		return list;
	} // End of method getPersonWidthBirthdayToday()
	
	
	// Metoden oppdaterer verdiene til personen (som kommer inn som parameter) i databasen.
	public boolean updatePerson(Person person)
	{
		myDB = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_FIRSTNAME, person.getFirstName());
		values.put(COLUMN_NAME_LASTNAME, person.getLastName());
		values.put(COLUMN_NAME_PHONE, person.getPhoneNum());
		values.put(COLUMN_NAME_BIRTHDAY, new SimpleDateFormat("yyyy-MM-dd").format(person.getBirthday().getTime()));
		
		return myDB.update(TABLE_PERSON, values, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(person.getId())}) > 0;
	} // End of method updatePerson(...)
	
	
	// Metoden sletter personobjektet fra databasen
	public void deletePerson(Person person)
	{
		myDB = getWritableDatabase();
		myDB.delete(TABLE_PERSON, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(person.getId())}); 
		myDB.close();
	} // End of method deletePerson(...)
	
	
	// Metode som legger inn datoen i databasen.
	public void addBirthdayCheck(Calendar calendar)
	{
		myDB = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_DATE_LAST_CHECKED, new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
		
		myDB.insert(TABLE_BIRTHDAY_CHECKED, null, values);
		myDB.close();
	} // End of method addBirthdayCheck(...)
	
	
	// Henter datoen for siste sjekk fra databasen.
	public Calendar getBirthdayCheck()
	{
		SQLiteDatabase rDB = getReadableDatabase();	
		Cursor cursor = rDB.query(TABLE_BIRTHDAY_CHECKED, new String[] {"*"}, null, null, null, null, null, null);	
		
		Calendar birthday = Calendar.getInstance();
		
		if (cursor.moveToFirst()) 
		{ 
			do
			{				
				try
				{
					birthday.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(0)));
				} 
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				
			} while (cursor.moveToNext());
		}
		else
			birthday = null;
		
		rDB.close();
		
		return birthday;
	} // End of method getBirthdayCheck()
	
	
	// Metoden oppdaterer verdiene til datoen for siste sjekk i databasen.
	public boolean updateBirthdayCheck(Calendar calendar)
	{
		myDB = getWritableDatabase();
		
		String checkDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_DATE_LAST_CHECKED, checkDate);
		
		return myDB.update(TABLE_BIRTHDAY_CHECKED, values, COLUMN_NAME_DATE_LAST_CHECKED + " = ?", new String[]{checkDate}) > 0;
	} // End of method updateBirthdayCheck(...)
} // End of class DBHandler
