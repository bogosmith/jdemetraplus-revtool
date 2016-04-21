/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.actions;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.DescriptiveSpecificationManager;
import ec.nbdemetra.ra.descriptive.descriptors.DescriptiveSpecUI;
import ec.nbdemetra.ui.properties.l2fprod.PropertiesDialog;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.ui.view.tsprocessing.IApplyAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author aresda
 */
@ActionID(category = "Tools",
id = "ec.nbdemetra.ra.descriptive.actions.EditDescriptiveSpec")
@ActionRegistration(displayName = "#CTL_EditDescriptiveSpec")
@ActionReferences({
    @ActionReference(path = DescriptiveSpecificationManager.ITEMPATH, position = 1000, separatorAfter=1090)
})
@NbBundle.Messages("CTL_EditDescriptiveSpec=Open")
public class EditDescriptiveSpec implements ActionListener {

    private final ItemWsNode context;

    public EditDescriptiveSpec(ItemWsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        edit();
    }

    private void edit() {
        final WorkspaceItem<DescriptiveSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), DescriptiveSpecification.class);
        if (xdoc == null|| xdoc.getElement() == null) {
            return; 
        }
        final DescriptiveSpecUI ui = new DescriptiveSpecUI(xdoc.getElement().clone(), xdoc.isReadOnly());
        PropertiesDialog propDialog =
                new PropertiesDialog(WindowManager.getDefault().getMainWindow(), true, ui,
                new IApplyAction() {
                    @Override
                    public void apply() {
                        xdoc.setElement(ui.getCore());
                   }

                    @Override
                    public String getActionName() {
                        return "OK";
                    }
                });
        propDialog.setTitle(xdoc.getDisplayName());
        propDialog.setVisible(true);
    }
}
