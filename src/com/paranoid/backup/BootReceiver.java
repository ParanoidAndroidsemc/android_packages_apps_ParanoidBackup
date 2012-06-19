package com.paranoid.backup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

public class BootReceiver extends BroadcastReceiver 
{ 
    @Override 
    public void onReceive(Context context, Intent intent) { 
    	if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
    	    final SharedPreferences sharedPreferences1 = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
         final SharedPreferences.Editor editor = sharedPreferences1.edit();
         String Saved_Version = sharedPreferences1.getString("Version", "");
    	
    	StringTokenizer tokens = new StringTokenizer(getVersion(), "-"); 
	String first = tokens.nextToken();
	String second = tokens.nextToken();
	String third = tokens.nextToken();
    		
 	f(!third.equals(Saved_Version)) {
 	    editor.putString("Version", third);
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
