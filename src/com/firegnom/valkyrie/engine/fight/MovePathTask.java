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
//package com.firegnom.valkyrie.engine.fight;
//
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import com.firegnom.valkyrie.engine.GameController;
//import com.firegnom.valkyrie.engine.Player;
//import com.firegnom.valkyrie.graphics.Animation;
//import com.firegnom.valkyrie.graphics.DirectionalAnimation;
//import com.firegnom.valkyrie.map.pathfinding.Path;
//
//public class MovePathTask implements Runnable{
//  Animation pl ;
//  ScheduledThreadPoolExecutor e ;
//  public MovePathTask(Animation pl,ScheduledThreadPoolExecutor e) {
//    this.pl = pl;
//    this.e = e;
//  }
//  @Override
//  public void run() {
//    pl.nextFrame();
//    GameController.getInstance().fightController.postInvalidate(pl);
//    e.schedule(this, 100, TimeUnit.MILLISECONDS);
//  }
//
// }
