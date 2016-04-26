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
package net.sf.openedfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Contains the ListModel
 */
public class OpenFilesModel {

    private static final Logger logger = Logger.getLogger(OpenFilesModel.class.getName());

    // the list which is used for the view
    private final ArrayList<OpenedListItem> modelList = new ArrayList<>();

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
                                return mode;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // if any error occurs, throws null
        }
        return null;
    }

    public List<TopComponent> getTCs() {
        List<TopComponent> result = new ArrayList<>();
        for (OpenedListItem item : modelList) {
            result.add(item.getTopComponent());
        }

        return result;
    }

    // ----------------------
    public final void logActivation(TopComponent topComp) {
        OpenedListItem item = findItem(topComp);
        if (item != null) {
            item.logActivation();
        }
    }

    public final List<TopComponent> readOpenedWindows() {
        List<TopComponent> result = new ArrayList<>();
        Mode editorMode = findEditorMode();
        try {
            if (editorMode != null) {
                TopComponent comps[] = editorMode.getTopComponents();

                if (comps != null) {
                    for (TopComponent single : comps) {
                        if (single != null && single.isOpened()) {
                            result.add(single);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "unable to read the list of open windows", e);
        }
        return result;
    }

    /**
     * import the preList and make it to the model
     */
    public final void updateModel(List<TopComponent> preList) {
        // prepare the current open items
        ArrayList<OpenedListItem> tempList = new ArrayList<>();
        for (TopComponent component : preList) {
            OpenedListItem item = findItem(component);

            // new: create a new Item and log its activity
            if (item == null) {
                item = new OpenedListItem(component);
                item.logActivation();
            }
            tempList.add(item);
        }

        // sort by last recent usage 
        Collections.sort(tempList, new Comparator<OpenedListItem>() {
                     @Override
                     public int compare(OpenedListItem o1, OpenedListItem o2) {
                         return (int) (o2.getLastActivation() - o1.getLastActivation());
                     }
                 });

        synchronized (modelList) {
            modelList.clear();
            modelList.addAll(tempList);
        }

        // do some other NO AWT stuff here
    }

    public final void updateModel() {
        List<TopComponent> openEditors = this.readOpenedWindows();
        this.updateModel(openEditors);
    }

    public final OpenedListItem findItem(TopComponent topComp) {
        if (topComp == null) {
            return null;
        }

        OpenedListItem temp = new OpenedListItem(topComp);
        for (OpenedListItem item : modelList) {
            if (item.equals(temp)) {
                return item;
            }
        }

        return null;
    }

}
