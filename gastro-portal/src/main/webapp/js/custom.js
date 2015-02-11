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
    initChosen(jQuery(this).find('select.chosen-select'));
});
jQuery(document).ready(function () {
    jQuery("ul.dropdown-menu").each(function (e) {
        var parentWidth = jQuery(this).parent().innerWidth();
        var menuWidth = jQuery(this).innerWidth();
        var margin = (parentWidth / 2 ) - (menuWidth / 2);
        margin = margin + "px";
        jQuery(this).css("margin-left", margin);
    });
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
function showProductModal(pid) {
    var modal = jQuery("#product-modal-template");
    var productMain = jQuery("div[data-productid='" + pid + "']:first");
    var product = jQuery(productMain).find(".modal-data");
    var block1 = modal.find(".block1");
    var block2 = modal.find(".block2");
    var block3 = modal.find(".block3");
    var layoutBlock3 = function () {
        block3.show();
        block3.height(block1.height() - block3.position().top);
    }
    //Init blocks
    if (product.find("div.has-block2").text() == 'true') {
        block2.show();
    } else {
        block2.hide();
    }
    block1.find("a.product-link").attr("href", product.find("div.url").text());
    block1.find("img.product-image").attr("src", product.find("div.image").text());
    block1.find("div.category").empty().append(product.find(".category").clone().children());
    block1.find("div.cook").empty().append(product.find(".cook").clone().children());
    block1.find("div.name").empty().append(product.find(".name").clone());
    block1.find("div.price").empty().append(product.find(".price").clone());
    block1.find("div.basket").empty().append(product.find(".basket").clone().click(function () {
        triggerEvent(productMain.find("a.basket-add").get(0), "click");
    }));
    block2.find("div.desc").empty().append(product.find(".desc").clone().html());
    block2.find("div.tags").empty().append(product.find(".tags").clone().children());
    block3.hide();
    //Start Modal
    if (!modal.hasClass("in")) {
        modal.modal('show')
            .unbind('shown.bs.modal').bind('shown.bs.modal', function () {
                layoutBlock3();
                return false;
            })
            .unbind('hidden.bs.modal').bind('hidden.bs.modal', function () {
                jQuery(document).unbind('keydown')
            });
    } else {
        layoutBlock3();
    }
    //Navigation
    modal.find(".modal-arrow").unbind('click').bind('click', function () {
        if (jQuery(this).hasClass("arrow-left"))productLeftScroll(pid);
        if (jQuery(this).hasClass("arrow-right"))productRightScroll(pid);
    });
    jQuery(document).unbind('keydown').bind('keydown', function (e) {
        if (e.keyCode == 37) productLeftScroll(pid);
        if (e.keyCode == 39) productRightScroll(pid);
        return false;
    });
    //Download recommended
    triggerEvent(jQuery(".recommended-link", productMain).get(0), 'click');
}
function productLeftScroll(pid) {
    var productMain = jQuery("div[data-productid='" + pid + "']:first");
    pid = productMain.prev().attr("data-productid");
    if (pid != undefined) showProductModal(pid);
}
function productRightScroll(pid) {
    var productMain = jQuery("div[data-productid='" + pid + "']:first");
    var pid = productMain.next().attr("data-productid");
    if (pid != undefined) showProductModal(pid);
}
function initProductInCatalog(items) {
    jQuery(items).find("a.delete").click(function (event) {
        var productMain = jQuery(this).closest(".product-item");
        productMain.fadeOut(300, function () {
            jQuery(this).remove();
        });
        event.stopPropagation();
    });
    jQuery(items).find("img.product-image").click(function () {
        showProductModal(jQuery(this).closest("div.product-item").attr("data-productid"));
    });
    jQuery(items)
        .mouseenter(function () {
            jQuery(this).find("img").css("opacity", "0.8");
        })
        .mouseleave(function () {
            jQuery(this).find("img").css("opacity", "1");
        });
}
function initProductCatalogFixed() {
    var newItems = jQuery("div.product-item.fixed");
    initProductInCatalog(newItems);
    jQuery(newItems).each(function (i, e) {
        var pic = jQuery(".pic", this);
        jQuery(".data", this).height(jQuery(this).height() - pic.height() - 20);
    }).fadeIn(1000);
    initBasket();
}
function initProductCatalogEdit() {
    jQuery(".product-item")
        .mouseenter(function () {
            jQuery("div.pic .edit-block", this).css("display", "block");
        }).mouseleave(function () {
            jQuery("div.pic .edit-block", this).css("display", "none");
        })
}
function initProductCatalog(ajaxContainer) {
    var layoutFunction = function (target) {
        var newItems = jQuery("div.product-item", jQuery("div[id^='productsZone']"));
        jQuery(newItems).css("display", "none");
        jQuery(newItems).appendTo(target);
        jQuery(target).freetile({
            animate: false,
            selector: ".product-item",
            containerResize: false,
            callback: function () {
                var items = jQuery("div.product-modal-trigger");
                jQuery(items)
                    .filter(function () {
                        return jQuery(this).css("display") == "none";
                    })
                    .fadeIn(1000);
                initProductInCatalog(items);
            }
        });
    }
    layoutFunction(jQuery("#product-items"));
    initBasket();
    var scrollMutex = true;
    Event.observe(ajaxContainer.get(0), Tapestry.ZONE_UPDATED_EVENT, function (event) {
        layoutFunction(jQuery("#product-items"));
        setTimeout(function () {
            scrollMutex = true;
        }, 500);
    });
    //jQuery(window).scroll(function () {
    //    if (scrollMutex && jQuery(window).scrollTop() + jQuery(window).height() > jQuery(document).height() - 50) {
    //        scrollMutex = false;
    //        triggerEvent(jQuery('a[id^=fetchProducts]').get(0), 'click');
    //    }
    //});
}
function initBasket() {
    Event.observe(jQuery("span[id^='basketZone']").get(0), Tapestry.ZONE_UPDATED_EVENT, function (event) {
        jQuery(".basket-show").show()
            .mouseenter(function () {
                jQuery(this).stop().fadeIn(100);
            })
            .mouseleave(function () {
                jQuery(this).stop().fadeOut(1000);
            })
            .delay(2000).fadeOut(1000);
    });
}
function initProductsEdit() {
    jQuery("div.product-edit-modal").each(function (i, e) {
        initProductEdit(e);
    });
}
function initProductEdit(el) {
    toggleProductEdit(el, false);
}
function addMoreProperties(product, el) {
    var listBlock = jQuery(el, product);
    var lastList = jQuery('select:last', listBlock);
    var newList = jQuery(lastList).clone();
    jQuery(newList).insertAfter(lastList);
    initChosen(jQuery(newList));
}
function toggleProductEdit(el, closeModal) {
    if (closeModal) {
        modal = jQuery(el).closest(".product-edit-modal");
        modal.modal('hide');
        initProductEdit(modal);
        jQuery("body").removeClass("modal-open");
    } else {
        jQuery(".product-details", el).addClass("hidden");
        jQuery(".properties-details", el).addClass("hidden");
        jQuery(".photo-details", el).addClass("hidden");
        var type = jQuery(el).find("input[name='stage']").attr('value');
        if (type == 'DESC') jQuery(".product-details", el).removeClass("hidden");
        if (type == 'PROP') jQuery(".properties-details", el).removeClass("hidden");
        if (type == 'PHOTO') jQuery(".photo-details", el).removeClass("hidden");
    }
    initTitle(jQuery("div.title", el));
    initFineUploader(jQuery(el).find("div.upload-file"));
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