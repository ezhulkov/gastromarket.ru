package org.ohm.gastro.gui.pages;

import org.ohm.gastro.domain.UserEntity.Type;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class SignupCook extends Signup {

    @Override
    protected Type getType() {
        return Type.COOK;
    }

}