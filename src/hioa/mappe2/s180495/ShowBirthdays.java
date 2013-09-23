/*
 * Charlotte Sjøthun, s180495
 * Klassen er controller for show birthday.
 */

package hioa.mappe2.s180495;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ShowBirthdays extends Activity
{
	DBHandler db;
	ArrayList<Person> personList;
	ListView listview;
	MyArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_birthdays);
		
		setWidthMenuButtons();
		
		db = new DBHandler(this);
		
        listview = (ListView)findViewById(R.id.birthday_list);

        personList = db.getListSortByBirthday();
        
        adapter = new MyArrayAdapter(this, R.layout.list_layout, R.id.firstname, personList);
        listview.setAdapter(adapter);
        
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            {
        		createChoiceDialog(view, position);
            }
        });
	} // End of method onCreate(...)
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_show_birthdays, menu);
		return true;
	} // End of method onCreateOptionsMenu(...)

	
	// metoden blir kalt når man trykker på add i knappemenyen.
	public void clickAdd(View view)
	{
		Intent intent = new Intent(this, AddBirthday.class);
		startActivity(intent);
		finish();
	} // End of method clickAdd(...)
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.sms_settings : showSMSPreferenceActivity();
									 return true;
		}
		
		return false;
	} // End of method onOptionsItemSelected(...)
	
	
	// metoden blir kaldt når man trykker på SMS settings i menyvalget.
	private void showSMSPreferenceActivity()
	{
		Intent intent = new Intent(this, SMSPreferenceActivity.class);
		startActivity(intent);
	} // End of method showSMSPreferenceActivity()
	
	
	// Oppretter dialogvindu som vises på skjermen når man trykker på en i listen over bursdager.
	private void createChoiceDialog(View view, int position)
	{
		Dialog myDialog;
		Builder builder = new AlertDialog.Builder(this);
		
		final Person chosenPerson = personList.get(position);
		
		builder.setIcon(R.drawable.ic_launcher); 
		builder.setTitle(R.string.choice_dialog_title);
		builder.setMessage(chosenPerson.toString());
		 
		builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				goToAddBirthday(chosenPerson);
			}
		});
		
		builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{ 
				deletePersonFromDB(chosenPerson);
			}
		});
		 
		builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{ 
			}
		});
		
		myDialog = builder.create();
		myDialog.setCancelable(false); // Gjør at man ikke kan trykke på noe annet enn de valgene man får i dialogboksen.
		myDialog.show();
	} // End of method createChoiceDialog(...)
	
	
	// Metoden sletter personen fra databasen, listen og oppdaterer Viewet. Viser også en toast om at personen er slettet.
	private void deletePersonFromDB(Person person)
	{
		db.deletePerson(person);
		personList.remove(person);
	    adapter.notifyDataSetChanged();
	    
	    Toast t = Toast.makeText(getApplicationContext(), person.getFirstName() + " " + person.getLastName() + " " 
	    						 + getString(R.string.toast_delete_birthday), Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	} // End of method createChoiceDialog(...)
	
	
	// Metoden startet AddBirthday aktiviteten og avslutter denne.
	private void goToAddBirthday(Person person)
	{
		Intent intent = new Intent(getApplicationContext(), AddBirthday.class);
		intent.putExtra("person", person);
		startActivity(intent);
		finish();
	} // End of method goToAddBirthday(...)
	
	
	// Hjelpemetode som setter bredden på menyknappene for at de skal dekke hele bredden uansett klient.
	private void setWidthMenuButtons()
	{
		Button menuButtonShowBirthday = (Button)findViewById(R.id.birthdays);
		Button menuButtonAddBirthday = (Button)findViewById(R.id.add);
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		int width = size.x;
		
		menuButtonShowBirthday.setWidth((width/2)-1);
		menuButtonAddBirthday.setWidth((width/2)-1);
	} // End of method setWidthMenuButtons()
} // End of class ShowBirthdays