package org.python.util.install;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class SuccessPage extends AbstractWizardPage {

    private JarInfo _jarInfo;
    private JLabel _label;

    public SuccessPage(JarInfo jarInfo) {
        super();
        _jarInfo = jarInfo;
        initComponents();
    }

    private void initComponents() {
        _label = new JLabel();
        add(_label);
    }

    protected String getTitle() {
        return Installation.getText(TextKeys.CONGRATULATIONS);
    }

    protected String getDescription() {
        return Installation.getText(TextKeys.SUCCESS, _jarInfo.getVersion(), FrameInstaller.getTargetDirectory());
    }

    protected boolean isCancelVisible() {
        return false;
    }

    protected boolean isPreviousVisible() {
        return false;
    }

    protected boolean isNextVisible() {
        return true;
    }

    protected JComponent getFocusField() {
        return null;
    }

    protected void activate() {
        _label.setText(Installation.getText(TextKeys.PRESS_FINISH, getWizard().getFinishString()));
    }

    protected void passivate() {
    }

    protected void beforeValidate() {
    }
}