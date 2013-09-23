/*
 * Charlotte Sjøthun, s180495
 * Klassen er en preference activity
 */

package hioa.mappe2.s180495;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SMSPreferenceActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    } // End of method onCreate(...)

	
	// Klasse for å lage fragment av preference.
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        } // End of method onCreate(...)
    } // End of class MyPreferenceFragment
} // End of class SMSPreferenceActivity
