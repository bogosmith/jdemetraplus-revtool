/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

import ec.nbdemetra.ra.ui.view.VintageTsProcessingViewer;
import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.IActiveView;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author aresda
 */
public abstract class RevisionTopComponent extends TopComponent implements ExplorerManager.Provider, IActiveView, LookupListener {

    @Override
    public ExplorerManager getExplorerManager() {
        return ActiveViewManager.getInstance().getExplorerManager();
    }
    protected Lookup.Result<WorkspaceFactory.Event> result;
    protected VintageTsProcessingViewer panel;

    protected RevisionTopComponent() {
        result = WorkspaceFactory.getInstance().getLookup().lookupResult(WorkspaceFactory.Event.class);
    }
    
    @Override
    public boolean fill(JMenu menu) {
        return true;
    }
    
    @Override
    public void componentOpened() {
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        if (panel != null) {
            panel.dispose();
        }
        result.removeLookupListener(this);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends WorkspaceFactory.Event> all = result.allInstances();
        if (!all.isEmpty()) {
            Iterator<? extends WorkspaceFactory.Event> iterator = all.iterator();
            while (iterator.hasNext()) {
                WorkspaceFactory.Event ev = iterator.next();
                if (ev.info == WorkspaceFactory.Event.REMOVINGITEM) {
                    WorkspaceItem<?> wdoc = ev.workspace.searchDocument(ev.id);
                    if (wdoc.getElement() == panel.getDocument()) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                close();
                            }
                        });
                    }
                }
            }
        }
    }
}
