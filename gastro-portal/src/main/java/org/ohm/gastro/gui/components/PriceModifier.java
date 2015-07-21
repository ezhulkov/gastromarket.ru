package org.ohm.gastro.gui.components;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PriceEntity;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class PriceModifier extends BaseComponent {

    @Property
    @Parameter(required = true, allowNull = false)
    private PriceEntity object;

    private PriceModifierEntity modifier;
    private List<PriceModifierEntity> submittedModifiers = Lists.newArrayList();

    public PriceModifierEntity getModifier() {
        return modifier;
    }

    public void setModifier(PriceModifierEntity modifier) {
        this.modifier = modifier;
        if (modifier.getId() == null || !submittedModifiers.contains(modifier)) submittedModifiers.add(modifier);
    }

    public List<PriceModifierEntity> getModifiers() {
        if (object.getId() == null) return Lists.newArrayList();
        return getProductService().findAllModifiers(object);
    }

    public ValueEncoder<PriceModifierEntity> getFormInjectorEncoder() {
        return new ValueEncoder<PriceModifierEntity>() {
            @Override
            public String toClient(PriceModifierEntity value) {
                return value.getId() == null ? "" : value.getId().toString();
            }

            @Override
            public PriceModifierEntity toValue(String id) {
                return StringUtils.isEmpty(id) ? new PriceModifierEntity() : getProductService().findPriceModifier(Long.parseLong(id));
            }
        };
    }

    public PriceModifierEntity onAddRow() {
        return new PriceModifierEntity();
    }

    public List<PriceModifierEntity> getSubmittedModifiers() {
        return submittedModifiers;
    }

}
