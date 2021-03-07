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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
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
@ActionID(category = "Window", id = "net.sf.openedfiles.OpenFilesListTopComponent")@
ActionReference(path = "Menu/Window", position = 20750)
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

        setName(NbBundle.getMessage(OpenFilesListTopComponent.class, "CTL_OpenFilesListTopComponent"));
        setToolTipText(NbBundle.getMessage(OpenFilesListTopComponent.class, "HINT_OpenFilesListTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

        associateLookup(ExplorerUtils.createLookup(em, getActionMap()));

        floatableToolbarPanel.setVisible(false);
        
        beanTreeView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent arg0) {
                // TODO: Show x to close a file from the TopComponent
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                // TODO: Show file on one click not on double, make an option to change the default behaviour
            }
        });

        topComponentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent arg0) {
                beanTreeLayeredPanel.setBounds(0, 0, topComponentPanel.getWidth(), topComponentPanel.getHeight());
                beanTreeView.setBounds(0, 0, topComponentPanel.getWidth(), topComponentPanel.getHeight());

                floatableToolbarPanel.setBounds((topComponentPanel.getWidth() - floatableToolbarPanel.getWidth()) - 2, 1, floatableToolbarPanel.getWidth(), floatableToolbarPanel.getHeight());
            }

            @Override
            public void componentShown(ComponentEvent arg0) {
                beanTreeLayeredPanel.setBounds(0, 0, topComponentPanel.getWidth(), topComponentPanel.getHeight());
                beanTreeView.setBounds(0, 0, topComponentPanel.getWidth(), topComponentPanel.getHeight());

                floatableToolbarPanel.setBounds((topComponentPanel.getWidth() - floatableToolbarPanel.getWidth()) - 2, 1, floatableToolbarPanel.getWidth(), floatableToolbarPanel.getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
                beanTreeLayeredPanel.setBounds(0, 0, topComponentPanel.getWidth(), topComponentPanel.getHeight());
                beanTreeView.setBounds(0, 0, topComponentPanel.getWidth(), topComponentPanel.getHeight());

                floatableToolbarPanel.setBounds((topComponentPanel.getWidth() - floatableToolbarPanel.getWidth()) - 2, 1, floatableToolbarPanel.getWidth(), floatableToolbarPanel.getHeight());
            }
        });
        
        hoverChildrenOfTopComponent(this);

        //initial loading
        updateModelAndRefreshUI();
    }

    private void hoverChildrenOfTopComponent(Container comp) {
        comp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent arg0) {
                floatableToolbarPanel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
                if (!OpenFilesListTopComponent.getInstance().contains(arg0.getPoint())) {
                    floatableToolbarPanel.setVisible(false);
                }
            }
        });

        final Component[] components = comp.getComponents();

        for (Component component : components) {
            if (component instanceof Container) {
                hoverChildrenOfTopComponent((Container)component);
            }
        }
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

        toolbarButtonGroup = new javax.swing.ButtonGroup();
        topComponentPanel = new javax.swing.JPanel();
        beanTreeLayeredPanel = new javax.swing.JLayeredPane();
        floatableToolbarPanel = new javax.swing.JPanel();
        floatableToolbar = new javax.swing.JToolBar();
        sortMostRecentButton = new javax.swing.JToggleButton();
        sortAscButton = new javax.swing.JToggleButton();
        sortDescButton = new javax.swing.JToggleButton();
        reloadFilesButton = new javax.swing.JButton();
        closeAllTabs = new javax.swing.JButton();
        beanTreeView = new org.openide.explorer.view.BeanTreeView();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        topComponentPanel.setAlignmentX(0.0F);
        topComponentPanel.setAlignmentY(0.0F);

        floatableToolbarPanel.setAlignmentX(0.0F);
        floatableToolbarPanel.setAlignmentY(0.0F);
        floatableToolbarPanel.setMinimumSize(new java.awt.Dimension(122, 40));
        floatableToolbarPanel.setPreferredSize(new java.awt.Dimension(122, 40));

        floatableToolbar.setRollover(true);
        floatableToolbar.setAlignmentX(0.0F);
        floatableToolbar.setAlignmentY(0.0F);
        floatableToolbar.setBorderPainted(false);
        floatableToolbar.setMaximumSize(new java.awt.Dimension(122, 40));
        floatableToolbar.setMinimumSize(new java.awt.Dimension(122, 40));
        floatableToolbar.setOpaque(false);
        floatableToolbar.setPreferredSize(new java.awt.Dimension(122, 40));

        toolbarButtonGroup.add(sortMostRecentButton);
        sortMostRecentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/sort-recent.png"))); // NOI18N
        sortMostRecentButton.setSelected(true);
        sortMostRecentButton.setFocusable(false);
        sortMostRecentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sortMostRecentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sortMostRecentButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sortMostRecentButtonMouseClicked(evt);
            }
        });
        floatableToolbar.add(sortMostRecentButton);

        toolbarButtonGroup.add(sortAscButton);
        sortAscButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/sort-az.png"))); // NOI18N
        sortAscButton.setFocusable(false);
        sortAscButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sortAscButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sortAscButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sortAscButtonMouseClicked(evt);
            }
        });
        floatableToolbar.add(sortAscButton);

        toolbarButtonGroup.add(sortDescButton);
        sortDescButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/sort-za.png"))); // NOI18N
        sortDescButton.setFocusable(false);
        sortDescButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sortDescButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sortDescButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sortDescButtonMouseClicked(evt);
            }
        });
        floatableToolbar.add(sortDescButton);

        reloadFilesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/reload.png"))); // NOI18N
        reloadFilesButton.setFocusable(false);
        reloadFilesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reloadFilesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reloadFilesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reloadFilesButtonMouseClicked(evt);
            }
        });
        floatableToolbar.add(reloadFilesButton);

        org.openide.awt.Mnemonics.setLocalizedText(closeAllTabs, "close");
        closeAllTabs.setFocusable(false);
        closeAllTabs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeAllTabs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        closeAllTabs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeAllTabsMouseClicked(evt);
            }
        });
        floatableToolbar.add(closeAllTabs);

        javax.swing.GroupLayout floatableToolbarPanelLayout = new javax.swing.GroupLayout(floatableToolbarPanel);
        floatableToolbarPanel.setLayout(floatableToolbarPanelLayout);
        floatableToolbarPanelLayout.setHorizontalGroup(
            floatableToolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(floatableToolbarPanelLayout.createSequentialGroup()
                .addComponent(floatableToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        floatableToolbarPanelLayout.setVerticalGroup(
            floatableToolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(floatableToolbarPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(floatableToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        beanTreeLayeredPanel.add(floatableToolbarPanel);
        floatableToolbarPanel.setBounds(335, 2, 175, 40);

        beanTreeView.setViewportBorder(javax.swing.BorderFactory.createEtchedBorder());
        beanTreeView.setAlignmentX(0.0F);
        beanTreeView.setAlignmentY(0.0F);
        beanTreeView.setMinimumSize(new java.awt.Dimension(18, 32));
        beanTreeView.setRootVisible(false);
        beanTreeView.setUseSubstringInQuickSearch(false);
        beanTreeLayeredPanel.add(beanTreeView);
        beanTreeView.setBounds(0, 0, 514, 422);

        javax.swing.GroupLayout topComponentPanelLayout = new javax.swing.GroupLayout(topComponentPanel);
        topComponentPanel.setLayout(topComponentPanelLayout);
        topComponentPanelLayout.setHorizontalGroup(
            topComponentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(beanTreeLayeredPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
        );
        topComponentPanelLayout.setVerticalGroup(
            topComponentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(beanTreeLayeredPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topComponentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topComponentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    private String sortBy = "";
    private void closeAllTabsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeAllTabsMouseClicked
        final Collection<TopComponent> tcs = model.getTCs();
        
        tcs.forEach((TopComponent tc) -> tc.close());
        
        model.updateModel(sortBy);
    }//GEN-LAST:event_closeAllTabsMouseClicked

    private void reloadFilesButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reloadFilesButtonMouseClicked
        updateModelAndRefreshUI();
    }//GEN-LAST:event_reloadFilesButtonMouseClicked

    private void sortAscButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sortAscButtonMouseClicked
        sortBy = "ASC";
        updateModelAndRefreshUI();
    }//GEN-LAST:event_sortAscButtonMouseClicked

    private void sortDescButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sortDescButtonMouseClicked
        sortBy = "DESC";
        updateModelAndRefreshUI();
    }//GEN-LAST:event_sortDescButtonMouseClicked

    private void sortMostRecentButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sortMostRecentButtonMouseClicked
        sortBy = "";
        updateModelAndRefreshUI();
    }//GEN-LAST:event_sortMostRecentButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane beanTreeLayeredPanel;
    private org.openide.explorer.view.BeanTreeView beanTreeView;
    private javax.swing.JButton closeAllTabs;
    private javax.swing.JToolBar floatableToolbar;
    private javax.swing.JPanel floatableToolbarPanel;
    private javax.swing.JButton reloadFilesButton;
    private javax.swing.JToggleButton sortAscButton;
    private javax.swing.JToggleButton sortDescButton;
    private javax.swing.JToggleButton sortMostRecentButton;
    private javax.swing.ButtonGroup toolbarButtonGroup;
    private javax.swing.JPanel topComponentPanel;
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
        model.updateModel(sortBy);

        final Collection<TopComponent> tcs = model.getTCs();

        Children children = Children.create(new OpenFileNodeFactory(tcs), true);
        Node rootNode = new AbstractNode(children);
        em.setRootContext(rootNode);
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
