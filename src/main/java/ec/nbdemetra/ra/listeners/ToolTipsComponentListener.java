/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.listeners;

import ec.nbdemetra.ra.parametric.ParametricSpecificationManager;
import ec.nbdemetra.ra.providers.oracle.VintageOracleProvider;
import ec.nbdemetra.ra.providers.spreadsheets.VintagesSpreadSheetProvider;
import ec.nbdemetra.ra.providers.txt.VintageTxtProvider;
import ec.nbdemetra.ui.tsproviders.ProvidersTopComponent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;

/**
 *
 * @author bennouha
 */
public class ToolTipsComponentListener implements ComponentListener {

    private ProvidersTopComponent providersTopComponent = null;
    private static ToolTipsComponentListener instance = null;
    private boolean installed = false;

    private ToolTipsComponentListener() {
    }

    public static ToolTipsComponentListener getInstance(ProvidersTopComponent providersTopComponent) {
        if (ToolTipsComponentListener.instance == null) {
                    ToolTipsComponentListener.instance = new ToolTipsComponentListener();
                    ToolTipsComponentListener.instance.providersTopComponent = providersTopComponent;
                    ToolTipsComponentListener.instance.installed = false;
        } else {
            ToolTipsComponentListener.instance.providersTopComponent = providersTopComponent;
            ToolTipsComponentListener.instance.installed = false;
        }
        return ToolTipsComponentListener.instance;
    }

    @Override
    public void componentResized(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
        if (!installed && this.providersTopComponent != null) {
            installed = true;
            for (Node item : this.providersTopComponent.getExplorerManager().getRootContext().getChildren().getNodes()) {
                if (item instanceof AbstractNode && (VintageOracleProvider.DISPLAYNAME.equals(item.getDisplayName())
                        || VintagesSpreadSheetProvider.DISPLAYNAME.equals(item.getDisplayName())
                        || VintageTxtProvider.DISPLAYNAME.equals(item.getDisplayName()))) {
                    ((AbstractNode) item).setShortDescription(ParametricSpecificationManager.ID.get(0));
                }
            }
        }
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
}
