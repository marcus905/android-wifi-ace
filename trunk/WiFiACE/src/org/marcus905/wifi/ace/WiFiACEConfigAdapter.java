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

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WiFiACEConfigAdapter extends ArrayAdapter<WifiConfiguration> {

	int resource;

	public WiFiACEConfigAdapter(Context context, int textViewResourceId,
			List<WifiConfiguration> objects) {
		super(context, textViewResourceId, objects);

		resource = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout wifiConfigView;

		WifiConfiguration config = getItem(position);

		String ssid = config.SSID;
		StringBuffer sbuf = new StringBuffer();
		for (int p = 0; p < config.allowedKeyManagement.size(); p++) {
			if (config.allowedKeyManagement.get(p)) {
				sbuf.append(" ");
				if (p < KeyMgmt.strings.length) {
					sbuf.append(KeyMgmt.strings[p]);
				} else {
					sbuf.append("??");
				}
			}
		}
		String protocols = sbuf.toString();

		if (convertView == null) {
			wifiConfigView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					inflater);
			vi.inflate(resource, wifiConfigView, true);
		} else {
			wifiConfigView = (LinearLayout) convertView;
		}

		TextView ssidView = (TextView) wifiConfigView
				.findViewById(R.id.ssid_name);
		TextView descView = (TextView) wifiConfigView
				.findViewById(R.id.wifi_desc);

		ssidView.setText(ssid.substring(1, ssid.length() - 1).trim());
		descView.setText(protocols.trim());

		return wifiConfigView;
	}
}
