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
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private PropertyAccess access;

    @Inject
    private Messages messages;

    @Inject
    private ApplicationGlobals globals;

    @Inject
    private PropertyAccess propertyAccess;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private CatalogService catalogService;

    @Inject
    private ProductService productService;

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
        return getAuthenticatedUserOpt().orElseThrow(RuntimeException::new);
    }

    public Optional<UserEntity> getAuthenticatedUserOpt() {
        return getAuthenticatedUser(userService);
    }

    public static Optional<UserEntity> getAuthenticatedUser(UserService userService) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null || securityContext.getAuthentication() == null) {
            return Optional.empty();
        }
        Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal != null && principal instanceof UserEntity) {
            UserEntity user = (UserEntity) principal;
            return Optional.of(userService == null ? user : userService.findUser(user.getId()));
        }
        return Optional.empty();
    }

    public boolean isAuthenticated() {
        return request.getSession(false) != null && getAuthenticatedUserOpt().isPresent();
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

    public ProductService getProductService() {
        return productService;
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

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public java.util.List<TagEntity> getProductTags(ProductEntity product) {
        java.util.List<TagEntity> allTags = getProductService().findAllTags(product);
        Map<PropertyEntity, List<TagEntity>> groupedTags = allTags.stream().collect(Collectors.groupingBy(TagEntity::getProperty));
        return groupedTags.entrySet().stream()
                .sorted((o1, o2) -> o1.getKey().getType().compareTo(o2.getKey().getType()))
                .map(t -> {
                    Type type = t.getKey().getType();
                    String data = Type.LIST.equals(type) ?
                            t.getValue().stream().map(k -> getCatalogService().findPropertyValue(Long.parseLong(k.getData())).getValue()).collect(Collectors.joining(", ")) :
                            t.getValue().stream().map(TagEntity::getData).collect(Collectors.joining());
                    TagEntity tag = new TagEntity();
                    tag.setData(data);
                    tag.setProperty(t.getKey());
                    return tag;
                })
                .collect(Collectors.toList());
    }

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