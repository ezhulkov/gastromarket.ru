package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class LoginUserModal extends BaseComponent {

    @Property
    private String eMail;

    @Property
    private String password;

}