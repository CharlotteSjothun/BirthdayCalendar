/*
 * Charlotte Sjøthun, s180495
 * Klassen inneholder informasjon om en person.
 */

package hioa.mappe2.s180495;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;

public class Person implements Serializable
{
    private static final long serialVersionUID = ObjectStreamClass.lookup(Person.class).getSerialVersionUID();
    
	private String firstName, lastName;
	private int id, phoneNum;
	private Calendar birthday;
	
	public Person(String fName, String lName, int phonenum, Calendar bDay)
	{
		id = -1;
		firstName = fName;
		lastName = lName;
		phoneNum = phonenum;
		birthday = bDay;
	} // End of constructor
	
	
	public Person(int _id, String fName, String lName, int phonenum, Calendar bDay)
	{
		id = _id;
		firstName = fName;
		lastName = lName;
		phoneNum = phonenum;
		birthday = bDay;
	} // End of constructor
	
	
	public void setFirstName(String fName)
	{
		firstName = fName;
	} // End of method setFirstName(...)
	
	
	public void setLastName(String lName)
	{
		lastName = lName;
	} // End of method setLastName(...)

	
	public void setPhoneNum(int number)
	{
		phoneNum = number;
	} // End of method setPhoneNum(...)

	
	public void setBirthday(Calendar bDay)
	{
		birthday = bDay;
	} // End of method setBirthday(...)

	
	public int getId()
	{
		return id;
	} // End of method getId()

	
	public String getFirstName()
	{
		return firstName;
	} // End of method getFirstName()

	
	public String getLastName()
	{
		return lastName;
	} // End of method getLastName()

	
	public int getPhoneNum()
	{
		return phoneNum;
	} // End of method getPhoneNum()

	
	public Calendar getBirthday()
	{
		return birthday;
	} // End of method getBirthday()

	
	public String toString()
	{
		return firstName + " " + lastName + "\n" + DateFormat.getDateInstance(DateFormat.MEDIUM).format(birthday.getTime()) 
			   + "\n" + phoneNum;
	} // End of method toString()
} // End of class Person