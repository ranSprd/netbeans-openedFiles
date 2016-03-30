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
// function   : listener for editor events and service provider for some...
//
// todo       :
//
// modified   : 
package net.sf.multikulti.openeditorfiles;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.DefaultListSelectionModel;
import javax.swing.SwingUtilities;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * bla
 */
public class OpenedFilesListener extends DefaultListSelectionModel
        implements PropertyChangeListener, Runnable {

    private final OpenFilesModel model = new OpenFilesModel();

    private final UpdateReadyTask task = new UpdateReadyTask();

    private final ModelUpdater noGuiTask = new ModelUpdater();

    private volatile boolean updateInProgress = false;

    private volatile int requestCounter = 0;

    public OpenedFilesListener() {
//    thread = new Thread(this) ;
//    thread = new TCReader() ;
        register();
    }

    /** put a callback into the WindowManager Registry */
    private void register() {
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(this);

        // DEBUG Output
//    WindowManager.getDefault().addPropertyChangeListener( new WindowStateListener() );
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt != null) {
            String name = evt.getPropertyName();
            if (name != null) {
                if ((name.equals(TopComponent.Registry.PROP_TC_OPENED))
                        || (name.equals(TopComponent.Registry.PROP_TC_CLOSED))) {
//          System.out.println("prop [" +evt.getPropertyName() +"] " ) ;
//          if (evt.getNewValue() instanceof TopComponent)
//          {
//            System.out.println("mode :"+evt.getNewValue() ) ;
//          }
                    update();
                } else if (name.equals(TopComponent.Registry.PROP_ACTIVATED)) {
                    final Object sender = evt.getNewValue();
                    if (sender instanceof TopComponent) {
                        model.markActive((TopComponent) sender);
                    }
                }
            }
        }
    }

    /** readOpenedWindows the listModel into a separate thread */
    public final void update() {
        if (!updateInProgress) {
            model.findEditorMode();

            // flag
            updateInProgress = true;

            // inform all listener
            for (UpdateListener listener : task.listenerList) {
                listener.updateStarting();
            }

            // read all TopComponent's in AWT thread context
            model.readOpenedWindows();

            // run a task for updating the model and compute some other stuff....
            RequestProcessor.getDefault().post(noGuiTask);
        } else {
            requestCounter++;
        }
    }

    // Task code
    public final void run() {
        // worker code
        try {
            model.readOpenedWindows();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        // gui readOpenedWindows code
        SwingUtilities.invokeLater(task);
    }

    public final OpenFilesModel getModel() {
        return model;
    }

    public final boolean isActivated(int index) {
        if (index >= 0) {
            // check, if the selected item into the list and the selected topcomponent
            // into the editor window are the same
            OpenedListItem item = model.getItem(index);
            if (item != null) {
                return item.isEqualTopComponent(model.getSelectedTopComponent());
            }
        }

        return false;
    }

    /** active the TopComponent at list index <index> (bring to front) */
    public final void bringToFront(int index) {
        if (index >= 0) {
            OpenedListItem item = model.getItem(index);
            if (item != null) {
                item.bringToFront();
            }
        }
    }

    // --------------------------------------------------------------------------
    // UpdateListener
    // --------------------------------------------------------------------------
    public void addUpdateListener(UpdateListener listener) {
        if (listener != null) {
            task.listenerList.add(listener);
        }
    }

    public void removeUpdateListener(UpdateListener listener) {
        if (listener != null) {
            task.listenerList.remove(listener);
        }
    }

    // --------------------------------------------------------------------------
    // UpdateTask
    // --------------------------------------------------------------------------
    // called into AWT thread context, do gui work
    private class UpdateReadyTask implements Runnable {

        private ArrayList<UpdateListener> listenerList = new ArrayList<UpdateListener>();

        public final void run() {
            if (requestCounter < 1) {
                model.fireUpdate();
                clearSelection();

                for(OpenedListItem item : model.getCloseableList()) {
                    item.getTopComponent().close();
                }

                for (UpdateListener listener : listenerList) {
                    listener.updateFinished();
                }
                updateInProgress = false;
                requestCounter = 0;
            } else {
                updateInProgress = false;
                requestCounter = 0;
                update();
            }
        }
    }

    // -------------------------------------------------------------------------
    private class ModelUpdater implements Runnable {

        public void run() {
            // worker code
            try {
                model.updateModel();

                // gui fire model update
                SwingUtilities.invokeLater(task);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }
    // -------------------------------------------------------------------------

    // DEBUG Version
    private class WindowStateListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
//      System.out.println("[" + getClass() + "] " +evt.getPropertyName()
//              + " from <" +evt.getSource() +">"
//              );

            Object sender = evt.getNewValue(); //evt.getSource() ;
            if (sender instanceof Mode) {
                Mode mode = (Mode) sender;
                TopComponent tc = mode.getSelectedTopComponent();
                if (tc != null) {
                    DataObject dObj = tc.getLookup().lookup(DataObject.class);
//          System.out.println("[" + getClass() + "] dataobject is <"
//                  +dObj +">");
                }
            }
//      else
//      {
//        System.out.println("[" + getClass() + "] no topcomponent <" +sender +">");
//      }
        }
    }
}
