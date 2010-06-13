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

import com.firegnom.rat.ExceptionActivity;

// TODO: Auto-generated Javadoc
/**
 * The Class Exception.
 */
public class Exception extends ExceptionActivity {

	/* (non-Javadoc)
	 * @see com.firegnom.rat.ExceptionActivity#getMessage()
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.rat.ExceptionActivity#getMoreDetails()
	 */
	@Override
	public String getMoreDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.firegnom.rat.ExceptionActivity#getSecurityToken()
	 */
	@Override
	public String getSecurityToken() {
		return "9823745653213099872193";
	}

	/* (non-Javadoc)
	 * @see com.firegnom.rat.ExceptionActivity#getUrl()
	 */
	@Override
	public String getUrl() {
		return "http://valkyrie.firegnom.com/exceptions/post.php";
	}

	/* (non-Javadoc)
	 * @see com.firegnom.rat.ExceptionActivity#isSend()
	 */
	@Override
	public boolean isSend() {
		return true;
	}

}
