/**
 * 
 * Copyright 2014 Noisy Flowers LLC
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
 * HeightGapFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.activities.SegmentActivity;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Transect;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;
import com.noisyflowers.rangelandhealthmonitor.android.util.MinMaxTextWatcher;
import com.noisyflowers.rangelandhealthmonitor.android.util.NicelyToastedCheckBox;
import com.noisyflowers.rangelandhealthmonitor.android.util.NicelyToastedRadioButton;
import com.noisyflowers.rangelandhealthmonitor.android.util.PersistenceFragment;

public class HeightGapFragment extends Fragment implements IHelp, PersistenceFragment, OnClickListener {
		
	private RadioGroup heightRG1, heightRG2, basalGapRG, canopyGapRG;
	private Button submitSpecies;
	private TextView speciesListTV, woodySpeciesTV, nonwoodySpeciesTV;
	private AutoCompleteTextView speciesNameTV;
	private EditText woodySpeciesCountET1, nonwoodySpeciesCountET2;
	ViewGroup speciesViewGroup;
	
	private ArrayList<String> heightChoices = new ArrayList<String>();
	private List<String> speciesList = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_height_gap, container, false);
    
        heightRG1 = (RadioGroup) view.findViewById(R.id.fragment_heightGapSpecies_heightRG01);
        heightRG2 = (RadioGroup) view.findViewById(R.id.fragment_heightGapSpecies_heightRG02);
		heightRG1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
		heightRG2.clearCheck();

		basalGapRG = (RadioGroup) view.findViewById(R.id.fragment_heightGapSpecies_basalGapRG);
		canopyGapRG = (RadioGroup) view.findViewById(R.id.fragment_heightGapSpecies_canopyGapRG);
		basalGapRG.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
		canopyGapRG.clearCheck();
		
		speciesViewGroup = 	(ViewGroup)view.findViewById(R.id.fragment_heightGapSpecies_speciesCountArea);

		woodySpeciesCountET1 = (EditText) view.findViewById(R.id.fragment_heightGapSpecies_speciesDensityET_1);
		nonwoodySpeciesCountET2 = (EditText) view.findViewById(R.id.fragment_heightGapSpecies_speciesDensityET_2);

		woodySpeciesTV = (TextView) view.findViewById(R.id.fragment_heightGapSpecies_speciesDensityTV_1);
		nonwoodySpeciesTV = (TextView) view.findViewById(R.id.fragment_heightGapSpecies_speciesDensityTV_2);
		Transect transect = ((SegmentActivity)getActivity()).transect;
		if (transect != null) {
			if (transect.dominantWoodySpecies != null && !"".equals(transect.dominantWoodySpecies.toString())) {
				view.findViewById(R.id.fragment_heightGapSpecies_speciesNotEnteredTV).setVisibility(View.GONE);
				speciesViewGroup.setVisibility(View.VISIBLE);
				woodySpeciesTV.setText(transect.dominantWoodySpecies);
				woodySpeciesCountET1.addTextChangedListener(new MinMaxTextWatcher(0, 999));
			}
			if (transect.dominantNonwoodySpecies != null && !"".equals(transect.dominantNonwoodySpecies.toString())) {
				view.findViewById(R.id.fragment_heightGapSpecies_speciesNotEnteredTV).setVisibility(View.GONE);
				speciesViewGroup.setVisibility(View.VISIBLE);
				nonwoodySpeciesTV.setText(transect.dominantNonwoodySpecies);
				nonwoodySpeciesCountET2.addTextChangedListener(new MinMaxTextWatcher(0, 999));
			}
		}

		for (int i = 0; i < heightRG1.getChildCount(); i++) {
			View v = heightRG1.getChildAt(i);
			if (v instanceof RadioButton) {
				heightChoices.add((String)v.getTag());
			}
		}
		for (int i = 0; i < heightRG2.getChildCount(); i++) {
			View v = heightRG2.getChildAt(i);
			if (v instanceof RadioButton) {
				heightChoices.add((String)v.getTag());
			}
		}
        
		load(((SegmentActivity)getActivity()).segment);
		
		if (((SegmentActivity)getActivity()).date != null) {
			RHMApplication.getInstance().setViewGroupEnabled((ViewGroup)view, false);
		}

        return view;
	}

	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		heightRG1.setOnCheckedChangeListener(null);
		heightRG2.setOnCheckedChangeListener(null);			
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//Waiting to add these here prevents Toast popping on load.  Still waiting to see if there are side-effects. 
		heightRG1.setOnCheckedChangeListener(heightListener1);
		heightRG2.setOnCheckedChangeListener(heightListener2);			
	}
	
	private OnCheckedChangeListener heightListener1 = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
            	heightRG2.setOnCheckedChangeListener(null);
            	heightRG2.clearCheck();
            	heightRG2.setOnCheckedChangeListener(heightListener2);
            }
        }
    };

    private OnCheckedChangeListener heightListener2 = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
            	heightRG1.setOnCheckedChangeListener(null);
            	heightRG1.clearCheck();
            	heightRG1.setOnCheckedChangeListener(heightListener1);
            }
        }
    };

	@Override
	public void load(Segment segment) {
		heightRG1.clearCheck();
		heightRG2.clearCheck();
		basalGapRG.clearCheck();
		canopyGapRG.clearCheck();
		//plantBaseUnderStickCB.setChecked(false); //TODO: necessary?
		//plantCanopyOverStickCB.setChecked(false); //TODO: necessary?

		String stringValue = null;
		try {
			//stringValue = segment.canopyHeight == null ? null : Height.valueOf(segment.canopyHeight).getDisplayName();
			stringValue = segment.canopyHeight == null ? null : segment.canopyHeight.getDisplayName();
		} catch (IllegalArgumentException iAE) {} //TODO: ignore?
		RadioButton rB = null;
		if (stringValue != null) {
			for (String heightChoice : heightChoices) {
				if (heightChoice.equals(stringValue)) {
					rB = (RadioButton) heightRG1.findViewWithTag(heightChoice);
					if (rB == null) {
						rB = (RadioButton) heightRG2.findViewWithTag(heightChoice);
					}
					break;
				}
			}
		}
		if (rB != null) rB.setChecked(true);
		
		rB = null;
		if (segment.basalGap != null) {
			if (segment.basalGap) {
				rB = (RadioButton) basalGapRG.findViewWithTag(getString(R.string.fragment_heightGapSpecies_basalGap_yes));
			} else {
				rB = (RadioButton) basalGapRG.findViewWithTag(getString(R.string.fragment_heightGapSpecies_basalGap_no));
			}
		}
		if (rB != null) rB.setChecked(true);		
		
		rB = null;
		if (segment.canopyGap != null) {
			if (segment.canopyGap) {
				rB = (RadioButton) canopyGapRG.findViewWithTag(getString(R.string.fragment_heightGapSpecies_canopyGap_yes));
			} else {
				rB = (RadioButton) canopyGapRG.findViewWithTag(getString(R.string.fragment_heightGapSpecies_canopyGap_no));
			}
		}
		if (rB != null) rB.setChecked(true);

		woodySpeciesCountET1.setText(segment.woodySpeciesCount == null ? null : ""+segment.woodySpeciesCount); //necessary to clarify signature
		nonwoodySpeciesCountET2.setText(segment.nonwoodySpeciesCount == null ? null : ""+segment.nonwoodySpeciesCount);
		
		//This is to display counts for transect data that predates woody/nonwoody (1.1)
		if (segment.woodySpeciesCount != null || segment.nonwoodySpeciesCount != null) {
			speciesViewGroup.setVisibility(View.VISIBLE);			
		}
		
	}

	private String getCheckedHeight() {		
		String retVal = null;
		int chkID = heightRG1.getCheckedRadioButtonId();
		if (chkID != -1) {
			retVal = (String)heightRG1.findViewById(chkID).getTag();
		} else {
			chkID = heightRG2.getCheckedRadioButtonId();
			if (chkID != -1) {
				retVal = (String)heightRG2.findViewById(chkID).getTag();
			}
		}
		return retVal;
	}


	@Override
	public Segment save(Segment segment) {
		NicelyToastedRadioButton.cancelToast();
		NicelyToastedCheckBox.cancelToast();

		String checkedHeight = getCheckedHeight();
		//segment.canopyHeight = checkedHeight == null ? null : Height.displayNameLookup.get(checkedHeight).name();
		segment.canopyHeight = checkedHeight == null ? null : Segment.Height.displayNameLookup.get(checkedHeight);
		
		//segment.basalGap = basalGapRG.getCheckedRadioButtonId() == R.id.fragment_heightGapSpecies_basalGapRG_yesRB;
		//segment.canopyGap = canopyGapRG.getCheckedRadioButtonId() == R.id.fragment_heightGapSpecies_canopyGapRG_yesRB;
		long id = basalGapRG.getCheckedRadioButtonId();
		segment.basalGap = id == -1 ? null : id == R.id.fragment_heightGapSpecies_basalGapRG_yesRB;
		id = canopyGapRG.getCheckedRadioButtonId();
		segment.canopyGap = id == -1 ? null : id == R.id.fragment_heightGapSpecies_canopyGapRG_yesRB;
		
		try {segment.woodySpeciesCount = Integer.parseInt(woodySpeciesCountET1.getText().toString());} catch(Exception eX) {segment.woodySpeciesCount = null;}
		try {segment.nonwoodySpeciesCount = Integer.parseInt(nonwoodySpeciesCountET2.getText().toString());} catch(Exception eX) {segment.nonwoodySpeciesCount = null;}
				
		segment.speciesList = speciesList;
		
		return segment;	
	}


	@Override
	public boolean isComplete(Segment segment) {
		boolean retVal = false;
		retVal = segment.canopyHeight != null;
		
		retVal = retVal && segment.basalGap != null;
		retVal = retVal && segment.canopyGap != null;
		
		retVal = retVal && segment.woodySpeciesCount != null;
		retVal = retVal && segment.nonwoodySpeciesCount != null;
		
		return retVal;
	}


	@Override
	public void onClick(View v) {
		String speciesName = speciesNameTV.getText().toString();
		if (speciesName == null || "".equals(speciesName)) {
			//return;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("You must enter a species name.")
				   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					   @Override
					   public void onClick(DialogInterface dialog, int which) {
					   }
				   });
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
		speciesNameTV.setText(null);
		if (!speciesList.contains(speciesName)) {
			speciesList.add(speciesName);
			String speciesListStr = speciesListTV.getText().toString();
			speciesListStr = getString(R.string.fragment_heightGapSpecies_speciesList_dummy).equals(speciesListStr) ? speciesName : speciesListStr + "\n" + speciesName;
			speciesListTV.setText(speciesListStr);
		}
	}
	
	@Override
	public View getHelpView() {
		ScrollView sV = new ScrollView(getActivity());
		TextView tV = new TextView(getActivity());
		tV.setPadding(10,10,10,10);
		tV.setText(Html.fromHtml(getString(R.string.fragment_heightgapspecies_help)));
		sV.addView(tV);
		return sV;
	}

}
