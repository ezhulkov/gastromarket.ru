var app = angular.module("gastroApp", []);
app.filter('raw', function ($sce) {
    return function (val) {
        return $sce.trustAsHtml(val);
    };
});
app.factory('wsMessage', [function () {
    var wsUrl = "wss://gastromarket.ru/chat";
    //var wsUrl = "ws://localhost:8080/chat";
    var ws = new ReconnectingWebSocket(wsUrl, null, {debug: false, reconnectInterval: 3000, maxReconnectAttempts: 10});
    var callbacks = [];
    ws.onmessage = function (message) {
        var json = JSON.parse(message.data);
        for (var i = 0; i < callbacks.length; i++) {
            callbacks[i](json);
        }
    };
    return {
        onMessage: function (f) {
            callbacks.push(f);
        }
    }
}]);
app.controller('headerCtrl', ['$http', '$scope', '$timeout', 'wsMessage', function ($http, $scope, $timeout, wsMessage) {
    var snd = new Audio("/sound/message.mp3");
    $http.get("/message?type=unread").success(function (data) {
        if (data.unread != 0) $scope.unread = "+" + data.unread;
    });
    wsMessage.onMessage(function (data) {
        if (snd != undefined) snd.play();
        $scope.$apply(function () {
            if (data.totalUnread != 0) $scope.unread = "+" + data.totalUnread;
        });
    });
}]);
app.controller('messagesCtrl', ['$http', '$scope', '$timeout', 'wsMessage', function ($http, $scope, $timeout, wsMessage) {
    $scope.init = function (aid, oid) {
        $scope.aid = aid;
        $scope.oid = oid;
    };
    wsMessage.onMessage(function (data) {
        if (data.author.id == $scope.aid && data.opponent.id == $scope.oid) {
            var msg = jQuery("#messages-block-{0}-{1}".format($scope.aid, $scope.oid));
            jQuery(".comment", msg).removeClass("read").addClass("unread");
            jQuery(".date", msg).html(data.messages[0].datePrintable);
            jQuery(".text", msg).html(data.messages[0].text);
        }
    });
}]);
app.controller('messageCtrl', ['$http', '$scope', '$timeout', 'wsMessage', function ($http, $scope, $timeout, wsMessage) {
    $scope.message = {};
    $scope.text = '';
    $scope.messages = [];
    $scope.scrollUp = false;
    $scope.photoBig = "/img/product-stub-1000x720.png";
    $scope.start = function (aid, oid) {
        $scope.aid = aid;
        $scope.oid = oid;
        $scope.refreshMessages();
        $scope.fitMessagesBlock();
    };
    $scope.init = function (aid, oid, modal) {
        $scope.modal = modal;
        if (!modal) {
            $scope.start(aid, oid);
        }
    };
    $scope.submit = function () {
        if ($scope.text) {
            $http.post("/message?type=text&aid={0}&oid={1}".format($scope.aid, $scope.oid), $scope.text).success(function (data) {
                if ($scope.messages == undefined) $scope.messages = [];
                $scope.messages.push(data.messages[0]);
                $scope.text = "";
                jQuery("#text-input").css("height", "");
                $scope.fitMessagesBlock();
            });
        }
    };
    $scope.fitMessagesBlock = function () {
        if ($scope.modal) {
            jQuery("#chat-cook .modal-body").css("height", (jQuery(window).height() - 200) + "px");
            jQuery("#messages").css("padding-bottom", (jQuery("#post").height() - 10) + "px");
        } else {
            jQuery("#messages").css("padding-bottom", (jQuery("#post").height() - 50) + "px");
        }
    };
    $scope.refreshMessages = function () {
        $http.get("/message?type=list&aid={0}&oid={1}".format($scope.aid, $scope.oid)).success(function (data) {
            $scope.messages = data.messages;
            $scope.messagesCount = data.messagesCount;
        });
    };
    $scope.hasPhoto = function (message) {
        return message.photoSmall != undefined;
    };
    $scope.needMore = function () {
        return $scope.messages != undefined && $scope.messagesCount != undefined && $scope.messagesCount > $scope.messages.length;
    };
    $scope.zoomPhoto = function (url) {
        $scope.photoBig = url;
        jQuery("#bigPhoto").modal("show");
    };
    $scope.loadMore = function () {
        $http.get("/message?type=list&aid={0}&oid={1}&from={2}&to={3}".format($scope.aid, $scope.oid, $scope.messages.length, $scope.messages.length + 20))
            .success(function (data) {
                $scope.scrollUp = true;
                $scope.messages = data.messages.concat($scope.messages);
            });
    };
    $scope.$watchCollection("messages", function () {
        $timeout(function () {
            if (!$scope.scrollUp) {
                if ($scope.modal) {
                    var lastMessage = jQuery("#messages .message:last");
                    if (lastMessage != undefined && lastMessage.offset() != undefined) jQuery("#chat-cook .modal-body").scrollTop(lastMessage.offset().top);
                } else {
                    jQuery("html, body").scrollTop(jQuery(document).height());
                }
                $scope.scrollUp = false;
            }
        }, 0);
    });
    wsMessage.onMessage(function (data) {
        $scope.$apply(function () {
            if (data.author.id == $scope.aid && data.opponent.id == $scope.oid) $scope.messages.push(data.messages[0]);
        });
    });
    jQuery(document).keydown(function (e) {
        if ((e.keyCode == 10 || e.keyCode == 13) && e.ctrlKey) {
            $scope.submit();
        }
        e.stopPropagation();
    });
    jQuery("#text-input")
        .focus()
        .each(function () {
            autosize(this);
        })
        .on("autosize:resized", function () {
            $scope.fitMessagesBlock();
            jQuery("html, body").scrollTop(jQuery(document).height());
        });
}]);
//if (!window.location.pathname.startsWith("/tender/")) angular.bootstrap(jQuery("body"), ["gastroApp"]);