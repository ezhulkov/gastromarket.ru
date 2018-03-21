package org.ohm.gastro.gui.pages.admin.catalog;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.pages.AbstractPage;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.UserExistsException;
import org.ohm.gastro.util.CommonsUtils;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Create extends AbstractPage {

    @Property
    private boolean sent;

    public Object onActivate(String email, Region region, String cookName, String catalogName, String catalogLink) throws UserExistsException, EmptyPasswordException {
        UserEntity user = new UserEntity();
        user.setType(Type.COOK);
        user.setEmail(email);
        user.setFullName(cookName);
        user.setSourceUrl(catalogLink);
        getUserService().createUser(user, CommonsUtils.generatePassword(), catalogName, region, true);
        return List.class;
    }

}
