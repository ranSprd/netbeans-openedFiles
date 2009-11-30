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
// function   : list renderer
//
// todo       : NOT USED (now), proof of concept, experimentel
//
// modified   : 


package net.sf.multikulti.openeditorfiles;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.openide.windows.TopComponent;

/**
 * bla
 */
public class OpenFilesListRenderer extends JLabel implements ListCellRenderer
{
//  private ListCellRenderer defaultRenderer ;
//  public OpenFilesListRenderer(ListCellRenderer renderer)
//  {
//    defaultRenderer = renderer ;
//  }
  
  public Component getListCellRendererComponent(JList list,
                                                 Object value,
                                                 int index,
                                                 boolean isSelected,
                                                 boolean cellHasFocus)
  {
    if (value != null)
    {
      if (value instanceof TopComponent)
      {
        TopComponent topComp = (TopComponent) value ;
        String back = topComp.getHtmlDisplayName();
        if (back == null)
        {
          back = topComp.getDisplayName();
          if (back == null)
          {
            back = topComp.getName();
            if (back == null)
            {
              back = "unresolved [" + topComp.toString() + "]";
            }
          }
        }
        this.setText(back);
        this.setIcon( new ImageIcon( topComp.getIcon()) ) ;
      }
      else // no TopComponent
      {
        this.setText(value.toString());
      }
    }
    else
    {
      this.setText("?");
    }
    
    if (isSelected)
    {
      //Component comp = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    }
    else
    {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    
    
    
    return this ;

  }
}
