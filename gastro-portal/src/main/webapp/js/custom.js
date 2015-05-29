var counter = 0;
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
    initTitle(jQuery(this).find("div.title"));
    initFineUploader(jQuery("div.upload-file"));
});
jQuery(document).ready(function () {
    jQuery("ul.dropdown-menu").each(function (e) {
        var parentWidth = jQuery(this).parent().innerWidth();
        var menuWidth = jQuery(this).innerWidth();
        var margin = (parentWidth / 2 ) - (menuWidth / 2);
        margin = margin + "px";
        jQuery(this).css("margin-left", margin);
    });
    jQuery("body").addClass(isMobile() ? "mobile" : "desktop");
    jQuery(".tip").tooltip({placement: "bottom"});
    jQuery(".popover-holder")
        .on('mouseenter', function () {
            jQuery(this).popover({placement: "bottom"}).popover('show');
        })
        .on('mouseleave', function () {
            jQuery(this).popover('hide');
        });
    initChosen(jQuery("select.chosen-select"));
    initLoginModal();
    initTitle(jQuery("div.title"));
    initFineUploader(jQuery("div.upload-file"));
});
function isMobile() {
    var ua = navigator.userAgent.toLowerCase(),
        isIOS = ua.match(/(iphone|ipod|ipad)/),
        isAmazon = ua.match(/(silk|kindle)/i),
        isAndroid = ua.match(/android/);
    return isIOS || isAmazon || isAndroid || ua.match(/(blackberry|bb10|mobi|tablet|playbook|opera mini|nexus 7)/i);
}

function initMainPage() {
    var current = Math.floor(Math.random() * 8) + 1;
    var zindex = 1;

    function nextBackground() {
        if (++current > 8) current = 1;
        var pic = jQuery(".main-img-after.main" + current);
        jQuery(pic).fadeIn(1500, function () {
            jQuery(".main-img-after").not(this).fadeOut(10);
        });
        setTimeout(nextBackground, 5000);
    }

    jQuery(".main-img-after.main" + current).fadeIn(0);
    setTimeout(nextBackground, 5000);
}

function activate_menu(el) {
    jQuery(el).closest(".office-menu").find(".sel").removeClass("sel");
    jQuery(el).addClass("sel");
}

function initTitle(el) {
    jQuery(el).each(function (i, e) {
        var h = jQuery(e).find("h1,h2");
        jQuery(h).css("width", realTitleWidth(jQuery(h)) + 50).addClass("line");
    });
}

function initChosen(el, onReady) {
    jQuery(el).on("chosen:ready", onReady).chosen({"width": "100%", allow_single_deselect: true});
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
    jQuery(items).find("a.deleteConfirm").click(function (event) {
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
            jQuery(this).find("img").css("opacity", "1");
        })
        .mouseleave(function () {
            jQuery(this).find("img").css("opacity", "0.9");
        });
    jQuery(".data", items).bind('click', function (e) {
        if (e.target.nodeName.toLowerCase() != 'a') {
            var url = "/product/" + jQuery(this).closest("div.product-item").attr("data-productaltid");
            window.location = url;
        }
        e.stopPropagation();
    });
}

function initProductCatalogFixed() {
    var newItems = jQuery("div.product-item.fixed");
    initProductInCatalog(newItems);
    jQuery(newItems).each(function (i, e) {
        var pic = jQuery(".pic", this);
        jQuery(".data", this).height(jQuery(this).height() - pic.height() - 20);
    }).fadeIn(50);
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
        var existingItemIds = jQuery.map(jQuery("div.product-item", target), function (n, i) {
            return jQuery(n).attr("id");
        });
        jQuery(newItems).each(function (i, e) {
            var eid = jQuery(e).attr("id");
            if (jQuery.inArray(eid, existingItemIds) < 0) jQuery(e).appendTo(target);
        });
        jQuery(target).freetile({
            animate: false,
            selector: ".product-item",
            containerResize: false,
            callback: function () {
                var items = jQuery("div.product-modal-trigger", target);
                jQuery(items)
                    .filter(function () {
                        return jQuery(this).css("display") == "none";
                    }).fadeIn(100);
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
    jQuery(window).scroll(function () {
        if (scrollMutex && jQuery(window).scrollTop() + jQuery(window).height() > jQuery(document).height() - 50) {
            scrollMutex = false;
            triggerEvent(jQuery('a[id^=fetchProducts]').get(0), 'click');
        }
    });
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
                endpoint: '/upload?file_type=' + fileType + '&object_id=' + objectId
            },
            validation: {
                allowedExtensions: ['jpeg', 'jpg', 'png'],
                sizeLimit: 10485760,
                itemLimit: 1
            }
        }).on("complete", function (id, name, responseJSON, xhr) {
            var d = new Date();
            jQuery(e).find("img").attr("src", xhr[respSize] + "?" + d.getTime());
        });
    })
}
function initImportPage() {
    jQuery(".import-source").each(function (i, e) {
        Event.observe(jQuery("div.elements-zone", e).get(0), Tapestry.ZONE_UPDATED_EVENT, function (event) {
            var target = jQuery(".grid-block.import", e);
            jQuery(".element-item", e).appendTo(target);
            jQuery(".element-item", target).filter(function () {
                return jQuery(this).css("display") == "none";
            }).fadeIn(100);
            jQuery(".more-elements .more-link a", e).unbind('click').bind('click', function (btn) {
                jQuery(btn.target).closest("div.more-link").hide();
                jQuery(".spinner.fetch", e).removeClass("hidden");
                triggerEvent(jQuery("a.fetch", e).get(0), "click");
            });
        });
        setTimeout(function () {
            triggerEvent(jQuery("a.initial-fetch", e).get(0), "click")
        }, 500);
        jQuery(".album").bind('click', function () {
            jQuery(this).closest(".import-source").find(".grid-block.import").find(".element-item").remove();
        })
    });
}
function initWizardPage() {
    var step1 = jQuery("div.step1-zone");
    if (step1.length != 0) {
        step1.show();
        Event.observe(step1.get(0), Tapestry.ZONE_UPDATED_EVENT, function (event) {
            jQuery("div.step1-zone").fadeIn(100);
        });
    }
}
function addMoreProperties(el) {
    var listBlock = jQuery(el);
    var lastBlock = jQuery('div.prop-edit-block:last', listBlock);
    var newBlock = jQuery(lastBlock).clone();
    jQuery(newBlock).find(".chosen-container").remove();
    initPropEdit(newBlock);
    jQuery(newBlock).insertAfter(lastBlock);
}
function initPropEdit(blocks) {
    jQuery(blocks).each(function (i, block) {
        counter++;
        var select = jQuery("select", block).each(function (i, select) {
            var selectName = jQuery(select).attr("name");
            jQuery(select).attr("name", selectName.substring(0, selectName.lastIndexOf("-")) + "-" + counter);
        });
        jQuery("select.parent-value", block).each(function (i, parentSelect) {
            initChosen(jQuery(parentSelect));
            jQuery(parentSelect).on('change', function (evt, params) {
                var propId = jQuery(this).attr("data-property");
                var container = jQuery(this).next(".chosen-container");
                var subSelect = jQuery("select[name^='sublist-" + (params == undefined || params.length == 0 ? "none" : params.selected) + "']", block);
                var len = jQuery("option", subSelect).length > 1 ? 250 : 510;
                //Destroy prev subs selected
                jQuery("select.sublist-" + propId, block)
                    .filter(function () {
                        return jQuery(this).data('chosen') != undefined;
                    })
                    .chosen("destroy")
                    .css("display", "none");
                //Animate main select
                if (jQuery(container).innerWidth() != len) {
                    jQuery(container).animate({width: len}, {
                        duration: 100,
                        step: function (now, fx) {
                            jQuery(container).attr('style', 'width: ' + now + 'px!important');
                        },
                        complete: function () {
                            showSubSelect(subSelect);
                        }
                    });
                } else {
                    showSubSelect(subSelect);
                }
            });
        });
        jQuery("select.child-value.show-true", block).each(function (i, subSelect) {
            jQuery(this).closest("div.prop-edit-block")
                .find("select.parent-value").next(".chosen-container")
                .attr('style', 'width: 250px!important');
            showSubSelect(subSelect);
        });
    });
}
function showSubSelect(el) {
    el = el.get(0);
    if (el != undefined && el.length != 0 && jQuery("option", el).length > 1) {
        initChosen(el, function () {
            jQuery(this).next(".chosen-container").attr('style', 'width: 250px!important;margin-left:10px;');
        });
    }
};
function realTitleWidth(obj) {
    var clone = obj.clone();
    clone.css("visibility", "hidden");
    jQuery('body').append(clone);
    var width = clone.find("span").width();
    clone.remove();
    return width;
}

function triggerEvent(element, eventName) {
    if (element && eventName) {
        if (document.createEvent) {
            var evt = document.createEvent('HTMLEvents');
            evt.initEvent(eventName, true, true);
            return element.dispatchEvent(evt);
        } else if (element.fireEvent) {
            return element.fireEvent('on' + eventName);
        }
    }
}