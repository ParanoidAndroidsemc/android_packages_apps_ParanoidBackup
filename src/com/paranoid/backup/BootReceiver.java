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

import java.io.IOException;
import java.io.InputStream;

public class BootReceiver extends BroadcastReceiver { 
	
    @Override 
    public void onReceive(Context context, Intent intent) { 
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
    	    final SharedPreferences sharedPreferences1 = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences1.edit();
            String Saved_Version = sharedPreferences1.getString("Version", "");
    		
 	    if(!getVersion().equals(Saved_Version)) {
 	    } else {
 	        editor.putString("Version", getVersion());
                editor.commit();
 		
 	        Intent i = new Intent(context, MainActivity.class);
 	        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	        context.startActivity(i);
 	    }
	}
  }
    
  private String getVersion(){
    ProcessBuilder cmd;
    String result="";

    try{
         String[] args = {"/system/bin/toolbox", "getprop", "ro.pa.version"};
         cmd = new ProcessBuilder(args);
      
         Process process = cmd.start();
         InputStream in = process.getInputStream();
         byte[] re = new byte[1];
         while(in.read(re) != -1){
             result = result + new String(re);
         }
         in.close();  
     } catch(IOException ex){
         ex.printStackTrace();
     }
     return result;
  }
    
}
