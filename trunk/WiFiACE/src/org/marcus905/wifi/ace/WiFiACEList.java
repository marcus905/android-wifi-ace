/* Copyright 2010 OddRain
 * Copyright 2010 marcus905 <marcus90@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.marcus905.wifi.ace;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class WiFiACEList extends Activity implements
		OnSharedPreferenceChangeListener {

	private static final String INT_PRIVATE_KEY = "private_key";
	private static final String INT_PHASE2 = "phase2";
	private static final String INT_PASSWORD = "password";
	private static final String INT_IDENTITY = "identity";
	private static final String INT_EAP = "eap";
	private static final String INT_CLIENT_CERT = "client_cert";
	private static final String INT_CA_CERT = "ca_cert";
	private static final String INT_ANONYMOUS_IDENTITY = "anonymous_identity";
	private static final String INT_ENTERPRISEFIELD_NAME = "android.net.wifi.WifiConfiguration$EnterpriseField";
	protected static final int SHOW_PREFERENCES = 0;
	private WifiManager wifiManager;
	private ListView aceListView;
	private List<WifiConfiguration> aceList;
	private WifiConfiguration selectedConfig;
	private WiFiACEConfigAdapter aceAdapter;
	private boolean editingPrefs = false;

	private void editConfig(WifiConfiguration selectedConfig){


		// Populate Preferences
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs
				.registerOnSharedPreferenceChangeListener(WiFiACEList.this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();

		if (selectedConfig.SSID != null) {
			editor.putString(WiFiACESettings.PREF_SSID,
					selectedConfig.SSID.replaceAll("\"", ""));
		}

		if (selectedConfig.BSSID != null) {
			editor.putString(WiFiACESettings.PREF_BSSID,
					selectedConfig.BSSID);
		}

		editor.putBoolean(WiFiACESettings.PREF_HIDDEN_SSID,
				selectedConfig.hiddenSSID);

		editor.putBoolean(WiFiACESettings.PREF_KEY_NONE,
				selectedConfig.allowedKeyManagement
						.get(WifiConfiguration.KeyMgmt.NONE));
		editor.putBoolean(WiFiACESettings.PREF_KEY_PSK,
				selectedConfig.allowedKeyManagement
						.get(WifiConfiguration.KeyMgmt.WPA_PSK));
		editor.putBoolean(WiFiACESettings.PREF_KEY_EAP,
				selectedConfig.allowedKeyManagement
						.get(WifiConfiguration.KeyMgmt.WPA_EAP));
		editor.putBoolean(WiFiACESettings.PREF_KEY_IEEE,
				selectedConfig.allowedKeyManagement
						.get(WifiConfiguration.KeyMgmt.IEEE8021X));

		editor.putBoolean(WiFiACESettings.PREF_AUTH_OPEN,
				selectedConfig.allowedAuthAlgorithms
						.get(WifiConfiguration.AuthAlgorithm.OPEN));
		editor.putBoolean(WiFiACESettings.PREF_AUTH_LEAP,
				selectedConfig.allowedAuthAlgorithms
						.get(WifiConfiguration.AuthAlgorithm.LEAP));
		editor.putBoolean(WiFiACESettings.PREF_AUTH_SHARED,
				selectedConfig.allowedAuthAlgorithms
						.get(WifiConfiguration.AuthAlgorithm.SHARED));

		editor.putBoolean(WiFiACESettings.PREF_SEC_WPA,
				selectedConfig.allowedProtocols
						.get(WifiConfiguration.Protocol.WPA));
		editor.putBoolean(WiFiACESettings.PREF_SEC_RSN,
				selectedConfig.allowedProtocols
						.get(WifiConfiguration.Protocol.RSN));

		editor.putBoolean(WiFiACESettings.PREF_PAIR_NONE,
				selectedConfig.allowedPairwiseCiphers
						.get(WifiConfiguration.PairwiseCipher.NONE));
		editor.putBoolean(WiFiACESettings.PREF_PAIR_CCMP,
				selectedConfig.allowedPairwiseCiphers
						.get(WifiConfiguration.PairwiseCipher.CCMP));
		editor.putBoolean(WiFiACESettings.PREF_PAIR_TKIP,
				selectedConfig.allowedPairwiseCiphers
						.get(WifiConfiguration.PairwiseCipher.TKIP));

		editor.putBoolean(WiFiACESettings.PREF_GRP_WEP40,
				selectedConfig.allowedGroupCiphers
						.get(WifiConfiguration.GroupCipher.WEP40));
		editor.putBoolean(WiFiACESettings.PREF_GRP_WEP104,
				selectedConfig.allowedGroupCiphers
						.get(WifiConfiguration.GroupCipher.WEP104));
		editor.putBoolean(WiFiACESettings.PREF_GRP_TKIP,
				selectedConfig.allowedGroupCiphers
						.get(WifiConfiguration.GroupCipher.TKIP));
		editor.putBoolean(WiFiACESettings.PREF_GRP_CCMP,
				selectedConfig.allowedGroupCiphers
						.get(WifiConfiguration.GroupCipher.CCMP));

		if (selectedConfig.wepTxKeyIndex > 3
				|| selectedConfig.wepTxKeyIndex < 0) {
			editor.putInt(WiFiACESettings.PREF_WEPKEY_IDX, 
					selectedConfig.wepTxKeyIndex);
		}

		if (selectedConfig.wepKeys[0] != null
				&& selectedConfig.wepKeys[0].length() >= 2) {
			editor.putString(WiFiACESettings.PREF_WEPKEY_KEY0,
					removeQuotes(selectedConfig.wepKeys[0]));
		}

		if (selectedConfig.wepKeys[1] != null
				&& selectedConfig.wepKeys[1].length() >= 2) {
		editor.putString(WiFiACESettings.PREF_WEPKEY_KEY1,
					removeQuotes(selectedConfig.wepKeys[1]));
		}

		if (selectedConfig.wepKeys[2] != null
				&& selectedConfig.wepKeys[2].length() >= 2) {
			editor.putString(WiFiACESettings.PREF_WEPKEY_KEY2,
					removeQuotes(selectedConfig.wepKeys[2]));
		}

		if (selectedConfig.wepKeys[3] != null
				&& selectedConfig.wepKeys[3].length() >= 2) {
			editor.putString(WiFiACESettings.PREF_WEPKEY_KEY3,
					removeQuotes(selectedConfig.wepKeys[3]));
		}

		if (selectedConfig.preSharedKey != null
				&& selectedConfig.preSharedKey.length() >= 2) {
			editor.putString(WiFiACESettings.PREF_WPA_KEY,
					removeQuotes(selectedConfig.preSharedKey));
		}

		// Reflection magic needs to be done here to access non-public
		// APIs
		// Also here new ad-hoc switch for CM6 users
		// FIXME Make me pretty, as now I'm ugly

		try {
			// Let the magic start
			Class[] wcClasses = WifiConfiguration.class.getClasses();
			// null for overzealous java compiler
			Class wcEnterpriseField = null;

			for (Class wcClass : wcClasses)
				if (wcClass
						.getName()
						.equals(
								INT_ENTERPRISEFIELD_NAME)) {
					wcEnterpriseField = wcClass;
					break;
				}
			// I know there is enterpriseFields but I haven't
			// gotten around it yet
			// nulls here to workaround the overzealous java compiler
			Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
			Field[] wcefFields = WifiConfiguration.class.getFields();
			// Dispatching Field vars
			for (Field wcefField : wcefFields) {
				if (wcefField
						.getName().trim()
						.equals(
								INT_ANONYMOUS_IDENTITY))
					wcefAnonymousId = wcefField;
				else if (wcefField.getName().trim().equals(
						INT_CA_CERT))
					wcefCaCert = wcefField;
				else if (wcefField
						.getName().trim()
						.equals(
								INT_CLIENT_CERT))
					wcefClientCert = wcefField;
				else if (wcefField.getName().trim().equals(
						INT_EAP))
					wcefEap = wcefField;
				else if (wcefField.getName().trim().equals(
						INT_IDENTITY))
					wcefIdentity = wcefField;
				else if (wcefField.getName().trim().equals(
						INT_PASSWORD))
					wcefPassword = wcefField;
				else if (wcefField.getName().trim().equals(
						INT_PHASE2))
					wcefPhase2 = wcefField;
				else if (wcefField
						.getName().trim()
						.equals(
								INT_PRIVATE_KEY))
					wcefPrivateKey = wcefField;
			}
			
			Method wcefValue = null;
			for(Method m: wcEnterpriseField.getMethods())
				//System.out.println(m.getName());
				if(m.getName().trim().equals("value"))
					wcefValue = m;
			
			// if (selectedConfig.eap.value() != null) {
			String tVal = (String) wcefValue.invoke(wcefEap.get(selectedConfig), null);
			if (tVal != null) {
				editor.putString(WiFiACESettings.PREF_ENTERPRISE_EAP,
				/* selectedConfig.eap.value() */
				tVal);
			} 

			// if (selectedConfig.phase2.value() != null) {
			tVal = (String) wcefValue
					.invoke(wcefPhase2.get(selectedConfig), null);
			if (tVal != null) {
				editor.putString(
						WiFiACESettings.PREF_ENTERPRISE_PHASE2,
						removeQuotes(tVal));
			}

			// if (selectedConfig.identity.value() != null) {
			tVal = (String) wcefValue
					.invoke(wcefIdentity.get(selectedConfig), null);
			if (tVal != null) {
				editor.putString(WiFiACESettings.PREF_ENTERPRISE_IDENT,
						removeQuotes(tVal));
			}

			// if (selectedConfig.anonymous_identity.value() != null) {
			tVal = (String) wcefValue
					.invoke(wcefAnonymousId.get(selectedConfig), null);
			if (tVal != null) {
				editor.putString(
						WiFiACESettings.PREF_ENTERPRISE_ANON_IDENT,
						removeQuotes(tVal));
			}

			// if (selectedConfig.password.value() != null) {
			tVal = (String) wcefValue
					.invoke(wcefPassword.get(selectedConfig), null);
			if (tVal != null) {
				editor.putString(WiFiACESettings.PREF_ENTERPRISE_PASS,
						removeQuotes(tVal));
			}

			// if (selectedConfig.client_cert.value() != null &&
			// selectedConfig.client_cert.value().length() >= 2
			tVal = (String) wcefValue
					.invoke(wcefClientCert.get(selectedConfig), null);
			if (tVal != null && tVal.length() >= 2) {
				editor.putString(
						WiFiACESettings.PREF_ENTERPRISE_CLIENT_CERT,
						removeQuotes(tVal));
			}

			// if (selectedConfig.ca_cert.value() != null &&
			// selectedConfig.ca_cert.value().length() >= 2) {
			tVal = (String) wcefValue
					.invoke(wcefCaCert.get(selectedConfig), null);
			if (tVal != null && tVal.length() >= 2) {
				editor.putString(
						WiFiACESettings.PREF_ENTERPRISE_CA_CERT,
						removeQuotes(tVal));
			}

			// if (selectedConfig.private_key.value() != null &&
			// selectedConfig.private_key.value().length() >= 2) {
			tVal = (String) wcefValue
					.invoke(wcefPrivateKey.get(selectedConfig), null);
			if (tVal != null && tVal.length() >= 2) {
				editor.putString(
						WiFiACESettings.PREF_ENTERPRISE_PRIV_KEY,
						removeQuotes(tVal));
			}
			
			// Adhoc for CM6
			// nested try-catch for graceful fail.
			try{
			Field wcAdhoc = WifiConfiguration.class.getField("adhocSSID");
			Field wcAdhocFreq = WifiConfiguration.class.getField("frequency");
			editor.putBoolean(WiFiACESettings.PREF_ADHOC,
					wcAdhoc.getBoolean(selectedConfig));
			editor.putString(WiFiACESettings.PREF_ADHOC_FREQUENCY,
					Integer.toString(wcAdhocFreq.getInt(selectedConfig)));
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// FIXME Not used to Android, what should I do here?
			e.printStackTrace();
		}

	
		// FIXME Up to here converted 8 errors in 19 warnings.

		editor.commit();
		editingPrefs = true;
					
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Setup WiFi
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		aceListView = (ListView) findViewById(R.id.aceList);

		aceListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> _av, View _v, int _index,
					long arg3) {
				selectedConfig = aceList.get(_index);

				Context context = getApplicationContext();
					
				editConfig(selectedConfig);
				
				// Display Preferences
				Intent i = new Intent(context, WiFiACESettings.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivityForResult(i, SHOW_PREFERENCES);
			}
		});

		aceList = new ArrayList<WifiConfiguration>();
		int resID = R.layout.wifi_ace_config_item;
		aceAdapter = new WiFiACEConfigAdapter(this, resID, aceList);
		aceListView.setAdapter(aceAdapter);

		Context context = getApplicationContext();
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				loadWifiConfigs();
				System.out.println("Recieved wifi state changed action:"
						+ intent);
			}
		}, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

		checkWifiState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Builder builder = new AlertDialog.Builder(this);
		switch (item.getItemId()) {
	    case R.id.about:
	    	PackageInfo pi = null;
	    	try{
	    	pi = getPackageManager().getPackageInfo(getClass().getPackage().getName(), 0);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		}
	    	builder.setTitle(getString(R.string.ABOUT_TITLE));
	    	builder.setMessage(getString(R.string.ABOUT_CONTENT)+
					"\n\n"+pi.packageName+"\n"+
					"V"+pi.versionName+
					"C"+pi.versionCode);
			builder.setPositiveButton(getString(android.R.string.ok), null);
			builder.show();
	    	
	        return true;
	    }
	    return false;
	}

	private void loadWifiConfigs() {
		aceList.clear();
		List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration config : configs) {
			aceList.add(config);
		}
		aceAdapter.notifyDataSetChanged();
	}

	private void checkWifiState() {
		boolean enabled = wifiManager.isWifiEnabled();
		if (!enabled) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.app_name));
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setMessage(getString(R.string.WIFI_ENABLE_MSG));
			builder.setPositiveButton(getString(R.string.YES),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							wifiManager.setWifiEnabled(true);
						}
					});
			builder.setNegativeButton(getString(R.string.NO), null);
			builder.show();
		} else {
			loadWifiConfigs();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		checkWifiState();
		editingPrefs = false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (editingPrefs) {
			// Save
			saveWiFiConfig();
		}
	}

	private void saveWiFiConfig() {
		// Save Wifi Config from Preferences
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		String slidingTemp = prefs.getString(WiFiACESettings.PREF_SSID, null);
		if (slidingTemp != null) {
			selectedConfig.SSID = surroundWithQuotes(slidingTemp);
		}
		slidingTemp = prefs.getString(WiFiACESettings.PREF_BSSID, null);
		if (slidingTemp != null && 
				slidingTemp.length() == 17 && // avoid regex matching if can't be a macaddr
				slidingTemp.matches(
						"[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}")
						) {
			selectedConfig.BSSID = slidingTemp;
		}

		if (!prefs.getBoolean(WiFiACESettings.PREF_HIDDEN_SSID, false)) {
			selectedConfig.hiddenSSID = false;
		} else {
			selectedConfig.hiddenSSID = true;
		}

		selectedConfig.allowedKeyManagement.clear();
		if (prefs.getBoolean(WiFiACESettings.PREF_KEY_IEEE, false)) {
			selectedConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.IEEE8021X);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_KEY_PSK, false)) {
			selectedConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_KEY_EAP, false)) {
			selectedConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_EAP);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_KEY_NONE, false)) {
			selectedConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.NONE);
		}

		// GroupCiphers
		selectedConfig.allowedGroupCiphers.clear();
		if (prefs.getBoolean(WiFiACESettings.PREF_GRP_WEP40, false)) {
			selectedConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP40);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_GRP_WEP104, false)) {
			selectedConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_GRP_CCMP, false)) {
			selectedConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_GRP_TKIP, false)) {
			selectedConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
		}

		// PairwiseCiphers
		selectedConfig.allowedPairwiseCiphers.clear();
		if (prefs.getBoolean(WiFiACESettings.PREF_PAIR_TKIP, false)) {
			selectedConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_PAIR_CCMP, false)) {
			selectedConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_PAIR_NONE, false)) {
			selectedConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.NONE);
		}

		// Authentication Algorithms
		selectedConfig.allowedAuthAlgorithms.clear();
		if (prefs.getBoolean(WiFiACESettings.PREF_AUTH_OPEN, false)) {
			selectedConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_AUTH_SHARED, false)) {
			selectedConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_AUTH_LEAP, false)) {
			selectedConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.LEAP);
		}

		// Protocols
		selectedConfig.allowedProtocols.clear();
		if (prefs.getBoolean(WiFiACESettings.PREF_SEC_RSN, false)) {
			selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		}
		if (prefs.getBoolean(WiFiACESettings.PREF_SEC_WPA, false)) {
			selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		}

		// WEP Keys
		String pIdx = prefs.getString(WiFiACESettings.PREF_WEPKEY_IDX, "-1");
		//System.err.println(pIdx);
		int idx = Integer.parseInt(pIdx);
		if (!(idx < 0 || idx > 3))
			selectedConfig.wepTxKeyIndex = idx;


		slidingTemp = prefs.getString(WiFiACESettings.PREF_WEPKEY_KEY0, null);
		if (slidingTemp != null) {
			switch(slidingTemp.length()){
			case 10:
			case 26:
			case 58:
				if(slidingTemp.matches("[0-9A-Fa-f]*"))
					selectedConfig.wepKeys[0] = slidingTemp;
				break;
			default:
			selectedConfig.wepKeys[0] = surroundWithQuotes(prefs.getString(
					WiFiACESettings.PREF_WEPKEY_KEY0, ""));
			}
		}
		slidingTemp = prefs.getString(WiFiACESettings.PREF_WEPKEY_KEY1, null);
		if (slidingTemp != null) {
			switch(slidingTemp.length()){
			case 10:
			case 26:
			case 58:
				if(slidingTemp.matches("[0-9A-Fa-f]*"))
					selectedConfig.wepKeys[1] = slidingTemp;
				break;
			default:
			selectedConfig.wepKeys[1] = surroundWithQuotes(prefs.getString(
					WiFiACESettings.PREF_WEPKEY_KEY1, ""));
			}
		}
		slidingTemp = prefs.getString(WiFiACESettings.PREF_WEPKEY_KEY2, null);
		if (slidingTemp != null) {
			switch(slidingTemp.length()){
			case 10:
			case 26:
			case 58:
				if(slidingTemp.matches("[0-9A-Fa-f]*"))
					selectedConfig.wepKeys[2] = slidingTemp;
				break;
			default:
			selectedConfig.wepKeys[2] = surroundWithQuotes(prefs.getString(
					WiFiACESettings.PREF_WEPKEY_KEY2, ""));
			}
		}
		slidingTemp = prefs.getString(WiFiACESettings.PREF_WEPKEY_KEY3, null);
		if (slidingTemp != null) {
			switch(slidingTemp.length()){
			case 10:
			case 26:
			case 58:
				if(slidingTemp.matches("[0-9A-Fa-f]*"))
					selectedConfig.wepKeys[3] = slidingTemp;
				break;
			default:
			selectedConfig.wepKeys[3] = surroundWithQuotes(prefs.getString(
					WiFiACESettings.PREF_WEPKEY_KEY3, ""));
			}
		}
		
		slidingTemp = prefs.getString(WiFiACESettings.PREF_WPA_KEY, null);
		if (slidingTemp != null) {
			if(slidingTemp.matches("[0-9A-Fa-f]{64}"))
				selectedConfig.preSharedKey = slidingTemp;
			else
				selectedConfig.preSharedKey = surroundWithQuotes(slidingTemp);
		}

		// Enterprise Settings
		// Reflection magic here too, need access to non-public APIs
		// Used also to access CM6 adhoc support
		// FIXME Make me pretty, as I'm uglier than ever before.

		try {
			// Let the magic start
			Class[] wcClasses = WifiConfiguration.class.getClasses();
			// null for overzealous java compiler
			Class wcEnterpriseField = null;

			for (Class wcClass : wcClasses)
				if (wcClass.getName().equals(
						INT_ENTERPRISEFIELD_NAME)) {
					wcEnterpriseField = wcClass;
					break;
				}
			// I know there is enterpriseFields but I haven't
			// gotten around it yet
			// nulls here to workaround the overzealous java compiler
			Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
			Field[] wcefFields = WifiConfiguration.class.getFields();
			// Dispatching Field vars
			for (Field wcefField : wcefFields) {
				if (wcefField
						.getName()
						.equals(
								INT_ANONYMOUS_IDENTITY))
					wcefAnonymousId = wcefField;
				else if (wcefField.getName().equals(
						INT_CA_CERT))
					wcefCaCert = wcefField;
				else if (wcefField.getName().equals(
						INT_CLIENT_CERT))
					wcefClientCert = wcefField;
				else if (wcefField.getName().equals(
						INT_EAP))
					wcefEap = wcefField;
				else if (wcefField.getName().equals(
						INT_IDENTITY))
					wcefIdentity = wcefField;
				else if (wcefField.getName().equals(
						INT_PASSWORD))
					wcefPassword = wcefField;
				else if (wcefField.getName().equals(
						INT_PHASE2))
					wcefPhase2 = wcefField;
				else if (wcefField.getName().equals(
						INT_PRIVATE_KEY))
					wcefPrivateKey = wcefField;
			}
			
			Method wcefSetValue = null;

			for(Method m: wcEnterpriseField.getMethods())
				//System.out.println(m.getName());
				if(m.getName().trim().equals("setValue"))
					wcefSetValue = m;
			
			String tVal = prefs.getString(WiFiACESettings.PREF_ENTERPRISE_EAP,
					null);
			if (tVal != null) {
				// selectedConfig.eap.setValue(tVal, ""));
				wcefSetValue.invoke(
						wcefEap.get(selectedConfig), tVal);

			}
			tVal = prefs
					.getString(WiFiACESettings.PREF_ENTERPRISE_PHASE2, null);
			if (tVal != null) {
				// selectedConfig.phase2.setValue(convertToQuotedString(prefs
				// .getString(WiFiACESettings.PREF_ENTERPRISE_PHASE2, "")));
				wcefSetValue.invoke(
						wcefPhase2.get(selectedConfig),
						//surroundWithQuotes(tVal));
						tVal);
			}

			tVal = prefs.getString(WiFiACESettings.PREF_ENTERPRISE_IDENT, null);
			if (tVal != null) {
				// selectedConfig.identity.setValue(convertToQuotedString(prefs
				// .getString(WiFiACESettings.PREF_ENTERPRISE_IDENT, "")));
				wcefSetValue.invoke(
						wcefIdentity.get(selectedConfig),
						//surroundWithQuotes(tVal));
						tVal);

			}

			tVal = prefs.getString(WiFiACESettings.PREF_ENTERPRISE_ANON_IDENT,
					null);
			if (tVal != null) {
				// selectedConfig.anonymous_identity
				// .setValue(convertToQuotedString(prefs.getString(
				// WiFiACESettings.PREF_ENTERPRISE_ANON_IDENT, "")));
				wcefSetValue.invoke(
						wcefAnonymousId.get(selectedConfig),
						//surroundWithQuotes(tVal));
						tVal);

			}
			tVal = prefs.getString(WiFiACESettings.PREF_ENTERPRISE_PASS, null);
			if (tVal != null) {
				// selectedConfig.password.setValue(convertToQuotedString(prefs
				// .getString(WiFiACESettings.PREF_ENTERPRISE_PASS, "")));
				wcefSetValue.invoke(
						wcefPassword.get(selectedConfig),
				//		surroundWithQuotes(tVal));
						tVal);

			}

			tVal = prefs.getString(WiFiACESettings.PREF_ENTERPRISE_CLIENT_CERT,
					null);
			if (tVal != null) {
				// selectedConfig.client_cert.setValue(convertToQuotedString(prefs
				// .getString(WiFiACESettings.PREF_ENTERPRISE_CLIENT_CERT,
				// "")));
				wcefSetValue.invoke(
						wcefClientCert.get(selectedConfig),
						//surroundWithQuotes(tVal));
						tVal);

			}

			tVal = prefs.getString(WiFiACESettings.PREF_ENTERPRISE_CA_CERT,
					null);
			if (tVal != null) {
				// selectedConfig.ca_cert.setValue(convertToQuotedString(prefs
				// .getString(WiFiACESettings.PREF_ENTERPRISE_CA_CERT, "")));
				wcefSetValue.invoke(
						wcefCaCert.get(selectedConfig),
						//surroundWithQuotes(tVal));
						tVal);

			}

			tVal = prefs.getString(WiFiACESettings.PREF_ENTERPRISE_PRIV_KEY,
					null);
			if (tVal != null) {
				// selectedConfig.private_key.setValue(convertToQuotedString(prefs
				// .getString(WiFiACESettings.PREF_ENTERPRISE_PRIV_KEY, "")));
				wcefSetValue.invoke(
						wcefPrivateKey.get(selectedConfig),
						//surroundWithQuotes(tVal));
						tVal);

			}

			// Adhoc for CM6
			// if non-CM6 fails gracefully thanks to nested try-catch
			
			try{
			Field wcAdhoc = WifiConfiguration.class.getField("adhocSSID");
			Field wcAdhocFreq = WifiConfiguration.class.getField("frequency");
			wcAdhoc.setBoolean(selectedConfig, prefs.getBoolean(WiFiACESettings.PREF_ADHOC,
					false));
			int freq = Integer.parseInt(prefs.getString(WiFiACESettings.PREF_ADHOC_FREQUENCY,
					"2462")); 	// default to channel 11
			//System.err.println(freq);
			wcAdhocFreq.setInt(selectedConfig, freq); 
			} catch (Exception e){
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// FIXME As above, what should I do here?
			e.printStackTrace();
		}

		wifiManager.updateNetwork(selectedConfig);
		wifiManager.enableNetwork(selectedConfig.networkId, false);
		wifiManager.saveConfiguration();
		
	}

	static String removeQuotes(String str) {
		int len = str.length();
		if ((len > 1) && (str.charAt(0) == '"') && (str.charAt(len - 1) == '"')) {
			return str.substring(1, len - 1);
		}
		return str;
	}

	static String surroundWithQuotes(String string) {
		return "\"" + string + "\"";
	}
}