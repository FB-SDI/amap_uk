app.controller('notificationDashboardController', ['$routeParams', '$scope', '$rootScope', 'locationMapService', 'getDataService', 'notificationDashboardService', '$interval', function($routeParams, $scope, $rootScope, locationMapService, getDataService, notificationDashboardService, $interval) {

    notificationDashboardService.getNotificationData().then(function(resp) {
        $scope.notificationData = resp.data.notificationData;

    });
    $interval(function() {
        notificationDashboardService.getNotificationData().then(function(resp) {
            $scope.notificationData = resp.data.notificationData;

        });
    }, 15000000000000);
    $scope.orderByDate = function(item) {
        var parts = item.dateStamp.split('/');
        var number = parseInt(parts[2] + parts[1] + parts[0]);

        return -number;
    };
    $scope.orderByTime = function(item) {
        var number;
        var parts = item.messageTime.split(':');
        //var number = parseInt(parts[2] + parts[1] + parts[0]);
        var subPart = parts[1].split(' ');
        console.log("part1 " + parts[0] + "part2 " + subPart[0] + "part3 " + subPart[1]);
        if (subPart[1] == "AM") {
            number = 100000;
        } else {
            number = 200000;
        }
        number = number + parseInt(parts[0]) + parseInt(subPart[0]);
        return -number;
    };
    $scope.notificationFilterList = {
        FAILURE: false,
        PREDICTING_FAILURE: false,
        UNDERGOING_MAINTENANCE: false,
        NO_DATA: false
    };

    $scope.all = function() {
        $scope.notificationFilterList.FAILURE = false;
        $scope.notificationFilterList.PREDICTING_FAILURE = false;
        $scope.notificationFilterList.UNDERGOING_MAINTENANCE = false;
        $scope.notificationFilterList.NO_DATA = false;
    }
    $scope.failure = function() {
        $scope.notificationFilterList.FAILURE = !$scope.notificationFilterList.FAILURE;
    }
    $scope.failurePred = function() {
        $scope.notificationFilterList.PREDICTING_FAILURE = !$scope.notificationFilterList.PREDICTING_FAILURE;
    }
    $scope.UnderMaintainance = function() {
        $scope.notificationFilterList.UNDERGOING_MAINTENANCE = !$scope.notificationFilterList.UNDERGOING_MAINTENANCE;
    }
    $scope.noData = function() {
        $scope.notificationFilterList.NO_DATA = !$scope.notificationFilterList.NO_DATA;
    }

}]);
app.filter('notificationFilter', function() {
    return function(notification, filterCheck) {
        var items = {
            filterCheck: filterCheck,
            out: []
        };
        if ((filterCheck.FAILURE || filterCheck.PREDICTING_FAILURE || filterCheck.NO_DATA || filterCheck.UNDERGOING_MAINTENANCE) === false) {
            items.out = notification;
        } else {
            angular.forEach(notification, function(value, key) {
                if (this.filterCheck[value.machineStatus] === true) {
                    this.out.push(value);
                }
            }, items);
        }
        return items.out;
    };
});