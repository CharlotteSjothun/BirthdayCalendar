/*
 * Charlotte Sjøthun, s180495
 * Klassen er controller for add birthday.
 */

package hioa.mappe2.s180495;

import java.util.Calendar;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddBirthday extends Activity
{
	DBHandler db;
	EditText firstNameView, lastNameView, phoneNumView;
	DatePicker datePicker;
	InputMethodManager imm;
	Person person;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_birthday);
		
		setWidthMenuButtons();
		setHeightAddView();
		
		Intent i = this.getIntent();
		person = (Person)i.getSerializableExtra("person");
		
		db = new DBHandler(this);
		firstNameView = (EditText)findViewById(R.id.firstnameView);
		lastNameView = (EditText)findViewById(R.id.lastnameView);
		phoneNumView = (EditText)findViewById(R.id.phonenumView);
		datePicker = (DatePicker)findViewById(R.id.datePicker);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		datePicker.setFocusable(true);
		datePicker.requestFocus();
		setOnFocusChangeListener(firstNameView);
		setOnFocusChangeListener(lastNameView);
		setOnFocusChangeListener(phoneNumView);
		
		// Hvis det er sendt med ett person objekt så skal tittel og menyknappen endre tekst og feltene skal oppdateres
		if (person != null) 
		{
			setFields();
			Button addMenuButton = (Button)findViewById(R.id.add);
			addMenuButton.setText(getString(R.string.edit));
			setTitle(getString(R.string.title_edit_birthday));
		}
	} // End of method onCreate(...)


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_birthday, menu);
		return true;
	} // End of method onCreateOptionsMenu(...)
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.save_button_addview : save();
											return true;
			case R.id.sms_settings : showSMSPreferenceActivity();
									 return true;
		}
		
		return false;
	} // End of method onOptionsItemSelected(...)
	
	
	// Metoden blir kalt når man trykker på "Show list"
	public void clickShowBirthdays(View view)
	{
		imm.hideSoftInputFromWindow(view.getWindowToken(),0);
		goToShowBirthdays();
	} // End of method clickShowBirthdays(...)
	
	
	/* Metoden blir kalt når man trykkerpå save. Hvis person objektet ikke er null vil den oppdatere databasen
	 * ellers vil den legge til i databasen.
	 */
	public void save()
	{
		String firstName = firstNameView.getText().toString();
		String lastName = lastNameView.getText().toString();
		String phoneNum = phoneNumView.getText().toString();
		
		if (firstName.equals(" ") || firstName.equals(getString(R.string.required_string))  
			|| lastName.equals(" ") || lastName.equals(getString(R.string.required_string)) 
			|| !regexPhoneNr(phoneNum) || !regexName(firstName) || !regexName(lastName))
		{
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.Fields_error), Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			return;
		}
		
		int phoneNumber = Integer.parseInt(phoneNum);
		
		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth();
		int year = datePicker.getYear();
		
		Calendar date = Calendar.getInstance();
		date.set(year, month, day);
		
		if (person != null)
		{
			person.setFirstName(firstName);
			person.setLastName(lastName);
			person.setPhoneNum(phoneNumber);
			person.setBirthday(date);
			
			db.updatePerson(person);
		}
		else
		{
			Person p = new Person(firstName, lastName, phoneNumber, date);
			
			db.addBirthday(p);
		}
		
		Toast t = Toast.makeText(getApplicationContext(), R.string.toast_add_birthday, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
		
		goToShowBirthdays();
	} // End of method save()
	
	
	// Metoden blir kalt når man trykker utenfor en EditText slik at tastaturet forsvinner.
	public void removeFocusFromEditText(View view)
	{
		view.requestFocus();
		imm.hideSoftInputFromWindow(view.getWindowToken(),0);
	} // End of method removeFocusFromEditText(...)
	
	
	// Hjelpemetode som fyller ut feltene med informasjonen til klassens person objekt.
	private void setFields()
	{
		if (person != null)
		{
			firstNameView.setText(person.getFirstName());
			lastNameView.setText(person.getLastName());
			phoneNumView.setText(person.getPhoneNum() + "");
			
			Calendar c = person.getBirthday();
			int day = c.get(Calendar.DAY_OF_MONTH);
			int month = c.get(Calendar.MONTH);
			int year = c.get(Calendar.YEAR);
			
			datePicker.updateDate(year, month, day);
		}
	} // End of method setFields()
	
	
	// Hjelpemetode som starter aktiviteten "showBithdays" og avslutter denne.
	private void goToShowBirthdays()
	{
		Intent intent = new Intent(this, ShowBirthdays.class);
		startActivity(intent);
		finish();
	} // End of method goToShowBirthdays()
	
	
	// Hjelpemetode som starter aktiviteten
	private void showSMSPreferenceActivity()
	{
		Intent intent = new Intent(this, SMSPreferenceActivity.class);
		startActivity(intent);
	} // End of method showSMSPreferenceActivity()
	
	
	// Hjelpemetode som setter onFocusChangeListener på en editText.
	private void setOnFocusChangeListener(EditText editTextView)
	{
		final EditText view = editTextView;
		
		editTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() 
		{
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) 
		    {
		        if (hasFocus)
		        {
		        	view.setText("");
		        	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		        }
		        else if(!hasFocus)
		        {
		        	if (view.getText().toString().equals("")) 
		        		view.setText(R.string.required_string);
		        	
		        	imm.hideSoftInputFromWindow(view.getWindowToken(),0);
		        }
		    }
		});
	} // End of method setOnFocusChangeListener(...)
	
	
	// Hjelpemetode for regex av telefonnr.
	private boolean regexPhoneNr(String phoneNr)
    {
        return phoneNr.matches("\\d{8}");
    } // End of method regexPhoneNr(...)
	
	
	// Hjelpemetode for regex av navn
	public boolean regexName(String name)
    {
        return name.matches("[A-ZÆØÅa-zæøå ]+");
    } // End of method regexPhoneNr()
	
	
	// Hjelpemetode som setter bredden på menyknappene for at de skal dekke hele bredden uansett klient.
	private void setWidthMenuButtons()
	{
		Button menuButtonShowBirthday = (Button)findViewById(R.id.birthdays);
		Button menuButtonAddBirthday = (Button)findViewById(R.id.add);
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		int width = size.x;
		int buttonWidth = width/2;
		
		menuButtonShowBirthday.setWidth(buttonWidth);
		menuButtonAddBirthday.setWidth(buttonWidth);
	} // End of method setWidthMenuButtons()
	
	
	// Hjelpemetode for å få viewet med info feltene og viewt med datePicker til å fordele seg litt penere over skjermen.
	private void setHeightAddView()
	{
		LinearLayout formView = (LinearLayout)findViewById(R.id.formView);
		LinearLayout dateView = (LinearLayout)findViewById(R.id.dateView);
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		int height = size.x;
		int viewHeight = height/2;
		
		formView.setMinimumHeight(viewHeight);
		dateView.setMinimumHeight(viewHeight);
	} // End of method setHeightAddView()
} // End of class AddBirthday
