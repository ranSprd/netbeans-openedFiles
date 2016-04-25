/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.openedfiles;

import java.util.Collection;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 *
 * @author markiewb
 */
public class OpenFileNodeFactory extends ChildFactory<TopComponent> {

    private final Collection<TopComponent> topComponents;

    public OpenFileNodeFactory(Collection<TopComponent> topComponents) {
        this.topComponents = topComponents;
    }

    @Override
    protected boolean createKeys(List<TopComponent> toPopulate) {
        toPopulate.addAll(topComponents);
        return true;
    }

    @Override
    protected Node createNodeForKey(TopComponent key) {
        return new OpenFileNode(key);
    }

}
