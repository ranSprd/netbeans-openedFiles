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

// name       : AbstractListPopupAction.java
//
// created by : R.Nagel <kiar@users.sourceforge.net>, 07.03.2008
//
// function   : base action for the OpenedList popupmenu
//
// todo       :
//
// modified   : 

package net.sf.multikulti.openeditorfiles.actions;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.event.PopupMenuListener;
import net.sf.multikulti.openeditorfiles.ListPopup;
import net.sf.multikulti.openeditorfiles.OpenedFilesListener;

/**
 * an action for the ListPopup
 */
public abstract class AbstractListPopupAction extends AbstractAction implements
        PopupMenuListener
{
  private ListPopup popupParent = null;

  public AbstractListPopupAction(String name, Icon icon)
  {
    super(name, icon);
  }

  public AbstractListPopupAction(String name)
  {
    super(name);
  }

  public AbstractListPopupAction()
  {
    super();
  }
  
  /** register the popup and add this action into the view */
  public void register(ListPopup popup)
  {
    if (popup != null)
    {
      popupParent = popup;
      popupParent.add(this);
      popupParent.addPopupMenuListener(this);
    }
  }
  
  public int getSelectedIndex()
  {
    return popupParent.getSelectedIndex();
  }

  public int getClickedIndex()
  {
    return popupParent.getClickedIndex();
  }

  public OpenedFilesListener getService()
  {
    return popupParent.getService();
  }
}
