/*
 * Copyright (c) 2008, R.Nagel <kiar@users.sourceforge.net>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the names of its contributors
 *       may be used to endorse or promote products derived from this
 *       software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT 
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY 
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Contributor(s):
 * 
 */

// created by : R.Nagel <kiar@users.sourceforge.net>, 07.03.2008
//
// function   : popupmenu (OpenedFilesList)
//
// todo       :
//
// modified   : 

package net.sf.multikulti.openeditorfiles;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * 
 */
public class ListPopup extends JPopupMenu
{
  private OpenedFilesListener service ;
  private Component parentComp ;
  
  private int selectedIndex ;
  private int clickedIndex ;

  public ListPopup(OpenedFilesListener model, Component parent)
  {
    super();
    
    service = model ;
    parentComp = parent ;
  }
  
  public final void loadActions()
  {
    // need wrapper actions
    Lookup lkp = Lookups.forPath("OFL/Menus/Popup/sensitive");

    for (Action a : lkp.lookupAll(Action.class))
    {
      add( new WrapperAction(a) ) ;
    }   
    
    //this.addSeparator();
    
    lkp = Lookups.forPath("OFL/Menus/Popup/content");

    for (Action a : lkp.lookupAll(Action.class))
    {
      add(a) ;
    }   
  }
  
  public void show( Point p, int clickedIndex, int selectedIndex)
  {
    this.clickedIndex = clickedIndex ;
    this.selectedIndex = selectedIndex ;
    
    // check, if the selected item into the list and the selected topcomponent
    // into the editor window are the same
    
    super.show(parentComp, p.x, p.y);
  }

  public int getClickedIndex()
  {
    return clickedIndex;
  }

  public int getSelectedIndex()
  {
    return selectedIndex;
  }

  public OpenedFilesListener getService()
  {
    return service;
  }
  
  
  // --------------------------------------------------------------------------
  private class WrapperAction extends AbstractAction
  {
    private Action orgAction ;
    
    public WrapperAction( Action org )
    {
      super() ;
      
      putValue(Action.NAME, org.getValue(Action.NAME)) ;
      orgAction = org ;
    }
    
    public void actionPerformed(ActionEvent e)
    {
//      if (clickedIndex != selectedIndex)
      {
        // bring it to front 
        service.bringToFront(clickedIndex) ;
      }
      orgAction.actionPerformed(e) ;
    }
  }
  
}
