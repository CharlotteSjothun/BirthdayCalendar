/*
 * Charlotte Sjøthun, s180495
 * Klassen er en dialog preference for å kunne vise frem en timePicker. 
 * For å kunne vise, velge og lagre tid i preferences.
 */

package hioa.mappe2.s180495;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference implements TimePicker.OnTimeChangedListener 
{
	private static final String VALIDATION_EXPRESSION = "[0-2]*[0-9]:[0-5]*[0-9]";
	private String defaultValue = "16:00";
	private boolean is24HourFormat;
	private TextView timeDisplay;
	private Context context;
 
	public TimePreference(Context con, AttributeSet attrs) 
	{
		super(con, attrs);
		is24HourFormat = DateFormat.is24HourFormat(con);
		context = con;
		initialize();
		setPositiveButtonText(R.string.save);
        setNegativeButtonText(R.string.cancel);
	} // End of constructor
 
	
	public TimePreference(Context con, AttributeSet attrs, int defStyle) 
	{
		super(con, attrs, defStyle);
		is24HourFormat = DateFormat.is24HourFormat(con);
		context = con;
		initialize();
		setPositiveButtonText(R.string.save);
        setNegativeButtonText(R.string.cancel);
	} // End of constructor

	
	@Override
	protected View onCreateDialogView() 
	{
 
		TimePicker tp = new TimePicker(getContext());
		
		tp.setIs24HourView(is24HourFormat);
		tp.setOnTimeChangedListener(this);
 
		int h = getHour();
		int m = getMinute();
		if (h >= 0 && h < 24)  tp.setCurrentHour(h);
		if ( m >= 0 && m < 60) tp.setCurrentMinute(m);
 
		return tp;
	} // End of method onCreateDialogView(...)
	
	
	@Override
    protected void onDialogClosed(boolean positiveResult) 
	{
        super.onDialogClosed(positiveResult);

        if (positiveResult) 
        {
        	String timeString = String.format("%02d:%02d",getHour() , getMinute());
    		persistString(timeString);
    		timeDisplay.setText(timeString);
    		
    		// Starter MyService slik at AlarmManager oppdateres med den nye tiden.
    		Intent service = new Intent(context, MyService.class);
    		context.startService(service);
        }
    } // End of method onDialogClosed(...)
 
	
	@Override
	public void onTimeChanged(TimePicker view, int hour, int minute) 
	{
		persistString(String.format("%02d:%02d",hour , minute));
	} // End of method onTimeChanged(...)
	
 
	@Override
	public void setDefaultValue(Object defaultValue) 
	{
		super.setDefaultValue(defaultValue);
 
		if (!(defaultValue instanceof String))
			return;
 
		if (!((String) defaultValue).matches(VALIDATION_EXPRESSION))
			return;
 
		this.defaultValue = (String) defaultValue;
	} // End of method setDefaultValue(...)
	
	
	// Legger til klokkeslettet i viewet
    @Override
    protected View onCreateView (ViewGroup parent) 
    {
    	LinearLayout layout = new LinearLayout(parent.getContext());
    	
        View prefView = super.onCreateView(parent); 
	    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 2);
	    layout.addView(prefView, lp);
	     
	    timeDisplay = new TextView(parent.getContext());
	    timeDisplay.setGravity(Gravity.CENTER | Gravity.RIGHT);
	    timeDisplay.setText(String.format("%02d:%02d", getHour(), getMinute()));
	    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
	    layout.addView(timeDisplay, lp2);
	    
	    return layout;
    } // End of method onCreateView(...)
    
	
   	// Initialiserer preference
   	private void initialize() 
   	{
   		setPersistent(true);
   		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
   		defaultValue = prefs.getString(getKey(), defaultValue);
   	} // End of method initialize(...)
    
    
	// Hjelpemetode som henter timen
	private int getHour() 
	{
		String time = getPersistedString(this.defaultValue);
		
		if (time == null || !time.matches(VALIDATION_EXPRESSION))
			return -1;
		
		return Integer.valueOf(time.split(":")[0]);
	} // End of method getHour(...)
 

	// Hjelpemetoden som henter minuttet
	private int getMinute() 
	{
		String time = getPersistedString(this.defaultValue);
		
		if (time == null || !time.matches(VALIDATION_EXPRESSION))
			return -1;
 
		return Integer.valueOf(time.split(":")[1]);
	} // End of method getMinute(...)
} // End of class TimePreference