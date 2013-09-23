/*
 * Charlotte Sjøthun, s180495
 * Klassen er en contentProvider som kan hente ut alle Personene i Person tabellen
 * eller bare en med gitt id.
 */

package hioa.mappe2.s180495;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Provider extends ContentProvider
{
	public final static String PROVIDER = "hioa.mappe2.s180495"; 
	private static final int PERSON = 1; 
	private static final int PERSONS = 2;
	public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER + "/person");

	private static final UriMatcher uriMatcher; 
	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH); 
		uriMatcher.addURI(PROVIDER, "person", PERSONS); 
		uriMatcher.addURI(PROVIDER, "person/#", PERSON);
	}
	
	private DBHandler DBHelper;
	private SQLiteDatabase db;

	@Override
	public boolean onCreate()
	{
		DBHelper = new DBHandler(getContext()); 
		db = DBHelper.getWritableDatabase(); 
		return true;
	} // End of method onCreate()

	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2)
	{
		// TODO Auto-generated method stub
		return 0;
	} // End of method delete(...)

	
	@Override
	public String getType(Uri uri)
	{
		switch(uriMatcher.match(uri))
		{
			case PERSONS : return "vnd.android.cursor.dir/vnd.example.person";
			case PERSON  : return "vnd.android.cursor.item/vnd.example.person";
			default : throw new IllegalArgumentException("Ugyldig URI" + uri);
		}
	} // End of method getType(...)

	
	@Override
	public Uri insert(Uri arg0, ContentValues arg1)
	{
		// TODO Auto-generated method stub
		return null;
	} // End of method insert(...)

	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder)
	{
		Cursor cur = null;
		
		if (uriMatcher.match(uri) == PERSON)
		{
			cur = db.query(DBHandler.TABLE_PERSON, projection, "_id = " + uri.getPathSegments().get(1) ,selectionArgs, null, null, sortOrder);
			return cur;
		}
		else
		{
			cur = db.query(DBHandler.TABLE_PERSON, null, null, null, null, null, null);
			return cur;
		}
	} // End of method query(...)

	
	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3)
	{
		// TODO Auto-generated method stub
		return 0;
	}  // End of method update(...)
}
