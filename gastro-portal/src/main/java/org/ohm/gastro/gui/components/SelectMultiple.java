package org.ohm.gastro.gui.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.BeforeRenderTemplate;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.ohm.gastro.domain.BaseEntity;
import org.ohm.gastro.gui.misc.MultiValueEncoder;
import org.ohm.gastro.gui.misc.SelectMultipleModelRenderer;

import java.util.List;

@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public final class SelectMultiple extends AbstractField {

    private class Renderer extends SelectMultipleModelRenderer {

        public Renderer(MarkupWriter writer) {
            super(writer, encoder);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected boolean isOptionSelected(OptionModel optionModel) {
            return values.stream().anyMatch(t -> t.equals(optionModel.getValue()));
        }

    }

    @Inject
    private Request request;

    @Inject
    private ComponentResources _resources;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Inject
    @Symbol(SymbolConstants.FORM_FIELD_CSS_CLASS)
    protected String cssClass;

    @Parameter(required = true, principal = true)
    private List<BaseEntity> values;

    @Parameter(required = true)
    private SelectModel model;

    @Parameter
    private MultiValueEncoder encoder;

    @Override
    public final void processSubmission(String elementName) {
        String[] primaryKeys = request.getParameters(elementName);
        values = encoder.toValue(primaryKeys);
    }

    public final void afterRender(MarkupWriter writer) {
        writer.end();
    }

    public final void beginRender(MarkupWriter writer) {
        writer.element("select", "name", getControlName(), "id", getClientId(), "multiple", "multiple", "class", cssClass);
    }

    @BeforeRenderTemplate
    public final void options(MarkupWriter writer) {
        SelectModelVisitor renderer = new Renderer(writer);
        model.visit(renderer);
    }

}