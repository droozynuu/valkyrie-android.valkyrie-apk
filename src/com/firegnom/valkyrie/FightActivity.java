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
package com.firegnom.valkyrie;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.firegnom.valkyrie.common.Dir;
import com.firegnom.valkyrie.engine.GameController;
import com.firegnom.valkyrie.engine.fight.NotImplementedAction;
import com.firegnom.valkyrie.engine.fight.RunAwayAction;
import com.firegnom.valkyrie.engine.fight.WalkAction;
import com.firegnom.valkyrie.view.FightView;

public class FightActivity extends ValkyrieActivity implements
		OnGestureListener {

	public ProgressBar progress;
	public static final int progressHeight = 5;
	public static final String PLAYER_EXTRA = "PLAYER_EXTRA";
	private String enemyName;
	public Button walkB;

	private GestureDetector gestureScanner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent i = getIntent();
		if (i.getExtras().containsKey(PLAYER_EXTRA)) {
			enemyName = i.getExtras().getString(PLAYER_EXTRA);
		}
		gestureScanner = new GestureDetector(this);
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		progress = new ProgressBar(this.getApplicationContext(), null,
				android.R.attr.progressBarStyleHorizontal);
		progress.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				progressHeight));
		progress.setHorizontalScrollBarEnabled(true);
		progress.setIndeterminate(false);
		progress.setProgress(1000);
		progress.setMax(1000);

		ll.addView(progress);
		View m = new FightView(this.getApplicationContext());

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		int width = dm.widthPixels;

		m.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height
				- 55 + progressHeight));
		LinearLayout ll1 = new LinearLayout(this);
		ll1.setOrientation(LinearLayout.HORIZONTAL);
		ll1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		Button b = new Button(this);
		b.setLayoutParams(new LayoutParams(50, 50));
		b.setBackgroundResource(R.drawable.attack);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameController.getInstance().fightController
						.changeAction(new NotImplementedAction());
			}
		});
		ll1.addView(b);
		walkB = new Button(this);
		walkB.setLayoutParams(new LayoutParams(50, 50));
		walkB.setBackgroundResource(R.drawable.walk);
		// b1.setText("W");
		walkB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameController.getInstance().fightController
						.changeAction(new WalkAction());
			}
		});
		ll1.addView(walkB);
		Button b2 = new Button(this);
		b2.setLayoutParams(new LayoutParams(50, 50));
		b2.setBackgroundResource(R.drawable.magic);
		b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameController.getInstance().fightController
						.changeAction(new NotImplementedAction());
			}
		});
		ll1.addView(b2);
		Button b4 = new Button(this);
		b4.setLayoutParams(new LayoutParams(50, 50));
		b4.setBackgroundResource(R.drawable.inventory);
		b4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameController.getInstance().fightController
						.changeAction(new NotImplementedAction());
			}
		});
		ll1.addView(b4);
		Button b3 = new Button(this);
		b3.setLayoutParams(new LayoutParams(50, 50));
		b3.setBackgroundResource(R.drawable.exit);
		b3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameController.getInstance().fightController
						.changeAction(new RunAwayAction());
			}
		});
		ll1.addView(b3);

		ll.addView(m);
		ll.addView(ll1);
		setContentView(ll);

		GameController.getInstance().fightController.connect(m, this);
		GameController.getInstance().fightController.init();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureScanner.onTouchEvent(event);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return GameController.getInstance().fightController.doScroll(distanceX,
				distanceY);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return GameController.getInstance().fightController.onSingleTapUp(e);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		GameController gc = GameController.getInstance();
		gc.user.fightMode = true;
		gc.fightController.enemy.fightMode = true;
		gc.fightController.enemy.fightPosition.setXY(7, 7);
		if (gc.fightController.enemy.animation == null) {
			gc.fightController.enemy.setPlayerClass(gc.fightController.enemy
					.getPlayerClass());
		}
		gc.fightController.enemy.animation.changeDir(Dir.W);

		super.onResume();
	}

}
