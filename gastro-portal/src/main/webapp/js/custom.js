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
});

function activate_menu(el) {
    jQuery(el).closest(".office-menu").find(".sel").removeClass("sel");
    jQuery(el).addClass("sel");
}