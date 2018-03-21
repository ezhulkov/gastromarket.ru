package org.ohm.gastro.gui.components;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.MailService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class EmailSection extends BaseComponent {

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP)
    private String emailString;

    @Inject
    private Block resultBlock;

    public Block onSubmitFromEmailAjaxForm() {
        if (StringUtils.isNotEmpty(emailString)) {
            logger.info("Subscribing {}", emailString);
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("email", emailString);
                }
            };
            getMailService().sendAdminMessage(MailService.MailType.USER_SUBSCRIBED, params);
            getMailService().syncChimpList(emailString, ImmutableMap.of(MailService.MC_REGION, RegionFilter.getCurrentRegion().getRegion()), MailService.MC_USERS_LIST);
            getMailService().subscribeChimpList(emailString, MailService.MC_USERS_LIST);
            return resultBlock;
        }
        return null;
    }

}
