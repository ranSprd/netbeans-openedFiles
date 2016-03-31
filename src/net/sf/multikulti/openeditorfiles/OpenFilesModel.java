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
// function   : model, contains the TopCompoment's
//
// todo       :
//
// modified   : 
package net.sf.multikulti.openeditorfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractListModel;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Contains the ListModel
 */
public class OpenFilesModel extends AbstractListModel {

    // a pre-computed list of open windows
    private ArrayList<TopComponent> preList = new ArrayList<TopComponent>();

    // the list which is used for the view
    private ArrayList<OpenedListItem> modelList = new ArrayList<OpenedListItem>();

    private ArrayList<OpenedListItem> closeableList = new ArrayList<OpenedListItem>();

    private Mode editorMode = null;

    public final Mode findEditorMode() {
        try {
            // get a set of all available modes
            Set<? extends Mode> modes = WindowManager.getDefault().getModes();

            // make it robust
            if (modes != null) {
                for (Mode mode : modes) {
                    if (mode != null) {
                        String dummy = mode.getName();
                        if (dummy != null) {
                            // we need only the <editor> mode
                            if ("editor".equals(dummy)) {
                                return editorMode = mode;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) // if any error occurs, throws null
        {
        }
        return editorMode = null;
    }

    // ----------------------
    public final void markActive(TopComponent topComp) {
        OpenedListItem item = findItem(topComp);
        if (item != null) {
            item.logActive();
        }
    }

    public final void readOpenedWindows() {
        try {
            preList.clear();

            if (editorMode != null) {
                TopComponent comps[] = editorMode.getTopComponents();

                if (comps != null) {
                    for (TopComponent single : comps) {
                        if (single != null) {
                            if (single.isOpened()) {
                                preList.add(single);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * import the preList and make it to the model
     */
    public final void updateModel() {

//        //invalidate all items
//        for(OpenedListItem item : modelList) {
//            item.setValid(false);
//        }
        // prepare the current open items
        ArrayList<OpenedListItem> tempList = new ArrayList<OpenedListItem>();
        for (TopComponent component : preList) {
            OpenedListItem item = findItem(component);

            // new: create a new Item and log its activity
            if (item == null) {
                item = new OpenedListItem(component);
                item.logActive();
            }
            item.setValid(true);
            tempList.add(item);
        }

        // sort by usage 
        Collections.sort(tempList);

        int mostActive = tempList.size();
        if (mostActive > 500) {
            mostActive = 500;
        }

        // add the first (most active) x items into the current model
        modelList.clear();
        for (int t = 0; t < mostActive; t++) {
            modelList.add(tempList.get(t));
        }

        // refresh the list of closeable components 
        closeableList.clear();
        for (int t = mostActive, len = tempList.size(); t < len; t++) {
            closeableList.add(tempList.get(t));
        }

        // do some other NO AWT stuff here
    }

    public final TopComponent getSelectedTopComponent() {
        if (editorMode == null) {
            editorMode = findEditorMode();
        }

        if (editorMode != null) {
            return editorMode.getSelectedTopComponent();
        }

        return null;

    }

    public final void fireUpdate() {
        this.fireIntervalAdded(this, 0, modelList.size());
    }

    public int getSize() {
        return modelList.size();
    }

//  use this methode, if a renderer is installed  
//  public Object getElementAt(int index) throws IndexOutOfBoundsException
//  {
//    String back = "<error>";
//    try
//    {
//      OpenedListItem item = ofList.get(index);
//      if (item != null)
//      {
//        return item.topComp ;
//      }
//    }
//    catch (Exception e)
//    {
//
//    }
//
//    return back;
//  }
    // no custom renderer method
    public Object getElementAt(int index) throws IndexOutOfBoundsException {
        String back = "<error>";
        try {
            OpenedListItem item = modelList.get(index);
            if (item != null) {
                TopComponent topComp = item.getTopComponent();
                if (topComp != null) {
                    back = topComp.getHtmlDisplayName();
                    if (back == null) {
                        back = topComp.getDisplayName();
                        if (back == null) {
                            back = topComp.getName();
                            if (back == null) {
                                back = "unresolved [" + topComp.toString() + "]";
                            }
                        }
                    }
                }
//        else
//        {
//          back = "unresolved" ;
//        }
            }
        } catch (Exception e) {
        }

        return back;
    }

    /**
     * returns the Listitem at ofList index <index> or null if something is
     * wrong...
     */
    public OpenedListItem getItem(int index) {
        OpenedListItem back = null;
        if (index >= 0) {
            try {
                back = modelList.get(index);
            } catch (Exception e) {
                back = null;
            }
        }

        return back;
    }

    public final OpenedListItem findItem(TopComponent topComp) {
        if (topComp == null) {
            return null;
        }

        for (OpenedListItem item : modelList) {
            if (item.isEqualTopComponent(topComp)) {
                return item;
            }
        }

        return null;
    }

    public List<OpenedListItem> getCloseableList() {
        return closeableList;
    }

}
//  some code examples
//
//
//      jTextArea1.setText( clickCounter +"\n") ;
//      
//      Set<? extends TopComponent> opened = WindowManager.getDefault().getRegistry().getOpened() ;
//      if (opened != null)
//      {
//        for( TopComponent single : opened)
//        {
//                if (single != null)
//                {
//                  jTextArea1.append("Component [" +single.getName() +"]\n") ;
//                }
//                else
//                {
//                  jTextArea1.append(" leer2\n") ;
//                }
//        }
//      }
//      else
//      {
//        jTextArea1.append("Nichts offenes gefunden!\n");
//      }
//      
//      jTextArea1.append("----------------------\n") ;
//      
//      Set<? extends Mode> modes = WindowManager.getDefault().getModes() ;
//      if (modes != null)
//      {
//        for(Mode mode : modes)
//        {
//          if (mode != null)
//          {
//            jTextArea1.append("Mode [" +mode.getName() +"]\n") ;
//          
//            TopComponent comps[] = mode.getTopComponents() ;
//            if (comps != null)
//            {
//              jTextArea1.append(" found " +comps.length +"\n") ;
//              
//              for( TopComponent single : comps )
//              {
//                if (single != null)
//                {
//                  jTextArea1.append(" - Component [" +single.getName() +"]\n") ;
//                }
//                else
//                {
//                  jTextArea1.append(" - leer2!!\n") ;
//                }
//              }
//            }
//            else
//            {
//              jTextArea1.append(" leer\n") ;
//            }
//          }
//          else
//          {
//            jTextArea1.setText("leerer Mode\n") ;
//          }
//        }
//      }
//      else
//      {
//        jTextArea1.setText("kein Mode gefunden!\n");
//      }

