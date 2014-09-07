package org.ohm.gastro.gui.pages.admin.user;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.UserService;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends EditObjectPage<UserEntity> {

    @Inject
    private UserService userService;

    @Property
    private UserEntity oneUser;

    @Component(id = "name", parameters = {"value=object?.username", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "type", parameters = {"value=object?.type", "validate=required"})
    private Select typeField;

    @Cached
    public java.util.List getUsers() {
        return userService.findAllUser();
    }

    @Override
    public ServiceCallback<UserEntity> getServiceCallback() {
        return new AbstractServiceCallback<UserEntity>() {

            @Override
            public Class<? extends BaseComponent> addObject(UserEntity user) {
                userService.saveUser(user);
                return List.class;
            }

            @Override
            public UserEntity newObject() {
                return new UserEntity();
            }

        };
    }

    public void onActionFromDelete(Long id) {
        userService.deleteUser(id);
    }

}

