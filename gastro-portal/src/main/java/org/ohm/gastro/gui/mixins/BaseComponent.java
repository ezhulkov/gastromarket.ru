package org.ohm.gastro.gui.mixins;

import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
    private CatalogService catalogService;

    @Inject
    private UserService userService;

    public UserDetails getAuthenticatedUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null || securityContext.getAuthentication() == null) {
            return null;
        }
        Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal != null && principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        return null;
    }

    public boolean isAuthenticated() {
        if (request.getSession(false) == null) {
            return false;
        }
        return getAuthenticatedUser() != null;
    }

    public boolean isAdmin() {
        UserDetails authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isUser() {
        UserDetails authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null && authenticatedUser.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
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
}