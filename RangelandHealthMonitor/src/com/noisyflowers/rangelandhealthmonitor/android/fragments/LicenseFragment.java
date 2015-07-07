/**
 * 
 * Copyright 2015 Noisy Flowers LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 * com.noisyflowers.rangelandhealthmonitor.android.fragments
 * LicenseFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import com.noisyflowers.rangelandhealthmonitor.android.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class LicenseFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_license, container, false);
		
		((TextView)root.findViewById(R.id.fragment_license_license_area)).setText(Html.fromHtml(getString(R.string.license)));
		//WebView webView = (WebView)root.findViewById(R.id.fragment_license_license_area);
		//webView.loadUrl("file:///android_asset/license.html");
		
		return root;
	}

}