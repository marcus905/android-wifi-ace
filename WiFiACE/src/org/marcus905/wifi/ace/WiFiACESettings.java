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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class WiFiACESettings extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {
	
	public static final String PREF_SSID = "SSID";
	public static final String PREF_BSSID = "BSSID";
	public static final String PREF_HIDDEN_SSID = "HIDDEN_SSID";
	public static final String PREF_ADHOC = "ADHOC";
	public static final String PREF_ADHOC_FREQUENCY = "ADHOC_FREQUENCY";

	public static final String PREF_KEY_NONE = "KEY_NONE";
	public static final String PREF_KEY_PSK = "KEY_PSK";
	public static final String PREF_KEY_EAP = "KEY_EAP";
	public static final String PREF_KEY_IEEE = "KEY_IEEE";

	public static final String PREF_AUTH_OPEN = "AUTH_OPEN";
	public static final String PREF_AUTH_LEAP = "AUTH_LEAP";
	public static final String PREF_AUTH_SHARED = "AUTH_SHARED";

	public static final String PREF_GRP_WEP40 = "GRP_WEP40";
	public static final String PREF_GRP_WEP104 = "GRP_WEP104";
	public static final String PREF_GRP_TKIP = "GRP_TKIP";
	public static final String PREF_GRP_CCMP = "GRP_CCMP";

	public static final String PREF_PAIR_NONE = "PAIR_NONE";
	public static final String PREF_PAIR_CCMP = "PAIR_CCMP";
	public static final String PREF_PAIR_TKIP = "PAIR_TKIP";

	public static final String PREF_SEC_WPA = "SEC_WPA";
	public static final String PREF_SEC_RSN = "SEC_RSN";

	public static final String PREF_WPA_KEY = "WPA_KEY";

	public static final String PREF_WEPKEY_IDX = "KEY_IDX";
	public static final String PREF_WEPKEY_KEY0 = "KEY_KEY0";
	public static final String PREF_WEPKEY_KEY1 = "KEY_KEY1";
	public static final String PREF_WEPKEY_KEY2 = "KEY_KEY2";
	public static final String PREF_WEPKEY_KEY3 = "KEY_KEY3";

	public static final String PREF_ENTERPRISE_EAP = "ENTERPRISE_EAP";
	public static final String PREF_ENTERPRISE_PHASE2 = "ENTERPRISE_PHASE2";
	public static final String PREF_ENTERPRISE_IDENT = "ENTERPRISE_IDENT";
	public static final String PREF_ENTERPRISE_ANON_IDENT = "ENTERPRISE_ANON_IDENT";
	public static final String PREF_ENTERPRISE_PASS = "ENTERPRISE_PASS";
	public static final String PREF_ENTERPRISE_CLIENT_CERT = "ENTERPRISE_CLIENT_CERT";
	public static final String PREF_ENTERPRISE_CA_CERT = "ENTERPRISE_CA_CERT";
	public static final String PREF_ENTERPRISE_PRIV_KEY = "ENTERPRISE_PRIV_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
		

		Preference e = findPreference(PREF_SSID);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_SSID, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_BSSID);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_BSSID, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_WPA_KEY);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_WPA_KEY, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_WEPKEY_KEY0);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
					PREF_WEPKEY_KEY0, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_WEPKEY_KEY1);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
					PREF_WEPKEY_KEY1, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_WEPKEY_KEY2);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
					PREF_WEPKEY_KEY2, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_WEPKEY_KEY3);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
					PREF_WEPKEY_KEY3, null));
		e.setOnPreferenceChangeListener(this);

		// Enterprise Settings
		e = findPreference(PREF_ENTERPRISE_EAP);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_ENTERPRISE_EAP, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_ENTERPRISE_PHASE2);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_ENTERPRISE_PHASE2, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_ENTERPRISE_IDENT);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_ENTERPRISE_IDENT, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_ENTERPRISE_ANON_IDENT);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_ENTERPRISE_ANON_IDENT, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_ENTERPRISE_PASS);
		
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
					PREF_ENTERPRISE_PASS, "")
					.equals("") ? "" : "*"); //we hide it as it's a pw
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_ENTERPRISE_CLIENT_CERT);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_ENTERPRISE_CLIENT_CERT, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_ENTERPRISE_PRIV_KEY);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_ENTERPRISE_PRIV_KEY, null));
		e.setOnPreferenceChangeListener(this);

		e = findPreference(PREF_ENTERPRISE_CA_CERT);
		e.setSummary(getPreferenceScreen().getSharedPreferences().getString(
				PREF_ENTERPRISE_CA_CERT, null));
		e.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(PREF_SSID)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_BSSID)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_WEPKEY_KEY0)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_WEPKEY_KEY1)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_WEPKEY_KEY2)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_WEPKEY_KEY3)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_ENTERPRISE_EAP)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_ENTERPRISE_PHASE2)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_ENTERPRISE_IDENT)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_ENTERPRISE_PASS)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_ENTERPRISE_CLIENT_CERT)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_ENTERPRISE_CA_CERT)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_ENTERPRISE_PRIV_KEY)) {
			preference.setSummary((String) newValue);
			return true;
		} else if (preference.getKey().equals(PREF_ENTERPRISE_ANON_IDENT)) {
			preference.setSummary((String) newValue);
			return true;
		}
		return false;
	}
}
