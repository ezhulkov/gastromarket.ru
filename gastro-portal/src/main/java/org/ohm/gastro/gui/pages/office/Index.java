package org.ohm.gastro.gui.pages.office;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.MailService.MailType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<UserEntity> {

    @Component(id = "fullName", parameters = {"value=object?.fullName", "validate=maxlength=64"})
    private TextField fNameField;

    @Component(id = "region", parameters = {"value=object?.region", "validate=required"})
    private Select region;

    @Component(id = "mobilePhone", parameters = {"value=object?.mobilePhone"})
    private TextField mobileField;

    @Component(id = "password1", parameters = {"value=newPassword1", "validate=maxlength=64"})
    private PasswordField p1Field;

    @Component(id = "password2", parameters = {"value=newPassword2", "validate=maxlength=64"})
    private PasswordField p2Field;

    @Property
    private String newPassword1;

    @Property
    private String newPassword2;

    @Property
    private UserEntity child;

    @Property
    private MailService.MailType notificationConfig;

    @Inject
    @Property
    private Block subscriptionBlock;

    @Override
    public ServiceCallback<UserEntity> getServiceCallback() {
        return new AbstractServiceCallback<UserEntity>() {

            @Override
            public UserEntity findObject(String id) {
                return getAuthenticatedUser();
            }

            @Override
            public Class<? extends BaseComponent> updateObject(UserEntity user) {
                if (StringUtils.isNotEmpty(newPassword1) && StringUtils.isNotEmpty(newPassword2)) {
                    if (newPassword1.equals(newPassword2)) user.setPassword(getPasswordEncoder().encode(newPassword1));
                    else getEditObject().getForm().recordError(p1Field, getMessages().get("error.password.mismatch"));
                }
                try {
                    getUserService().saveUser(user, newPassword1);
                } catch (EmptyPasswordException e) {
                    getEditObject().getForm().recordError(p1Field, getMessages().get("error.password.empty"));
                }
                if (isCook()) {
                    final List<MailType> enabledConfigs = getRequest().getParameterNames().stream()
                            .filter(t -> t.startsWith("NOTIF"))
                            .map(t -> MailType.valueOf(t.substring(6)))
                            .collect(Collectors.toList());
                    final Collection<MailType> disabledConfigs = CollectionUtils.disjunction(getNotificationConfigs(), enabledConfigs);
                    getUserService().toggleSubscription(user, disabledConfigs);
                }
                return Index.class;
            }

        };
    }

    public String getAvatarUrl() {
        return getAuthenticatedUser().getAvatarUrl();
    }

    public List<UserEntity> getChildrenUsers() {
        return getUserService().findAllChildren(getAuthenticatedUser());
    }

    public List<MailService.MailType> getNotificationConfigs() {
        return Arrays.stream(MailService.MailType.values()).filter(MailType::isConfigurable).collect(Collectors.toList());
    }

    public String getNotificationConfigPrintable() {
        return getMessages().get("MailType." + notificationConfig.name());
    }

    public boolean isNotificationEnabled() {
        return getMailService().isNotificationEnabled(getAuthenticatedUser(), notificationConfig);
    }

    public Block onActionFromSubscribe() {
        getUserService().toggleSubscription(getAuthenticatedUser());
        return subscriptionBlock;
    }

    public Block onActionFromUnsubscribe() {
        getUserService().toggleSubscription(getAuthenticatedUser());
        return subscriptionBlock;
    }

}
