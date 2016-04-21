/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.actions;

import ec.nbdemetra.ra.descriptive.DescriptiveDocumentManager;
import ec.nbdemetra.ra.descriptive.DescriptiveSpecificationManager;
import ec.nbdemetra.ra.parametric.ParametricDocumentManager;
import ec.nbdemetra.ra.parametric.ParametricSpecificationManager;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.utilities.Id;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author bennouha
 */
@ActionID(category = "Edit",
        id = "ec.nbdemetra.ra.actions.RenameAction")
@ActionRegistration(displayName = "#CTL_RenameAction", lazy = false)
@ActionReferences({
    //    @ActionReference(path = "Menu/Edit"),
    @ActionReference(path = ParametricSpecificationManager.ITEMPATH, position = 1200),
    @ActionReference(path = ParametricDocumentManager.ITEMPATH, position = 1200),
    @ActionReference(path = DescriptiveSpecificationManager.ITEMPATH, position = 1200),
    @ActionReference(path = DescriptiveDocumentManager.ITEMPATH, position = 1200)
})
@Messages("CTL_RenameAction=Rename")
public final class RenameAction extends SingleNodeAction<ItemWsNode> {

    public static final String RENAME_TITLE = "Please enter the new name",
            NAME_MESSAGE = "New name:";

    public RenameAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur != null && !cur.isReadOnly()) {
            // create the input dialog
            String oldName = cur.getDisplayName(), newName = null;
            WsName nd = new WsName(cur.getFamily(), NAME_MESSAGE, RENAME_TITLE, oldName);
            nd.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(NotifyDescriptor.PROP_DETAIL)) {
                    }
                }
            });
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            newName = nd.getInputText();
//            cur.setIdentifier(newName);
            cur.setDisplayName(newName);
            WorkspaceFactory.Event ev = new WorkspaceFactory.Event(cur.getOwner(), cur.getId(), WorkspaceFactory.Event.ITEMRENAMED);
            WorkspaceFactory.getInstance().notifyEvent(ev);
        }
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        return cur != null && !cur.isReadOnly();
    }

    @Override
    public String getName() {
        return Bundle.CTL_RenameAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}

class WsName extends NotifyDescriptor.InputLine {

    WsName(final Id id, String title, String text, String input) {
        super(title, text);
        setInputText(input);
        textField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField txt = (JTextField) input;
                String name = txt.getText();
                if (null != WorkspaceFactory.getInstance().getActiveWorkspace().searchDocumentByName(id, name)) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(name + " is in use. You should choose another name!");
                    DialogDisplayer.getDefault().notify(nd);
                    return false;
                }
                return true;
            }
        });
    }
}
