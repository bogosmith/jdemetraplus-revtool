/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ws;

import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ra.RevisionTopComponent;
import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.Menus;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import javax.swing.Action;
import javax.swing.JMenu;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;

/**
 *
 * @author aresda
 */
public abstract class WorkspaceRevisionTopComponent<T extends VintageTsDocument<?, ?>> extends RevisionTopComponent {

    private final WorkspaceItem<T> doc;

    protected abstract String getContextPath();

    protected WorkspaceRevisionTopComponent(WorkspaceItem<T> doc) {
        this.doc = doc;
        associateLookup(ExplorerUtils.createLookup(ActiveViewManager.getInstance().getExplorerManager(), getActionMap()));
    }

    public WorkspaceItem<T> getDocument() {
        return doc;
    }
    
    @Override
    public boolean hasContextMenu(){
        return true;
    }

    @Override
    public boolean fill(JMenu menu) {
        Menus.fillMenu(menu, getContextPath(), WorkspaceFactory.TSCONTEXTPATH);
        return true;
    }
    
    @Override
    public Node getNode(){
        return null;
    }

    @Override
    public Action[] getActions() {
        return Menus.createActions(super.getActions(), getContextPath(), WorkspaceFactory.TSCONTEXTPATH);
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        doc.setView(this);
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        doc.setView(null);
        super.componentClosed();
    }
    
    @Override
    public void componentActivated(){
        ActiveViewManager.getInstance().set(this);
    }
    
    @Override
    public void componentDeactivated(){
        ActiveViewManager.getInstance().set(null);
    }
}
