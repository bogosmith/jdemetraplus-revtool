/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.actions;

import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.ParametricSpecificationManager;
import ec.nbdemetra.ra.parametric.descriptors.ParametricSpecUI;
import ec.nbdemetra.ra.ui.properties.ArrayRevisionId;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
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
 * @author bennouha
 */
@ActionID(category = "Tools",
id = "ec.nbdemetra.ra.parametric.actions.EditParametricSpec")
@ActionRegistration(displayName = "#CTL_EditParametricSpec")
@ActionReferences({
    @ActionReference(path = ParametricSpecificationManager.ITEMPATH, position = 1000, separatorAfter = 1090)
})
@NbBundle.Messages("CTL_EditParametricSpec=Open")
public class EditParametricSpec implements ActionListener {

    private final ItemWsNode context;

    public EditParametricSpec(ItemWsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        edit();
    }

    private void edit() {
        final WorkspaceItem<ParametricSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), ParametricSpecification.class);
        if (xdoc == null || xdoc.getElement() == null) {
            return;
        }
        final ParametricSpecUI ui = new ParametricSpecUI(xdoc.getElement().clone(), xdoc.isReadOnly());
        PropertiesDialog propDialog =
                new PropertiesDialog(WindowManager.getDefault().getMainWindow(), true, ui,
                new IApplyAction() {
                    @Override
                    public void apply() {
                        xdoc.setElement(ui.getCore());
                        //CustomPropertyEditorRegistry.INSTANCE.getRegistry().registerEditor(ArrayRevisionId.class, ui.getCore().getRegressionModelsSpec().editor);
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
