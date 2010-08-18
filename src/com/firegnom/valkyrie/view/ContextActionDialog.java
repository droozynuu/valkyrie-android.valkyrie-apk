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

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firegnom.valkyrie.action.ContextAction;
import com.firegnom.valkyrie.engine.GameController;

public class ContextActionDialog extends Dialog {
	ArrayList<ContextAction> arrayList;
	ContextActionView contextActionView;

	public ContextActionDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setTitle("Avilable actions");
		contextActionView = new ContextActionView(context);
		contextActionView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				GameController sc = GameController.getInstance();
				ContextAction action = arrayList.get(arg2);
				switch (action.type) {
				case ContextAction.DEBUG_SCRIPT_EDITOR:
					// FIXME connect this call to action call show dialog using
					// handlers
					new ScriptEditorDialog(arg1.getContext()).show();
					dismiss();
					return;
				case ContextAction.SCRIPT_ACTION:
					sc.parser.parse(action.script);
					dismiss();
					return;
				case ContextAction.ACTION_MANAGER:
					dismiss();
					sc.actionManager.execute(action);
					return;
				default:
					// TODO do something :)
					dismiss();
					return;
				}

			}
		});
		setContentView(contextActionView);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			GameController gc = GameController.getInstance();
			arrayList = gc.zone.contextActions.get(gc.longPressPosition.x
					+ (-1 * gc.sX), gc.longPressPosition.y + (-1 * gc.sY), 50);
			arrayList.addAll(gc.players.getActions(gc.longPressPositionMap, 4));
			contextActionView.setAdapter(new MyListAdapter());
		}
	}

	public class MyListAdapter extends BaseAdapter {
		// Sample data set. children[i] contains the children (String[]) for
		// groups[i].

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return arrayList.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout ll = new LinearLayout(parent.getContext());
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);
			ll.setLayoutParams(lp);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// textView.setLayoutParams(lp);
			TextView textView = new TextView(parent.getContext());
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setTextSize(16);
			textView.setTextColor(Color.GREEN);
			textView.setPadding(36, 0, 0, 0);
			textView.setText(arrayList.get(position).name);
			TextView textView1 = new TextView(parent.getContext());

			textView1.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView1.setTextSize(10);
			textView1.setPadding(36, 0, 0, 0);
			textView1.setText(arrayList.get(position).container);
			ll.addView(textView);
			ll.addView(textView1);
			return ll;
		}

	}

}
