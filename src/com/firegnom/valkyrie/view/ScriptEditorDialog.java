/*******************************************************************************
 * Copyright (c) 2010 Maciej Kaniewski (mk@firegnom.com).
 * 
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 * 
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software Foundation,
 *    Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * 
 *    Contributors:
 *     Maciej Kaniewski (mk@firegnom.com) - initial API and implementation
 ******************************************************************************/
package com.firegnom.valkyrie.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firegnom.valkyrie.R;
import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.map.Position;

// TODO: Auto-generated Javadoc
/**
 * The Class ScriptEditorDialog.
 */
public class ScriptEditorDialog extends Dialog {

	/** The text. */
	EditText text;
	
	/** The spinner. */
	Spinner spinner;

	/* (non-Javadoc)
	 * @see android.app.Dialog#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			GameController sc = GameController.getInstance();
			Position eventScreenPosition = sc.longPressPosition;
			if (eventScreenPosition != null) {
				Position eventMapPosition = sc.zone.getMapPosition(
						eventScreenPosition.x, eventScreenPosition.y, sc.sX,
						sc.sY);
				Log.d("ScriptEditorDialog", eventMapPosition.toString());
				// "pos:m("+eventMapPosition.x+","+eventMapPosition.y+"),s("+eventScreenPosition.x+","+eventScreenPosition.y+")"
				setTitle("pos:m(" + eventMapPosition.x + ","
						+ eventMapPosition.y + "),s(" + eventScreenPosition.x
						+ "," + eventScreenPosition.y + ")");
			}
		}
	}

	/**
	 * Instantiates a new script editor dialog.
	 *
	 * @param context the context
	 */
	public ScriptEditorDialog(Context context) {
		super(context);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.script_editor);

		spinner = (Spinner) findViewById(R.id.script_editor_spinner);
		text = (EditText) findViewById(R.id.script_editor_text);
		Button exec = (Button) findViewById(R.id.script_editor_execute_button);
		exec.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String src = text.getEditableText().toString();
				dismiss();
				GameController.getInstance().parser.parse(src);
			}
		});
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (text == null || arg1 == null) {
					return;
				}
				text.append(arg1.getResources().getStringArray(
						R.array.script_editor_defaults_text)[arg2]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
		Button cancel = (Button) findViewById(R.id.script_editor_cancel_button);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel();
			}
		});
	}

}
