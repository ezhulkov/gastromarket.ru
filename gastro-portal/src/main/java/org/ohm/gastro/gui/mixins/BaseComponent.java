package org.ohm.gastro.gui.mixins;

import com.google.common.collect.Maps;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Hibernate;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyEntity.Type;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.filter.SocialFilter;
import org.ohm.gastro.gui.dto.ShoppingCart;
import org.ohm.gastro.service.BillService;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.OfferService;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.service.PhotoService;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.PropertyService;
import org.ohm.gastro.service.RatingService;
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
import java.util.Properties;
import java.util.stream.Collectors;

public abstract class BaseComponent {

    protected final Logger logger = LoggerFactory.getLogger(BaseComponent.class);

    @Inject
    private HttpServletRequest httpServletRequest;

    @Inject
    private Request request;

    @InjectService("properties")
    private Properties properties;

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
    private ComponentResources componentResources;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private BillService billService;

    @Inject
    private PhotoService photoService;

    @Inject
    private PropertyAccess propertyAccess;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private CatalogService catalogService;

    @Inject
    private MailService mailService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private RatingService ratingService;

    @Inject
    private ProductService productService;

    @Inject
    private OfferService offerService;

    @Inject
    private OrderService orderService;

    @Inject
    private UserService userService;

    @Inject
    private ConversationService conversationService;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @SessionState
    private ShoppingCart shoppingCart;

    @Cached
    public UserEntity getAuthenticatedUser() {
        return getAuthenticatedUserOpt().orElseThrow(RuntimeException::new);
    }

    @Cached
    public UserEntity getAuthenticatedUserSafe() {
        return getAuthenticatedUserOpt().orElse(null);
    }

    @Cached
    public Optional<UserEntity> getAuthenticatedUserOpt() {
        return getAuthenticatedUser(userService);
    }

    public static Optional<UserEntity> getAuthenticatedUser(UserService userService) {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null || securityContext.getAuthentication() == null) {
            return Optional.empty();
        }
        final Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal != null && principal instanceof UserEntity) {
            final UserEntity user = (UserEntity) principal;
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

    public String getCurrentPage() {
        return httpServletRequest.getRequestURL().toString();
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

    public ComponentResources getComponentResources() {
        return componentResources;
    }

    public java.util.List<TagEntity> getProductTags(ProductEntity product) {
        java.util.List<TagEntity> allTags = getProductService().findAllTags(product);
        Map<PropertyEntity, List<TagEntity>> groupedTags = allTags.stream().collect(Collectors.groupingBy(TagEntity::getProperty));
        return groupedTags.entrySet().stream()
                .sorted((o1, o2) -> o1.getKey().getType().compareTo(o2.getKey().getType()))
                .map(t -> {
                    final Type type = t.getKey().getType();
                    final String data = Type.LIST == type || Type.OPEN == type ?
                            t.getValue().stream().filter(k -> k.getValue() != null).map(k -> k.getValue().getName()).distinct().collect(Collectors.joining(", ")) :
                            t.getValue().stream().map(TagEntity::getData).collect(Collectors.joining());
                    TagEntity tag = new TagEntity();
                    tag.setData(data);
                    tag.setProperty(t.getKey());
                    return tag;
                })
                .collect(Collectors.toList());
    }

    public PropertyService getPropertyService() {
        return propertyService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public ConversationService getConversationService() {
        return conversationService;
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

    public RatingService getRatingService() {
        return ratingService;
    }

    public OfferService getOfferService() {
        return offerService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public PhotoService getPhotoService() {
        return photoService;
    }

    public BillService getBillService() {
        return billService;
    }

    public OrderProductEntity createPurchaseItem(PurchaseEntity.Type eType, Long eId) {
        final OrderProductEntity purchaseItem = new OrderProductEntity();
        final PurchaseEntity entity = eType == PurchaseEntity.Type.OFFER ? getOfferService().findOffer(eId) : getProductService().findProduct(eId);
        Hibernate.initialize(entity);
        Hibernate.initialize(entity.getCatalog());
        purchaseItem.setCount(1);
        purchaseItem.setPrice(entity.getPrice());
        purchaseItem.setEntity(entity);
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

    protected String getDeclInfo(String value, Integer count) {
        if (count == null || count == 0) return getMessages().format(value + ".decl.0", count);
        if (count == 1) return getMessages().format(value + ".decl.1", count);
        if (count <= 4) return getMessages().format(value + ".decl.2-4", count);
        if (count == 11) return getMessages().format(value + ".decl.11", count);
        if (count % 100 >= 11 && count % 100 <= 14) return getMessages().format(value + ".decl.*11-*14", count);
        if (count % 10 == 1) return getMessages().format(value + ".decl.*1", count);
        if (count % 10 >= 2 && count % 10 <= 4) return getMessages().format(value + ".decl.*2-*4", count);
        return getMessages().format(value + ".decl.*5-*0", count);
    }

    public List<PhotoEntity> getTenderPhotos() {
        final Session session = getRequest().getSession(false);
        return session.getAttributeNames().stream()
                .filter(t -> t.startsWith(FileType.TENDER.name()))
                .map(t -> {
                    final Map<ImageSize, String> imageUrls = (Map<ImageSize, String>) session.getAttribute(t);
                    final PhotoEntity photo = new PhotoEntity();
                    photo.setId(Long.parseLong(t.split("_")[1]));
                    photo.setAvatarUrlSmall(imageUrls.get(ImageSize.SIZE1));
                    photo.setAvatarUrl(imageUrls.get(ImageSize.SIZE2));
                    photo.setAvatarUrlBig(imageUrls.get(ImageSize.SIZE3));
                    return photo;
                }).collect(Collectors.toList());
    }

    public Optional<PhotoEntity> getTenderPhoto(Long id) {
        return getTenderPhotos().stream().filter(t -> id.equals(t.getId())).findFirst();
    }

    public boolean isProduction() {
        return Boolean.parseBoolean(properties.getProperty("production", "false"));
    }

    @Cached
    public String getCurrentRegionPrintable() {
        return getMessages().get("Region." + RegionFilter.getCurrentRegion());
    }

}