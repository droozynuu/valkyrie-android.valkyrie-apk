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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.firegnom.valkyrie.engine.GameController;

public class PackageLoaderActivity extends ValkyrieActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_packages);
		if (GameController.getInstance().packsDownloading) {
			startActivity(new Intent(PackageLoaderActivity.this,
					PackageDownloadActivity.class));
			finish();
			return;
		}
		((Button) findViewById(R.id.download_packages_button_no))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						GameController.getInstance().downloadPackages = false;
						GameController.getInstance().papksLoading = false;
						startActivity(new Intent(PackageLoaderActivity.this,
								GameActivity.class));
						finish();
					}
				});
		((Button) findViewById(R.id.download_packages_button_yes))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// PackageLoaderActivity.this.setContentView(R.layout.please_wait);
						startActivity(new Intent(PackageLoaderActivity.this,
								PackageDownloadActivity.class));
						finish();
					}
				});
	}
	//

}
