package org.ohm.gastro.gui.components.comment;

import com.google.common.collect.Lists;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 13.08.15.
 */
public class Inject extends BaseComponent {

    @Property
    @Parameter
    private CommentEntity comment;

    @Property
    @Parameter
    private OrderEntity order;

    private PhotoEntity photo;

    @Component(id = "photoProduct", parameters = {"model=productsModel", "encoder=productsModel", "value=photo.product"})
    private Select productsField;

    @Component(id = "photoText", parameters = {"value=photo.text"})
    private TextField desc;

    private java.util.List<PhotoEntity> submittedPhotos = Lists.newArrayList();

    public List<PhotoEntity> getSubmittedPhotos() {
        return submittedPhotos;
    }

    @Cached
    public GenericSelectModel<ProductEntity> getProductsModel() {
        return new GenericSelectModel<>(getCatalogService().findAllCatalogs(getAuthenticatedUser()).stream()
                                                .flatMap(t -> getProductService().findProductsForFrontend(null, t, true, false, null, null, null, 0, Integer.MAX_VALUE).stream())
                                                .collect(Collectors.toList()),
                                        ProductEntity.class,
                                        "name", "id", getAccess());
    }

    public ValueEncoder<PhotoEntity> getFormInjectorEncoder() {
        return new ValueEncoder<PhotoEntity>() {
            @Override
            public String toClient(PhotoEntity value) {
                return value.getId().toString();
            }

            @Override
            public PhotoEntity toValue(String id) {
                return getPhotoService().findPhoto(Long.parseLong(id));
            }
        };
    }

    public PhotoEntity onAddRow() {
        return getPhotoService().createPhoto();
    }

    public void onRemoveRow(final PhotoEntity photo) {
        getPhotoService().deletePhoto(photo.getId());
    }

    public PhotoEntity getPhoto() {
        return photo;
    }

    public void setPhoto(final PhotoEntity photo) {
        this.photo = photo;
        if (photo.getId() == null || !submittedPhotos.contains(photo)) submittedPhotos.add(photo);
    }

    public java.util.List<PhotoEntity> getPhotos() {
        if (comment != null) {
            if (comment.getId() == null) return Lists.newArrayList();
            return getPhotoService().findAllPhotos(comment);
        } else {
            if (order.getId() == null) return Lists.newArrayList();
            return getPhotoService().findAllPhotos(order);
        }
    }

}
