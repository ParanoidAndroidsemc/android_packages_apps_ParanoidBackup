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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity{
    
    private String mBackupPath;
    private ArrayList<String> mBackupArray = new ArrayList();
    private CharSequence[] mItems;
    private SharedPreferences preferences;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        String mSdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mBackupPath = mSdcardPath + File.separator + "paranoid_backup" + File.separator;
        getBackupList();
        
        final Preference mBackup = findPreference("backup_pad");
        final Preference mRemove = findPreference("remove_pad");
        final Preference mRestore = findPreference("restore_pad");
        updateSummary(mRemove);
        
        mBackup.setOnPreferenceClickListener(new OnPreferenceClickListener() {
             public boolean onPreferenceClick(Preference preference) {
                 Calendar mCalendar = Calendar.getInstance();
                 String mDate = mCalendar.get(Calendar.DAY_OF_MONTH)+"."+(mCalendar.get(Calendar.MONTH)+1)+"."+
                         mCalendar.get(Calendar.YEAR)+"."+mCalendar.get(Calendar.HOUR)+"."+mCalendar.get(Calendar.MINUTE)+"."
                         +mCalendar.get(Calendar.SECOND);
                 String mBackupFile = mBackupPath+mDate+".prop";
                 RunCommands.execute(new String[]{"busybox mkdir "+mBackupPath, "busybox cp /system/pad.prop "+mBackupFile}, 0);                
                 Toast.makeText(MainActivity.this, getString(R.string.backup_sucess).replace("%s", mBackupFile), Toast.LENGTH_LONG).show();
                 updateSummary(mRemove);
                 return true;
             }
         });
        
        mRestore.setOnPreferenceClickListener(new OnPreferenceClickListener() {
             public boolean onPreferenceClick(Preference preference) {
                 if(getIndex() != 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.restore_dialog);
                    builder.setItems(mItems, new DialogInterface.OnClickListener() {
                    	 public void onClick(DialogInterface dialog, int item) {
                             RunCommands.execute(new String[]{"busybox mount -o rw,remount /system", "busybox cp "+mBackupPath+mItems[item]+" /system/pad.prop", "busybox chmod 644 /system/pad.prop", "busybox mount -o ro,remount /system"}, 0);
                             AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                             builder2.setTitle(R.string.restore_sucess);
                             builder2.setMessage(R.string.reboot_warning);
                             builder2.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                   	               PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                   	               pm.reboot("");
				}});
                             builder2.setNegativeButton(android.R.string.no, null);
                             AlertDialog alert = builder2.create();
                             alert.show();
                             
                         }
                     });
                    AlertDialog alert = builder.create();
                    alert.show();
                 } else
                     Toast.makeText(MainActivity.this, R.string.no_backups, Toast.LENGTH_LONG).show();
                 return true;
             }
         });
        
        mRemove.setOnPreferenceClickListener(new OnPreferenceClickListener() {
             public boolean onPreferenceClick(Preference preference) {
                 if(getIndex() != 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.remove_pad);
                    builder.setItems(mItems, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if(getIndex() == 1)
                                RunCommands.execute(new String[]{"busybox rm -rf "+mBackupPath}, 0);
                            else
                                RunCommands.execute(new String[]{"busybox rm "+mBackupPath+mItems[item]}, 0);
                            Toast.makeText(MainActivity.this, getString(R.string.remove_sucess).replace("%s", mItems[item]), Toast.LENGTH_LONG).show();
                            updateSummary(mRemove);
                        }
                    })
                    .setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        	RunCommands.execute(new String[]{"busybox rm -rf "+mBackupPath}, 0);
                        	updateSummary(mRemove);
                    }});
                    AlertDialog alert = builder.create();
                    alert.show();
                 } else
                     Toast.makeText(MainActivity.this, R.string.no_backups, Toast.LENGTH_LONG).show();
                 return true;
             }
         });

        /*
         * We can't detect screen automatically via API's. We need to use our ExtendedPropertiesUtils class. 
         */

        if(!Utils.getProp("ro.cm.version").equals("PARANOIDANDROID")){
            Toast.makeText(this, R.string.rom_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void updateSummary(Preference mPreference){
        getBackupList();
        mPreference.setSummary(getString(R.string.remove_pad_summary)+" "+getIndex());
    }
    
    private int getDeviceScreen(){
        return -1;
    }
    
    private ArrayList<String> getBackupList(){
        if(!mBackupArray.isEmpty())
            mBackupArray.clear();
        try{
            File mBackupFolder = new File(mBackupPath);
            File[] mBackups = mBackupFolder.listFiles();
            for(int i=0; i<mBackups.length; i++){
                mBackupArray.add(mBackups[i].getName());
            }
            mItems = new CharSequence[mBackupArray.size()];
            for(int i=0; i<mItems.length; i++){
                mItems[i] = mBackupArray.get(i);
            }
        } catch (Exception e){
            // Folder is empty
        }
        return mBackupArray;
    }
    
    private int getIndex(){
        return mBackupArray.size();
    }
}
