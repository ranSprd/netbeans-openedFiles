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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;

/**
 * Top component which displays OpenedFiles.
 */
@ConvertAsProperties(
        dtd = "-//net.sf.openedfiles//OpenFiles//EN",
        autostore = false
)
@TopComponent.Description(
    preferredID = "OpenFilesListTopComponent",
    iconBase = "resource/windowlist16.png",
    persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "navigator", openAtStartup = false)
@ActionID(category = "Window", id = "net.sf.openedfiles.OpenFilesListTopComponent")
@ActionReference(path = "Menu/Window", position = 20750)
@TopComponent.OpenActionRegistration(
    displayName = "#CTL_OpenFilesListTopComponent",
    preferredID = "OpenFilesListTopComponent"
)
public class OpenFilesListTopComponent extends TopComponent implements ExplorerManager.Provider, PropertyChangeListener {
    private final OpenFilesModel model = new OpenFilesModel();
    private final ExplorerManager em = new ExplorerManager();
    /**
     * path to the icon used by the component and its open action
     */
    static final String ICON_PATH = "resource/windowlist16.png";
    static final String LARGE_ICON_PATH = "resource/windowlist32.png";

    public static OpenFilesListTopComponent getInstance() {
        return new OpenFilesListTopComponent();
    }

    private OpenFilesListTopComponent() {
        initComponents();

        // work in progress
//        sortByUsageButton.setVisible(false);
//        btnManualRefresh.setVisible(false);
        setName(NbBundle.getMessage(OpenFilesListTopComponent.class, "CTL_OpenFilesListTopComponent"));
        setToolTipText(NbBundle.getMessage(OpenFilesListTopComponent.class, "HINT_OpenFilesListTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

//        btnManualRefresh.addActionListener( (ActionEvent e) -> updateModelAndRefreshUI());
        associateLookup(ExplorerUtils.createLookup(em, getActionMap()));

        //initial loading
        updateModelAndRefreshUI();
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

    @Override
    protected void componentClosed() {
        /**
         * remove a callback into the WindowManager Registry
         */
        WindowManager.getDefault().getRegistry().removePropertyChangeListener(this);
    }

    @Override
    protected void componentOpened() {
        /**
         * put a callback into the WindowManager Registry
         */
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        beanTreeView1 = new org.openide.explorer.view.BeanTreeView();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        beanTreeView1.setViewportBorder(javax.swing.BorderFactory.createEtchedBorder());
        beanTreeView1.setRootVisible(false);
        beanTreeView1.setUseSubstringInQuickSearch(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(beanTreeView1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(beanTreeView1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView1;
    private javax.swing.JPanel jPanel1;
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
                // dock everywhere, except editor frame
                return modes.stream()
                        .filter(singleMode -> singleMode.getName() != null && !"editor".equals(singleMode.getName()))
                        .collect(Collectors.toList());
            }
        }

        return null;
    }

    @Override
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

        int itemCount = model.readOpenedWindows().size();

        if (itemCount > 0) {
            setName(String.format("%s (%s)", NbBundle.getMessage(OpenFilesListTopComponent.class, "CTL_OpenFilesListTopComponent"), itemCount));
        } else {
            setName(NbBundle.getMessage(OpenFilesListTopComponent.class, "CTL_OpenFilesListTopComponent"));
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
