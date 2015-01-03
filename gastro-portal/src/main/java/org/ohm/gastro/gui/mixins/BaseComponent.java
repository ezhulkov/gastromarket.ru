package org.ohm.gastro.gui.mixins;

import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyEntity.Type;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.dto.ShoppingCart;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.MessageService;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseComponent {

    protected static final ThreadLocal<DateFormat> GUIDATE = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy");
        }
    };

    protected static final ThreadLocal<DateFormat> GUIDATE_LONG = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        }
    };

    protected final Logger logger = LoggerFactory.getLogger(BaseComponent.class);

    @Inject
    private HttpServletRequest httpServletRequest;

    @Inject
    private Request request;

    @Inject
    private Response response;

    @Inject
    private DaoAuthenticationProvider authenticationProvider;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private PropertyAccess access;

    @Inject
    private Messages messages;

    @Inject
    private ApplicationGlobals globals;

    @Inject
    private PropertyAccess propertyAccess;

    @Inject
    private CatalogService catalogService;

    @Inject
    private OrderService orderService;

    @Inject
    private UserService userService;

    @Inject
    private MessageService messageService;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @SessionState
    private ShoppingCart shoppingCart;

    public UserEntity getAuthenticatedUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null || securityContext.getAuthentication() == null) {
            return null;
        }
        Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal != null && principal instanceof UserEntity) {
            return (UserEntity) principal;
        }
        return null;
    }

    public boolean isAuthenticated() {
        return request.getSession(false) != null && getAuthenticatedUser() != null;
    }

    public boolean isAdmin() {
        UserDetails authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

    public boolean isCook() {
        UserDetails authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("COOK"));
    }

    public boolean isUser() {
        UserDetails authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("USER"));
    }

    public String getContextPath() {
        return globals.getServletContext().getContextPath();
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public Messages getMessages() {
        return messages;
    }

    public PropertyAccess getAccess() {
        return access;
    }

    public PropertyAccess getPropertyAccess() {
        return propertyAccess;
    }

    public CatalogService getCatalogService() {
        return catalogService;
    }

    public UserService getUserService() {
        return userService;
    }

    public AjaxResponseRenderer getAjaxResponseRenderer() {
        return ajaxResponseRenderer;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public java.util.List<TagEntity> getProductTags(ProductEntity product) {
        java.util.List<TagEntity> allTags = getCatalogService().findAllTags(product);
        Map<PropertyEntity, List<TagEntity>> groupedTags = allTags.stream().collect(Collectors.groupingBy(TagEntity::getProperty));
        return groupedTags.entrySet().stream()
                .sorted((o1, o2) -> o1.getKey().getType().compareTo(o2.getKey().getType()))
                .map(t -> {
                    Type type = t.getKey().getType();
                    String data = Type.LIST.equals(type) ?
                            t.getValue().stream().map(k -> getCatalogService().findPropertyValue(Long.parseLong(k.getData())).getValue()).collect(Collectors.joining(",")) :
                            t.getValue().stream().map(TagEntity::getData).collect(Collectors.joining());
                    TagEntity tag = new TagEntity();
                    tag.setData(data);
                    tag.setProperty(t.getKey());
                    return tag;
                })
                .collect(Collectors.toList());
    }

//    public SignupResult signupUser(UserEntity.Type type, UserEntity user, String password1, String password2, Optional<Long> referrer) {
//        if (password1 == null || password2 == null || !password1.equals(password2)) {
//            return SignupResult.PASSWORD;
//        }
//        user.setPassword(passwordEncoder.encode(password1));
//        try {
//            user.setId(null);
//            user.setType(type);
//            user.setReferrer(referrer.map(t -> getUserService().findUser(t)).orElse(null));
//            user = getUserService().saveUser(user);
//            try {
//                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), password1);
//                token.setDetails(new WebAuthenticationDetails(getHttpServletRequest()));
//                Authentication authentication = authenticationProvider.authenticate(token);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } catch (AuthenticationException e) {
//                SecurityContextHolder.getContext().setAuthentication(null);
//            }
//        } catch (UserExistsException e) {
//            return SignupResult.DUPLICATE;
//        } catch (EmptyPasswordException e) {
//            return SignupResult.PASSWORD;
//        }
//        return SignupResult.OK;
//    }

    public OrderService getOrderService() {
        return orderService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}