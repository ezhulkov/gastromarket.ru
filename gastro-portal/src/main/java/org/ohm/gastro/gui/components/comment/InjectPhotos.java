package org.ohm.gastro.gui.components.comment;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.PhotoEntity.Type;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 13.08.15.
 */
public class InjectPhotos extends BaseComponent {

    @Property
    @Parameter
    private CommentEntity comment;

    @Parameter
    private OfferEntity offer;

    @Parameter
    private OrderEntity order;

    @Parameter
    @Property
    private List<ProductEntity> products = Lists.newArrayList();

    @Parameter
    @Property
    private List<PhotoEntity> initialPhotos = Lists.newArrayList();

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String caption;

    @Component(id = "photoProduct", parameters = {"model=productsModel", "encoder=productsModel", "value=photo.product"})
    private Select productsField;

    @Component(id = "photoText", parameters = {"value=photo.text"})
    private TextField desc;

    private java.util.List<PhotoEntity> submittedPhotos = Lists.newArrayList();

    private PhotoEntity photo;

    public void purgeSubmittedPhotos() {
        this.submittedPhotos.clear();
    }

    public List<PhotoEntity> getSubmittedPhotos() {
        final List<PhotoEntity> photos = submittedPhotos.stream().filter(t -> t.getId() != null).collect(Collectors.toList());
        return Lists.newArrayList(photos);
    }

    public GenericSelectModel<ProductEntity> getProductsModel() {
        return new GenericSelectModel<>(products, ProductEntity.class, "name", "id", getAccess());
    }

    public ValueEncoder<PhotoEntity> getFormInjectorEncoder() {
        return new ValueEncoder<PhotoEntity>() {
            @Override
            public String toClient(PhotoEntity value) {
                return value.getId().toString();
            }

            @Override
            public PhotoEntity toValue(String id) {
                long pid = Long.parseLong(id);
                return getPhotoService().findPhoto(pid);
            }
        };
    }

    public PhotoEntity onAddRow() {
        return getPhotoService().createPhoto(getType());
    }

    public void onRemoveRow(final PhotoEntity photo) {
        if (photo != null) {
            submittedPhotos.remove(photo);
            getPhotoService().deletePhoto(photo.getId());
        }
    }

    public PhotoEntity getPhoto() {
        return photo;
    }

    public void setPhoto(final PhotoEntity photo) {
        this.photo = photo;
        if (photo.getId() == null || !submittedPhotos.contains(photo)) submittedPhotos.add(photo);
    }

    public OfferEntity getOffer() {
        return offer;
    }

    public void setOffer(final OfferEntity offer) {
        this.offer = offer;
    }

    @SuppressWarnings("unchecked")
    public java.util.List<PhotoEntity> getPhotos() {
        if (CollectionUtils.isNotEmpty(initialPhotos)) return getPhotoService().findAllPhotos(initialPhotos.stream().map(t -> t.getId()).collect(Collectors.toList()));
        if (comment != null) {
            if (comment.getId() == null) return Lists.newArrayList();
            return getPhotoService().findAllPhotos(comment);
        } else if (order != null) {
            if (order.getId() == null) return Lists.newArrayList();
            return getPhotoService().findAllPhotos(order);
        } else if (offer != null) {
            if (offer.getId() == null) return Lists.newArrayList();
            return getPhotoService().findAllPhotos(offer);
        }
        return Lists.newArrayList();
    }

    public PhotoEntity.Type getType() {
        if (comment != null) return Type.COMMENT;
        if (order != null) return Type.ORDER;
        if (offer != null) return Type.OFFER;
        return null;
    }

}
