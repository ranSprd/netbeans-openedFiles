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
// function   : main component of the module, contains a list of all opened 
//              files (editor)
//
// todo       :
//
// modified   : 
package net.sf.openedfiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;

/**
 * Top component which displays OpenedFiles.
 */
@TopComponent.Description(
        preferredID = "OpenFilesListTopComponent",
        iconBase="resource/windowlist16.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@ActionID(category = "Window", id = "net.sf.openedfiles.testerTopComponent")
@ActionReference(path = "Menu/Window" , position = 333)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_OpenFilesListTopComponent",
        preferredID = "OpenFilesListTopComponent"
)
@NbBundle.Messages({
    "CTL_OpenFilesListAction=Opened Files",
    "CTL_OpenFilesListTopComponent=Opened Files",
    "HINT_OpenFilesListTopComponent=Window contains a collection of Files which are opened in editor"
})
public final class OpenFilesListTopComponent extends TopComponent implements ExplorerManager.Provider, PropertyChangeListener {
    private final OpenFilesModel model = new OpenFilesModel();
    private final ExplorerManager em = new ExplorerManager();
    /**
     * path to the icon used by the component and its open action
     */
    static final String ICON_PATH = "resource/windowlist16.png";
    static final String LARGE_ICON_PATH = "resource/windowlist32.png";

    private OpenFilesListTopComponent() {
        initComponents();
        jToolBar1.setVisible(false);
        setName(NbBundle.getMessage(OpenFilesListTopComponent.class, "CTL_OpenFilesListTopComponent"));
        setToolTipText(NbBundle.getMessage(OpenFilesListTopComponent.class, "HINT_OpenFilesListTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

        btnManualRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateModelAndRefreshUI();
            }
        });
        
        associateLookup(ExplorerUtils.createLookup(em, getActionMap()));

        //initial loading
        updateModelAndRefreshUI();
    }

    public ExplorerManager getExplorerManager() {
        return em;
    }

    @Override
    protected void componentClosed() {
        /** remove a callback into the WindowManager Registry */
        WindowManager.getDefault().getRegistry().removePropertyChangeListener(this);
    }


    @Override
    protected void componentOpened() {
        /** put a callback into the WindowManager Registry */
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(this);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnManualRefresh = new javax.swing.JButton();
        beanTreeView1 = new org.openide.explorer.view.BeanTreeView();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnManualRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/reload.png"))); // NOI18N
        btnManualRefresh.setToolTipText("update view");
        jToolBar1.add(btnManualRefresh);

        beanTreeView1.setRootVisible(false);
        beanTreeView1.setUseSubstringInQuickSearch(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
            .add(beanTreeView1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(beanTreeView1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView1;
    private javax.swing.JButton btnManualRefresh;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    /**
     * Allows the top component to specify a ui-list (with modes) which can be
     * docked by end user. Subclasses should override this method if they want
     * to alter docking policy of top component. So for example, by returning
     * empty list, top component refuses to be docked anywhere. Default
     * implementation allows docking anywhere by returning input list unchanged.
     *
     * @param modes
     * @return
     */
    @Override
    public List<Mode> availableModes(List<Mode> modes) {
        if (modes != null) {
            int size = modes.size();
            if (size > 0) {
                ArrayList<Mode> back = new ArrayList<Mode>(size);
                for (Mode single : modes) {
                    String name = single.getName();
                    if (name != null) {
                        // dock everywhere, except editor frame
                        if (!"editor".equals(name)) {
                            back.add(single);
                        }
                    }
                }
            }
        }

        return null;
    }
    
        public void propertyChange(PropertyChangeEvent evt) {
        if (evt != null) {
            String name = evt.getPropertyName();
            if (name != null) {
                if ((name.equals(TopComponent.Registry.PROP_TC_OPENED))
                        || (name.equals(TopComponent.Registry.PROP_TC_CLOSED))) {
                    updateModelAndRefreshUI();
                } else if (name.equals(TopComponent.Registry.PROP_ACTIVATED)) {
                    final Object sender = evt.getNewValue();
                    if (sender instanceof TopComponent) {
                        model.logActivation((TopComponent) sender);
                        //FIXME mark the selected TC
                    }
                }
            }
        }
    }

    private void updateModelAndRefreshUI() {
        model.updateModel();

        final Collection<TopComponent> tcs = model.getTCs();

        Children children = Children.create(new OpenFileNodeFactory(tcs), true);
        Node rootNode = new AbstractNode(children);
        em.setRootContext(rootNode);
    }

}
