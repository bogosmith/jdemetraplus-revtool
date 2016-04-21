/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.actions;

import ec.nbdemetra.ra.IMatrixResults;
import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ra.descriptive.DescriptiveDocumentManager;
import ec.nbdemetra.ra.export.CSVGenerator;
import ec.nbdemetra.ra.export.ExportFileHelper;
import ec.nbdemetra.ra.parametric.ParametricDocumentManager;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import java.io.File;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author bennouha
 */
@ActionID(category = "Edit",
        id = "ec.nbdemetra.ra.actions.ExportToCSVAction")
@ActionRegistration(
        displayName = "#CTL_ExportToCSVAction", lazy = false)
@ActionReferences({
    @ActionReference(path = ParametricDocumentManager.ITEMPATH, position = 1100),
    @ActionReference(path = DescriptiveDocumentManager.ITEMPATH, position = 1100)
})
@Messages("CTL_ExportToCSVAction=Export to csv")
public final class ExportToCSVAction extends SingleNodeAction<ItemWsNode> {

    public static final String EXPORT_MESSAGE = "Export to CSV ?";

    public ExportToCSVAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        VintageTsDocument elem = (VintageTsDocument) cur.getElement();
        IMatrixResults result = (IMatrixResults) elem.getResults();
        if (result == null) {
            NotifyDescriptor nd = new NotifyDescriptor.Message("Document is empty !", NotifyDescriptor.PLAIN_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        File fileChoosed = ExportFileHelper.openFileChooser(".csv");
        if (fileChoosed != null) {
            File file = CSVGenerator.toCSV(result, elem.getSpecification());
            ExportFileHelper.saveFile(fileChoosed, file, ".csv");
        }
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        return cur != null && !cur.isReadOnly();
    }

    @Override
    public String getName() {
        return Bundle.CTL_ExportToCSVAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
