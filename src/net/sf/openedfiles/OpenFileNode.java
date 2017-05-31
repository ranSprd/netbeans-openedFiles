/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.openedfiles;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

/**
 *
 * @author markiewb
 */
public class OpenFileNode extends AbstractNode{

    private static Lookup createLookup(TopComponent tc) {
        final DataObject dataO = getDataObject(tc);
        InstanceContent instanceContent = new InstanceContent();
        if (tc != null) {
            instanceContent.add(tc);
        }
        if (dataO != null) {
            instanceContent.add(dataO);
            try {
                FileObject fileO = dataO.getPrimaryFile();
                if (null!=fileO){
                    instanceContent.add(fileO);
                }
            } catch (Exception e) {
            }
        }

        return new AbstractLookup(instanceContent);
    }

    private static DataObject getDataObject(TopComponent tc) {
        return tc.getLookup().lookup(DataObject.class);
    }

    private final TopComponent context;

    public OpenFileNode(TopComponent context) {
        super(Children.LEAF, createLookup(context));
        this.context=context;
    }
    

    @Override
    public Action[] getActions(boolean _context) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(Utilities.actionsForPath("OFL/Menus/Popup"));
        
        DataObject dataObject = getDataObject(context);
        if (null!=dataObject){
            
            Node nodeDelegate = dataObject.getNodeDelegate();
            if (null!=nodeDelegate){
                Action[] defaultActions = nodeDelegate.getActions(false);
                for (Action action : defaultActions) {
                    actions.add(action);
                }
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }

    
    @Override
    public String getHtmlDisplayName() {

        String back = context.getHtmlDisplayName();
        if (back == null) {
            back = context.getDisplayName();
            if (back == null) {
                back = context.getName();
                if (back == null) {
                    back = "unresolved [" + context.toString() + "]";
                }
            }
        }
        return back;

    }

    @Override
    public Image getIcon(int type) {
        return context.getIcon();
    }

    @Override
    public Action getPreferredAction() {
        return getActions(false)[0];
    }
    
    

    

}
