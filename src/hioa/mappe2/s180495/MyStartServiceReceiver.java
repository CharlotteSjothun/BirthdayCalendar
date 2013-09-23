/*
 * Charlotte Sjøthun, s180495
 * Klassen er en broadcastReceiver som starter myService.
 */

package hioa.mappe2.s180495;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartServiceReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent service = new Intent(context, MyService.class);
		context.startService(service);
	} // End of method onReceive(...)
} // End of class MyStartServiceReceiver