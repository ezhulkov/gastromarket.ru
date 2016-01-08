var counter = 0;
var titleDict = {
    "birthday": "Планируете день рождения?"
};
var textDict = {
    "birthday": "200 домашних кондитеров и кулинаров Москвы готовы сделать блюдо по индивидуальному заказу. <br/> Торты, капкейки, пироги, первые блюда и салаты — любые кулинарные шедевры для вашего дня рождения!"
};
var bgDict = {
    "birthday": "url(../img/landing-main.jpg)"
};
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
            all.on(eventName, function () {
                isBootstrapEvent = true;
            });
        });
    }
    Effect.DefaultOptions.duration = 0.3;
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
(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/ru_RU/sdk.js#xfbml=1&version=v2.3&appId=325659080959378";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
Event.observe(document, Tapestry.ZONE_UPDATED_EVENT, function () {
    initControls();
});
jQuery(document).ready(function () {
    jQuery("body").addClass(isMobile() ? "mobile" : "desktop");
    initLoginModal();
    initControls();
    initSmoothScroll();
});
function initSmoothScroll() {
    jQuery('a.smooth-scroll').click(function () {
        if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
            var target = jQuery(this.hash);
            target = target.length ? target : jQuery('[name=' + this.hash.slice(1) + ']');
            if (target.length) {
                jQuery('html,body').animate({
                    scrollTop: target.offset().top - 70
                }, 500);
                return false;
            }
        }
    });
}
function initControls() {
    initMobilePhone();
    initDatePicker();
    initToolTip();
    initChosen(jQuery("select.chosen-select"));
    initFineUploader(jQuery("div.upload-file"));
    initSortable();
    initModalStack();
    initBreadCrumbs();
}
function initBreadCrumbs() {
    var breadCrumbSelector = ".breadcrumbs";
    jQuery(breadCrumbSelector).ready(function () {
        jQuery.fn.hanselsRevenge({
            inheritLandingCrumbs: false,
            breadCrumbSelector: breadCrumbSelector,
            maxDepth: 3,
            debug: true,
            cookieOptions: {path: "/"}
        })
    });
}
function initDatePicker() {
    jQuery('.date-picker').datepicker({
        format: "dd/mm/yyyy",
        weekStart: 1,
        startDate: new Date(),
        todayBtn: true,
        language: "ru",
        autoclose: true,
        todayHighlight: true,
        autoPlaceholder: false
    });
}
function initMainPageMedia() {
    var term = getUrlVars()["utm_term"];
    if (term != undefined) {
        if (textDict[term] != undefined) jQuery("#main-text").html(textDict[term]);
        if (titleDict[term] != undefined) jQuery("#main-title").html(titleDict[term]);
        if (bgDict[term] != undefined) jQuery("#main-header").css("background-image", bgDict[term]);
    }
}
function initMobilePhone() {
    jQuery(document).ready(function () {
        jQuery(".mobile-phone").intlTelInput({
            onlyCountries: ["ru"],
            defaultCountry: "ru",
            utilsScript: "/js/intlTelInputUtils.min.js"
        });
    });
}
function initToolTip() {
    jQuery(".tip").tooltip({placement: "bottom"});
    jQuery("[data-toggle='popover']").popover();
}
function initModalStack() {
    jQuery(".modal").on("show.bs.modal", function () {
        if (jQuery(this).hasClass("img-big-modal") && !isMobile()) {
            jQuery(this).find(".img-big").css("height", Math.max(100, Math.min(720, jQuery(window).height() - 220)));
        }
    });
}
function initSortable() {
    jQuery(".sortable-container").sortable({
        placeholder: "ui-state-highlight",
        handle: ".handle",
        update: function (event) {
            var items = jQuery.map(jQuery(event.target).find(".item"), function (n) {
                return jQuery(n).attr("data-productid");
            });
            var form = jQuery(event.target).find("form.reorder");
            jQuery(form).find("input[type='text']").attr("value", items);
            triggerEvent(jQuery(form).find("input[type='submit']").get(0), 'click');
        }
    });
}
function isMobile() {
    var ua = navigator.userAgent.toLowerCase(),
        isIOS = ua.match(/(iphone|ipod|ipad)/),
        isAmazon = ua.match(/(silk|kindle)/i),
        isAndroid = ua.match(/android/);
    return isIOS || isAmazon || isAndroid || ua.match(/(blackberry|bb10|mobi|tablet|playbook|opera mini|nexus 7)/i);
}
function activate_menu(el) {
    jQuery(el).closest(".office-menu").find(".sel").removeClass("sel");
    jQuery(el).addClass("sel");
}
function initChosen(el, onReady) {
    jQuery(el).on("chosen:ready", onReady).chosen({"width": "100%", allow_single_deselect: true});
}
function showProductModal(pid) {
    if (isMobile()) return;
    var modal = jQuery("#product-modal-template");
    var productMain = jQuery("div[data-productid='" + pid + "']:first");
    var product = jQuery(productMain).find(".modal-data");
    var block1 = modal.find(".block1");
    var block2 = modal.find(".block2");
    var block3 = modal.find(".block3");
    var layoutBlock3 = function () {
        block3.show();
        block3.height(block1.height() - block3.position().top);
    };
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
    block1.find("div.pname").empty().append(product.find(".pname").clone().children());
    block1.find("div.basket-block").empty().append(product.find(".basket-block").clone().children());
    block1.find("div.basket-block").unbind('click').bind('click', function (e) {
        triggerEvent(productMain.find("a.basket-add").get(0), "click");
        e.stopPropagation();
        return false;
    });
    block2.find("div.desc").empty().append(product.find(".desc").clone().html());
    block2.find("div.price-modifier").empty().append(product.find(".price-modifier").clone().html());
    block2.find("div.tags").empty().append(product.find(".tags").clone().children());
    block3.hide();
    //Start Modal
    if (!modal.hasClass("in")) {
        modal.modal({show: true})
            .on('shown.bs.modal', function () {
                layoutBlock3();
                return false;
            })
            .on('hidden.bs.modal', function () {
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
    var pid2 = productMain.next().attr("data-productid");
    if (pid2 != undefined) showProductModal(pid2);
}

function initProductInCatalog(items) {
    jQuery(items).find("a.deleteConfirm").click(function (event) {
        var productMain = jQuery(this).closest(".product-item");
        productMain.fadeOut(300, function () {
            jQuery(this).remove();
        });
        event.stopPropagation();
    });
    jQuery(items).find("img.image").click(function () {
        showProductModal(jQuery(this).closest("div.product-item").attr("data-productid"));
    });
    jQuery(items)
        .mouseenter(function () {
            jQuery(this).find("img").css("opacity", "1");
        })
        .mouseleave(function () {
            jQuery(this).find("img").css("opacity", "0.9");
        });
}

function initProductCatalogFixed() {
    var newItems = jQuery("div.product-item.fixed");
    initProductInCatalog(newItems);
    jQuery(newItems).fadeIn(50);
    initBasket();
}
function initProductCatalog(ajaxContainer) {
    var layoutFunction = function (target) {
        var newItems = jQuery("div.product-item", jQuery("div[id^='productsZone']"));
        var existingItemIds = jQuery.map(jQuery("div.product-item", target), function (n) {
            return jQuery(n).attr("id");
        });
        jQuery(newItems).each(function (i, e) {
            jQuery(e).hide();
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
                    }).fadeIn(500);
                initProductInCatalog(items);
            }
        });
    };
    layoutFunction(jQuery("#product-items"));
    initBasket();
    var scrollMutex = true;
    Event.observe(ajaxContainer.get(0), Tapestry.ZONE_UPDATED_EVENT, function () {
        layoutFunction(jQuery("#product-items"));
        setTimeout(function () {
            scrollMutex = true;
        }, 500);
    });
    jQuery(window).scroll(function () {
        if (!isMobile()) {
            if (scrollMutex && jQuery(window).scrollTop() + jQuery(window).height() > jQuery(document).height() - 50) {
                scrollMutex = false;
                triggerEvent(jQuery('a[id^=fetchProductsAjaxLink]').get(0), 'click');
            }
        }
    });
    if (jQuery.cookie("tenderadv.shown") == undefined) {
        setTimeout(function () {
            jQuery("#tender-adv").modal("show");
            jQuery.cookie("tenderadv.shown", "true", {expires: 30, path: "/"});
        }, 10000);
    }
}

function initBasket() {
    Event.observe(jQuery("div[id^='orderShowZone']").get(0), Tapestry.ZONE_UPDATED_EVENT, function (event) {
        if (jQuery(event.target).attr("id").startsWith("orderShowZone")) {
            jQuery(".modal.in").modal('hide');
            jQuery("#order-new-modal").modal('show');
        }
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
    jQuery(".forget-link").unbind("click").bind("click", function () {
        hideAll();
        jQuery(".modal-dialog.remember").show();
        jQuery(".modal-body.data").show();
    });
    jQuery(".login-link").unbind("click").bind("click", function () {
        hideAll();
        jQuery(".modal-dialog.login").show();
    });
    jQuery(".signup-link").unbind("click").bind("click", function () {
        hideAll();
        jQuery(".modal-dialog.signup").show();
    });
    jQuery(".application-link").unbind("click").bind("click", function () {
        hideAll();
        jQuery(".modal-dialog.application").show();
    });
    hideAll();
    jQuery(".modal-dialog.login").show();
}
function showModalResult(block) {
    jQuery(block).find(".data").hide();
}
function initFineUploader(el) {
    jQuery(el).each(function (i, e) {
        var button = jQuery(".uploader-button-container", e);
        var fileType = jQuery(e).attr("data-type") || "";
        var respSize = jQuery(e).attr("data-size") || "";
        var objectId = jQuery(e).attr("data-objectid") || "";
        var targetContext = jQuery(e).attr("data-targetcontext") || "";
        var imageSelector = jQuery(e).attr("data-image") || "";
        var imageWidth = parseInt(jQuery(e).attr("data-image-width") || "210");
        var imageHeight = parseInt(jQuery(e).attr("data-image-height") || "210");
        var refreshAjax = jQuery(e).attr("data-refreshajax") || "";
        var autoUpload = Boolean(jQuery(e).attr("data-autoupload") === "true");
        var fineUploaderUrl = '/upload?file_type=' + fileType + '&object_id=' + objectId + '&target_context=' + targetContext;
        var fineUploader = jQuery(button).fineUploader({
            request: {
                endpoint: fineUploaderUrl
            },
            validation: {
                allowedExtensions: ['jpeg', 'jpg', 'png'],
                sizeLimit: 10485760,
                itemLimit: 1
            }
        }).on("submit", function (id, name) {
            var file = jQuery(this).fineUploader("getFile", 0);
            if (file.autoUpload != true) {
                var image = jQuery(imageSelector);
                var btnContainer = jQuery("div[data-image='" + imageSelector + "']").find(".qq-upload-button-selector");
                if (autoUpload) {
                    jQuery(button).addClass("upload-progress");
                    return true;
                } else {
                    image.unbind("load").bind("load", function (id) {
                        jQuery(image).removeAttr("width").removeAttr("height").css("width", "").css("height", "");
                        image.guillotine("remove");
                        image.guillotine({width: imageWidth, height: imageHeight});
                        image.guillotine("fit");
                        image.unbind("upload").bind("upload", function (e) {
                            file.autoUpload = true;
                            var data = image.guillotine('getData');
                            fineUploader.fineUploader("setEndpoint", fineUploaderUrl + "&s={0}&a={1}&x={2}&y={3}&w={4}&h={5}".format(data.scale, data.angle, data.x, data.y, data.w, data.h));
                            fineUploader.fineUploader("clearStoredFiles");
                            fineUploader.fineUploader("addFiles", file);
                            image.guillotine("disable");
                            jQuery(button).addClass("upload-progress");
                            jQuery(btnContainer).css("background", "none");
                            jQuery(btnContainer).css("width", "50px");
                            jQuery(btnContainer).find("input[type='file']").show();
                            jQuery(btnContainer).find(".qq-upload-button-tools").hide();
                            jQuery(btnContainer).find(".qq-upload-button-tools a").unbind("click")
                        });
                        jQuery(btnContainer).find(".btn-file").hide();
                        jQuery(btnContainer).css("background", "white")
                        jQuery(btnContainer).animate({
                            width: "175px"
                        }, 200, function () {
                            jQuery(btnContainer).find(".qq-upload-button-tools").show();
                            jQuery(btnContainer).find("input[type='file']").hide();
                            jQuery(btnContainer).find(".qq-upload-button-tools a").unbind("click").bind("click", function () {
                                image.guillotine(this.id);
                                jQuery(image).trigger(this.id);
                                return false;
                            });
                        });
                    });
                    var reader = new FileReader();
                    reader.onload = function (e) {
                        image.attr("src", e.target.result);
                    };
                    reader.readAsDataURL(file);
                }
            }
            return true;
        }).on("complete", function (id, name, responseJSON, xhr) {
            jQuery(button).removeClass("upload-progress");
            if (autoUpload) {
                if (respSize != undefined && respSize.length > 0 && xhr.response != undefined) {
                    jQuery(imageSelector).attr("src", JSON.parse(xhr.response)[respSize] + "?" + new Date().getTime());
                }
                if (refreshAjax != null) {
                    triggerEvent(jQuery(refreshAjax).get(0), "click");
                }
            }
        }).on("error", function (id, name, reason, xhr) {
            jQuery(button).removeClass("upload-progress");
        });
    });
}
function initImportPage() {
    jQuery(".import-item").each(function (i, e) {
        Event.observe(jQuery("div.elements-zone", e).get(0), Tapestry.ZONE_UPDATED_EVENT, function () {
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
            jQuery(this).closest(".import-item").find(".grid-block.import").find(".element-item").remove();
        })
    });
}
function initWizardPage() {
    var step1 = jQuery("div.step1-zone");
    if (step1.length != 0) {
        step1.show();
        Event.observe(step1.get(0), Tapestry.ZONE_UPDATED_EVENT, function () {
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
function initMessages() {
    jQuery(".messages .message .delete").on('click', function () {
        jQuery(this).closest(".message").fadeOut(200);
    });
}
function initMessagePage() {
    var postInput = jQuery(".post .text");
    var newMessagesZone = jQuery("#newMessagesZone");
    jQuery(document).ready(function () {
        initMessages();
        jQuery("html, body").scrollTop(jQuery(document).height());
    });
    Event.observe(document, Tapestry.ZONE_UPDATED_EVENT, function (event) {
        if (jQuery(event.target).attr("id").startsWith("messagesZone")) {
            window.scrollBy(0, jQuery(event.target).height() + 15);
        }
    });
    Event.observe(newMessagesZone.get(0), Tapestry.ZONE_UPDATED_EVENT, function () {
        jQuery("html, body").scrollTop(jQuery(document).height());
        jQuery(postInput).val("").focus();
        jQuery(this).find(".message").last().find(".text").fadeOut(0, function () {
            jQuery(this).fadeIn(700);
        });
    });
    jQuery(postInput)
        .focus()
        .each(function () {
            autosize(this);
        })
        .on("autosize:resized", function () {
            jQuery(".messages").css("padding-bottom", (isMobile() ? 80 : 60) + jQuery(this).height());
            jQuery("html, body").scrollTop(jQuery(document).height());
        });
    jQuery(document).keydown(function (e) {
        if ((e.keyCode == 10 || e.keyCode == 13) && e.ctrlKey) {
            e.preventDefault();
            e.stopPropagation();
            triggerEvent(jQuery("form.postform").find("input[type='submit']")[0], "click");
        }
    });
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
                var subSelect = jQuery("select[name^='sublist-" + (params == undefined || params.length == 0 ? "none" : params.selected) + "']", block)[0];
                var len = jQuery("option", subSelect).length > 1 ? 241 : 493;
                //Destroy prev subs selected
                jQuery("select.sublist-" + propId, block)
                    .filter(function () {
                        return jQuery(this).data('chosen') != undefined;
                    })
                    .chosen("destroy")
                    .css("display", "none");
                //Animate main select
                if (jQuery(container).innerWidth() != len) {
                    jQuery(container).attr('style', 'width: ' + len + 'px!important');
                    showSubSelect(subSelect);
                } else {
                    showSubSelect(subSelect);
                }
            });
        });
        jQuery("select.child-value.show-true", block).each(function (i, subSelect) {
            if (i === 0) {
                jQuery(this).closest("div.prop-edit-block")
                    .find("select.parent-value").next(".chosen-container")
                    .attr('style', 'width: 241px!important');
                showSubSelect(subSelect);
            }
        });
        jQuery("select.parent-value.open-value", block).each(function (i, select) {
            jQuery(select).on('chosen:no_results', function () {
                var container = jQuery(this).next(".chosen-container");
                jQuery(".no-results", container).on('click', function () {
                    var newValue = jQuery("span", this).text();
                    var newValueId = "new-{0}".format(newValue);
                    jQuery(select).append("<option value='{0}'>{1}</option>".format(newValueId, newValue));
                    jQuery(select).val(newValueId).trigger("chosen:updated");
                });
            });
        });
    });
}
function showSubSelect(el) {
    if (el != undefined) {
        el = el.tagName == 'SELECT' ? el : el.get(0);
        if (el != undefined && el.length != 0 && jQuery("option", el).length > 1) {
            initChosen(el, function () {
                jQuery(this).next(".chosen-container").attr('style', 'width: 241px!important;margin-left:10px;');
            });
        }
    }
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
function getUrlVars() {
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}
function onLogin() {
    connectWebSocket("wss://gastromarket.ru/chat");
    //connectWebSocket("ws://localhost:8080/chat");
}
function connectWebSocket(url) {
    var ws = new ReconnectingWebSocket(url, null, {debug: false, reconnectInterval: 3000, maxReconnectAttempts: 10});
    ws.onmessage = function (message) {
        var unread = JSON.parse(message.data)["unread"];
        if (unread != undefined)jQuery("#unread-messages").text("+" + unread);
    };
}
String.prototype.format = function () {
    var formatted = this;
    for (var i = 0; i < arguments.length; i++) {
        var regexp = new RegExp('\\{' + i + '\\}', 'gi');
        formatted = formatted.replace(regexp, arguments[i]);
    }
    return formatted;
};