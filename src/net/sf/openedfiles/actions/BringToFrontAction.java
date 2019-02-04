package net.sf.openedfiles.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ActionID(
        category = "Tools",
        id = "net.sf.openedfiles.actions.BringToFrontAction"
)
@ActionRegistration(
        displayName = "#CTL_BringToFrontAction"
)
@ActionReference(path = "OFL/Menus/Popup/sensitive", position = 0, separatorAfter = 10)
@Messages("CTL_BringToFrontAction=Bring To Front")
public final class BringToFrontAction implements ActionListener {

    private final TopComponent context;

    public BringToFrontAction(TopComponent context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (context != null) {
            if (context.isOpened()) {
                context.requestActive();
            }
        }
    }
}
