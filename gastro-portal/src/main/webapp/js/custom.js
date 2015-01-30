jQuery.noConflict();
(function () {
    var isBootstrapEvent = false;
    if (window.jQuery) {
        var all = jQuery('*');
        jQuery.each(['hide.bs.dropdown',
            'hide.bs.collapse',
            'hide.bs.modal',
            'hide.bs.tooltip',
            'hide.bs.popover'], function (index, eventName) {
            all.on(eventName, function (event) {
                isBootstrapEvent = true;
            });
        });
    }
    var originalHide = Element.hide;
    Element.addMethods({
        hide: function (element) {
            if (isBootstrapEvent) {
                isBootstrapEvent = false;
                return element;
            }
            return originalHide(element);
        }
    });
})();
Event.observe(document, Tapestry.ZONE_UPDATED_EVENT, function (event) {
    initChosen(jQuery(event.srcElement).find('select.chosen-select'));
});
jQuery(document).ready(function () {
    jQuery(".tip").tooltip({placement: "bottom"});
    initChosen(jQuery("select.chosen-select"));
    initLoginModal();
    initTitle(jQuery("div.title"));
    initFineUploader(jQuery("div.upload-file"));
});
function activate_menu(el) {
    jQuery(el).closest(".office-menu").find(".sel").removeClass("sel");
    jQuery(el).addClass("sel");
}
function initTitle(el) {
    jQuery(el).each(function (i, e) {
        var h1 = jQuery(e).find("h1");
        jQuery(h1).css("width", realTitleWidth(jQuery(h1)) + 50);
    });
}
function initChosen(el) {
    jQuery(el).chosen({"width": "100%"}).on('change', function (e) {
        this.fire(Tapestry.ACTION_EVENT, e);
    });
}
function initProductCatalog(ajaxContainer) {
    var layoutFunction = function (source, target) {
        var newItems = jQuery("div.product-item", source);
        jQuery(newItems).css("display", "none");
        jQuery(newItems).appendTo(target);
        jQuery(target).freetile({
            animate: false,
            selector: ".product-item",
            containerResize: false,
            callback: function () {
                var items = jQuery("div.product-item");
                jQuery(items)
                    .filter(function () {
                        return jQuery(this).css("display") == "none";
                    })
                    .fadeIn(1000);
                jQuery(items)
                    .mouseenter(function () {
                        jQuery(this).find("img").css("opacity", "0.8");
                    }).mouseleave(function () {
                        jQuery(this).find("img").css("opacity", "1");
                    });
            }
        });
    }
    layoutFunction(jQuery("#productsZone"), jQuery("#product-items"));
    var scrollMutex = true;
    Event.observe(ajaxContainer.get(0), Tapestry.ZONE_UPDATED_EVENT, function (event) {
        layoutFunction(event.srcElement, jQuery("#product-items"));
        scrollMutex = true;
    });
    Event.observe(jQuery("span[id^='basketZone']").get(0), Tapestry.ZONE_UPDATED_EVENT, function (event) {
        jQuery(".basket-show").show()
            .mouseenter(function () {
                jQuery(this).stop().fadeIn(100);
            })
            .mouseleave(function () {
                jQuery(this).fadeOut(1000);
            })
            .fadeOut(3000);
    });
    jQuery(window).scroll(function () {
        if (scrollMutex && jQuery(window).scrollTop() + jQuery(window).height() > jQuery(document).height() - 50) {
            triggerEvent(jQuery('a[id^=fetchProducts]').get(0), 'click');
            scrollMutex = false;
        }
    });
}
function initProductsEdit() {
    var modals = jQuery("div.product-edit-modal");
    initProductEdit(modals);
    jQuery(modals).find("div.t-zone").each(function (i, e) {
        Event.observe(e, Tapestry.ZONE_UPDATED_EVENT, function (event) {
            initProductEdit(jQuery("div.product-edit-modal", event.srcElement));
        });
    })
}
function initProductEdit(el) {
    jQuery(el).each(function (i, e) {
        var linkId = jQuery(e).attr("id");
        jQuery("a[data-target='#" + linkId + "']").unbind("click").bind("click", function () {
            jQuery("div.t-error", e).hide();
            jQuery(".t-error", e).removeClass("t-error");
            jQuery(".error", e).hide();
            jQuery("input[name='stage']", e).attr('value', 'DESC');
            toggleProductEdit(e, false);
            initTitle(jQuery("div.title", e));
        });
    });
}
function addMoreProperties(el) {
    var listBlock = jQuery(el);
    var lastList = jQuery('select:last', listBlock);
    var newList = jQuery(lastList).clone();
    jQuery(newList).insertAfter(lastList);
    initChosen(jQuery(newList));
}
function toggleProductEdit(el, closeModal) {
    var type = jQuery(el).find("input[name='stage']").attr('value');
    if (closeModal) {
        jQuery(el).closest(".product-edit-modal").modal('hide');
        return;
    }
    initFineUploader(jQuery(el).find("div.upload-file"));
    jQuery(".product-details", el).hide();
    jQuery(".properties-details", el).hide();
    jQuery(".photo-details", el).hide();
    if (type == 'DESC') jQuery(".product-details", el).show();
    if (type == 'PROP') jQuery(".properties-details", el).show();
    if (type == 'PHOTO') jQuery(".photo-details", el).show();
}
function initLoginModal() {
    var hideAll = function () {
        jQuery(".modal-dialog.login").hide();
        jQuery(".modal-dialog.remember").hide();
        jQuery(".modal-dialog.application").hide();
        jQuery(".modal-dialog.signup").hide();
        jQuery(".modal-dialog div.t-error").hide();
        jQuery(".modal-dialog .error").hide();
        jQuery(".modal-body.result").hide();
        jQuery(".modal-body.data").hide();
        jQuery(".modal-body input.t-error").removeClass("t-error");
    };
    jQuery(".forget-link").unbind("click").bind("click", function (e) {
        hideAll();
        jQuery(".modal-dialog.remember").show();
        jQuery(".modal-body.data").show();
    });
    jQuery(".login-link").unbind("click").bind("click", function (e) {
        hideAll();
        jQuery(".modal-dialog.login").show();
    });
    jQuery(".signup-link").unbind("click").bind("click", function (e) {
        hideAll();
        jQuery(".modal-dialog.signup").show();
    });
    jQuery(".application-link").unbind("click").bind("click", function (e) {
        hideAll();
        jQuery(".modal-dialog.application").show();
    });
}
function showModalResult(block) {
    jQuery(block).find(".data").hide();
}
function initFineUploader(el) {
    jQuery(el).each(function (i, e) {
        var button = jQuery(".uploader-button", e);
        var fileType = jQuery(e).attr("data-type");
        var respSize = jQuery(e).attr("data-size");
        var objectId = jQuery(e).attr("data-objectid");
        button.fineUploader({
            request: {
                endpoint: '/upload?file_path=/tmp/1.jpg&file_type=' + fileType + '&object_id=' + objectId
            },
            validation: {
                allowedExtensions: ['jpeg', 'jpg'],
                sizeLimit: 10485760,
                itemLimit: 1
            }
        }).on("complete", function (id, name, responseJSON, xhr) {
            jQuery(e).find("img").attr("src", xhr[respSize]);
        });
    })
}
function realTitleWidth(obj) {
    var clone = obj.clone();
    clone.css("visibility", "hidden");
    jQuery('body').append(clone);
    var width = clone.find("span").width();
    clone.remove();
    return width;
}
function triggerEvent(element, eventName) {
    if (document.createEvent) {
        var evt = document.createEvent('HTMLEvents');
        evt.initEvent(eventName, true, true);
        return element.dispatchEvent(evt);
    } else if (element.fireEvent) {
        return element.fireEvent('on' + eventName);
    }
}