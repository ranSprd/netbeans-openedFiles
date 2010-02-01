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
// function   : model item (ListModel), contains a single TopComponent
//
// todo       :
//
// modified   : 


package net.sf.multikulti.openeditorfiles;

import org.openide.windows.TopComponent;

/**
 * bla
 */
public class OpenedListItem 
{
  private TopComponent topComp;
  private long lastActive = 0;

  public OpenedListItem(TopComponent topComp)
  {
    this.topComp = topComp;
  }

  public TopComponent getTopComponent()
  {
    return topComp;
  }

  public void setTopComponent(TopComponent topComp)
  {
    this.topComp = topComp;
  }

  /** check, if there is the same topcomponent */
  public final boolean isEqualTopComponent(TopComponent other)
  {
    if ((other != null) && (topComp != null))
    {
      return (other.hashCode() == topComp.hashCode()) ;
    }
    
    return false ;
  }


  /** log activation of this component */
  public void logActive() {
    lastActive = System.currentTimeMillis();
  }

  /** last activation time */
  public long getLastActive() {
      return lastActive;
  }


  
  /** bring the component to the front */
  public void bringToFront()
  {
    if (topComp != null)
    {
      if (topComp.isOpened())
      {
        topComp.requestActive(); 
      }
    }
  }
  
  public boolean isOpen()
  {
    if (topComp != null)
    {
      return topComp.isOpened() ;
    }
    
    return false ;
  }

}
