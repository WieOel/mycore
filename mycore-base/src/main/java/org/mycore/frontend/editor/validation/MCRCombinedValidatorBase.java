package org.mycore.frontend.editor.validation;

import java.util.ArrayList;
import java.util.List;

public abstract class MCRCombinedValidatorBase extends MCRConfigurableBase {

    protected List<MCRConfigurable> validators = new ArrayList<MCRConfigurable>();

    public void setProperty(String name, String value) {
        super.setProperty(name, value);
        for (MCRConfigurable validator : validators)
            validator.setProperty(name, value);
    }

    public boolean hasRequiredProperties() {
        for (MCRConfigurable validator : validators) {
            if (validator.hasRequiredProperties())
                return true;
        }
        return false;
    }

}