'use strict';
app.controller('mapLocationController', ['$routeParams', '$scope', '$rootScope', 'locationMapService', 'getDataService', '$timeout', '$interval', 'analyticsMapService', function($routeParams, $scope, $rootScope, locationMapService, getDataService, $timeout, $interval, analyticsMapService) {

    $scope.location = [];
    $rootScope.activePage = 'analytics';

    $scope.mapFilterList = {
        PERFORMING_OPTIMALLY: false,
        FAILURE: false,
        PREDICTING_FAILURE: false,
        MAINTENANCE: false,
        NO_DATA: false
    };

    var filter = "all";
    showMapView();
    $scope.selectedFilterCheck = function(filterVerify) {
        return ($scope.selectedMapFilter == filterVerify);
    }

    function showMapView() {

        analyticsMapService.getMapLocationData().then(function(resp) {
            $scope.location = resp;
            var map = locationMapService.createOutdoorMap($scope.location);
            if (map)
                locationMapService.createMachineMarkers($scope.location, $scope.mapFilterList);

        });


        $scope.all = function() {
            $scope.mapFilterList.FAILURE = false;
            $scope.mapFilterList.PREDICTING_FAILURE = false;
            $scope.mapFilterList.UNDERGOING_MAINTENANCE = false;
            $scope.mapFilterList.PERFORMING_OPTIMALLY = false;
            $scope.mapFilterList.NO_DATA = false;
            locationMapService.createMachineMarkers($scope.location, $scope.mapFilterList);
        }
        $scope.performingOptimally = function() {
            $scope.mapFilterList.PERFORMING_OPTIMALLY = !$scope.mapFilterList.PERFORMING_OPTIMALLY;
            locationMapService.createMachineMarkers($scope.location, $scope.mapFilterList);
        }
        $scope.failure = function() {
            $scope.mapFilterList.FAILURE = !$scope.mapFilterList.FAILURE;
            locationMapService.createMachineMarkers($scope.location, $scope.mapFilterList);
        }
        $scope.failurePred = function() {
            $scope.mapFilterList.PREDICTING_FAILURE = !$scope.mapFilterList.PREDICTING_FAILURE;
            locationMapService.createMachineMarkers($scope.location, $scope.mapFilterList);
        }
        $scope.underMaintainance = function() {
            $scope.mapFilterList.UNDERGOING_MAINTENANCE = !$scope.mapFilterList.UNDERGOING_MAINTENANCE;
            locationMapService.createMachineMarkers($scope.location, $scope.mapFilterList);
        }
        $scope.noData = function() {
            $scope.mapFilterList.NO_DATA = !$scope.mapFilterList.NO_DATA;
            locationMapService.createMachineMarkers($scope.location, $scope.mapFilterList);
        }
    }
}]);