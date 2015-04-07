package org.ohm.gastro.gui.mixins;

import com.google.common.collect.Maps;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Hibernate;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyEntity.Type;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.filter.SocialFilter;
import org.ohm.gastro.gui.dto.ShoppingCart;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.MessageService;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.UserService;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseComponent {

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
    private PageRenderLinkSource pageLinkSource;

    @Inject
    private Messages messages;

    @Inject
    private ApplicationGlobals globals;

    @Inject
    private ApplicationContext applicationContext;

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
        return getAuthenticatedUserOpt()
                .map(UserEntity::isAdmin)
                .orElse(false);
    }

    public boolean isCook() {
        return getAuthenticatedUserOpt()
                .map(UserEntity::isCook)
                .orElse(false);
    }

    public boolean isUser() {
        final Optional<UserEntity> userOpt = getAuthenticatedUserOpt();
        return !userOpt.isPresent() || userOpt.map(UserEntity::isUser).orElse(false);
    }

    public String getContextPath() {
        final ServletContext servletContext = globals.getServletContext();
        return servletContext.getContextPath();
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

    public PageRenderLinkSource getPageLinkSource() {
        return pageLinkSource;
    }

    public ApplicationGlobals getGlobals() {
        return globals;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public OrderProductEntity createPurchaseItem(Long pid) {
        final OrderProductEntity purchaseItem = new OrderProductEntity();
        final ProductEntity product = productService.findProduct(pid);
        Hibernate.initialize(product);
        Hibernate.initialize(product.getCatalog());
        purchaseItem.setCount(0);
        purchaseItem.setPrice(product.getPrice());
        purchaseItem.setProduct(product);
        return purchaseItem;
    }

    @Cached
    public Map<String, Token> getTokens() {
        final Object tokensMap = httpServletRequest.getSession().getAttribute(SocialFilter.TOKENS);
        if (tokensMap != null && tokensMap instanceof Map) {
            return (Map<String, Token>) tokensMap;
        }
        return Maps.newHashMap();
    }

    public Optional<Token> getToken(String code) {
        return Optional.of(getTokens().get(code));
    }

}