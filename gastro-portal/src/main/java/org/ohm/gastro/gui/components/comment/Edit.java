package org.ohm.gastro.gui.components.comment;

import com.google.common.collect.Lists;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.CommentableEntity.Type;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 13.08.15.
 */
public class Edit extends BaseComponent {

    @Property
    @Parameter
    private CommentEntity comment;

    @Property
    @Parameter
    private CommentableEntity entity;

    private PhotoEntity photo;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String modalId;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String title;

    @Component(id = "photoProduct", parameters = {"model=productsModel", "encoder=productsModel", "value=photo.product"})
    private Select productsField;

    @Component(id = "photoText", parameters = {"value=photo.text"})
    private TextField desc;

    @Property
    @Parameter
    private boolean commentAllowed;

    private java.util.List<PhotoEntity> submittedPhotos = Lists.newArrayList();

    @Cached
    public GenericSelectModel<ProductEntity> getProductsModel() {
        return new GenericSelectModel<>(getCatalogService().findAllCatalogs(getAuthenticatedUser()).stream()
                                                .flatMap(t -> getProductService().findProductsForFrontend(null, t, true, false, null, null, null, 0, Integer.MAX_VALUE).stream())
                                                .collect(Collectors.toList()),
                                        ProductEntity.class,
                                        "name", "id", getAccess());
    }

    public void onPrepareFromRateForm(Long cid) {
        submittedPhotos.clear();
        if (comment == null && entity != null && isAuthenticated()) {
            comment = new CommentEntity();
            comment.setEntity(entity);
            comment.setAuthor(getAuthenticatedUser());
        } else {
            comment = getRatingService().findComment(cid);
        }
    }

    public void onSuccessFromRateForm(Long cid) throws FileUploadException {
        java.util.List<FileItem> files = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(getHttpServletRequest()).stream()
                .filter(t -> "rateFile".equals(t.getName()))
                .filter(t -> t.getSize() != 0)
                .collect(Collectors.toList());
        getRatingService().rateCommentableEntity(comment.getEntity(), comment, getAuthenticatedUserOpt().orElse(null));
        getRatingService().attachPhotos(comment, submittedPhotos, files);
    }

    public boolean getLike() {
        return true;
    }

    public boolean getDislike() {
        return false;
    }

    public boolean isShowRate() {
        return comment.getEntity().getCommentableType() == Type.USER || comment.getEntity().getCommentableType() == Type.CATALOG;
    }

    public String getBtnTitle() {
        return isShowRate() ? getMessages().get("comment.rate") : getMessages().get("comment.add");
    }

    public Object[] getRateFormContext() {
        return new Object[]{comment == null ? null : comment.getId()};
    }

    public java.util.List<PhotoEntity> getPhotos() {
        if (comment.getId() == null) return Lists.newArrayList();
        return getImageService().findAllPhotos(comment);
    }

    public ValueEncoder<PhotoEntity> getFormInjectorEncoder() {
        return new ValueEncoder<PhotoEntity>() {
            @Override
            public String toClient(PhotoEntity value) {
                return value.getId() == null ? "" : value.getId().toString();
            }

            @Override
            public PhotoEntity toValue(String id) {
                return StringUtils.isEmpty(id) ? new PhotoEntity() : getImageService().findPhoto(Long.parseLong(id));
            }
        };
    }

    public PhotoEntity onAddRow() {
        return new PhotoEntity();
    }

    public PhotoEntity getPhoto() {
        return photo;
    }

    public void setPhoto(final PhotoEntity photo) {
        this.photo = photo;
        if (photo.getId() == null || !submittedPhotos.contains(photo)) submittedPhotos.add(photo);
    }

}
