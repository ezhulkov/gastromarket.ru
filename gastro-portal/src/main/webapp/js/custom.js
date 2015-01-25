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
    initProductEdit(jQuery("div.product-edit-modal"));
    initFineUploader(jQuery("div.upload-file"));
});
function activate_menu(el) {
    jQuery(el).closest(".office-menu").find(".sel").removeClass("sel");
    jQuery(el).addClass("sel");
}
function initChosen(el) {
    jQuery(el).chosen({"width": "100%"}).on('change', function (e) {
        this.fire(Tapestry.ACTION_EVENT, e);
    });
}
function initProductEdit(el) {
    jQuery(el).each(function (i, e) {
        var linkId = jQuery(e).attr("id");
        jQuery("#" + linkId).unbind("click").bind("click", function () {
            jQuery("div.t-error", e).hide();
            jQuery(".t-error", e).removeClass("t-error");
            jQuery(".error", e).hide();
            jQuery(".photo-details", e).hide();
            jQuery(".properties-details", e).hide();
            jQuery(".product-details", e).show();
            jQuery(".product-finish-close", e).hide();
        });
        jQuery(".more-btn", e).unbind().bind('click', function () {
            var id = jQuery(this).attr('data');
            var listBlock = jQuery('#list' + id);
            var lastList = jQuery('select:last', listBlock);
            var newList = jQuery(lastList).clone();
            jQuery(newList).insertAfter(lastList);
            initChosen(jQuery(newList));
        });
        jQuery(".uploader-button", e).on("complete", function (id, name, responseJSON, xhr) {
            jQuery(".product-finish-close").hide();
        });
    });
}
function toggleProductEdit(type, el) {
    jQuery(".product-details", el).hide();
    jQuery(".properties-details", el).hide();
    jQuery(".photo-details", el).hide();
    if (type == 'DESC') jQuery(".product-details", el).show();
    if (type == 'PROP') jQuery(".properties-details", el).show();
    if (type == 'PHOTO') jQuery(".photo-details", el).show();
    if (type == 'DONE') location.reload();
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