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
});
jQuery(document).on(Tapestry.ZONE_UPDATED_EVENT, function (event) {
    alert(event);
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
function initLoginModal() {
    jQuery([".login-link", ".signup-link", ".forget-link", ".result"], ".modal-dialog").hide();
    jQuery(".forget-link").unbind("click").bind("click", function (e) {
        jQuery(".modal-dialog.login").hide();
        jQuery(".modal-dialog.remember").show();
        jQuery(".modal-dialog.signup").hide();
        jQuery(".modal-body.result").hide();
        jQuery(".modal-body.data").show();
    });
    jQuery(".login-link").unbind("click").bind("click", function (e) {
        jQuery(".modal-dialog.login").show();
        jQuery(".modal-dialog.remember").hide();
        jQuery(".modal-dialog.signup").hide();
        jQuery(".modal-body.result").hide();
        jQuery(".modal-body.data").show();
    });
    jQuery(".signup-link").unbind("click").bind("click", function (e) {
        jQuery(".modal-dialog.login").hide();
        jQuery(".modal-dialog.remember").hide();
        jQuery(".modal-dialog.signup").show();
        jQuery(".modal-body.result").hide();
        jQuery(".modal-body.data").show();
    });
}
function showModalResult(block) {
    jQuery(block).find(".data").hide();
}