/*
 * Copyright (C) 2012 ParanoidAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paranoid.backup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
	
    @Override 
    public void onReceive(Context context, Intent intent) { 
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
    	    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            String storedVersion = sharedPreferences.getString("version", "");
            String currentVersion = String.valueOf(Utils.getRomVersion());
    		
 	    if(!currentVersion.equals(storedVersion) && !storedVersion.equals("")) {
 	        Intent i = new Intent(context, MainActivity.class);
 	        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	        context.startActivity(i);
 	    }

            editor.putString("version", currentVersion);
            editor.commit();
	}
  }
    
}
