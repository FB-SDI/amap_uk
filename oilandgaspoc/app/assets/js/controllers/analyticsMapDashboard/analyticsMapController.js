app.controller('analyticsMapController', ['$routeParams', '$scope', '$rootScope', 'locationMapService', 'getDataService', 'analyticsMapService', function($routeParams, $scope, $rootScope, locationMapService, getDataService, analyticsMapService) {

    var filter = "all";

    $scope.selectedTab = "analyticsView";
    $rootScope.activePage = 'analytics';

    $scope.performanceMetrics = [];

    // $scope.selection = "";

    $scope.$watch('selection', function(newVal) {
        $rootScope.pumpType = newVal;
        $rootScope.$emit("callJSONservice", {});
    });

    // $scope.pumpTypeList = [{
    //     label: 'Choose from the Pumps below',
    //     value: 'none'
    // }];


    $scope.selectTab = function(tabSelected) {
        console.log(tabSelected + "tabSelected");
        $scope.selectedTab = tabSelected;

    }

    $scope.selectedTabCheck = function(tabCheck) {
        if (tabCheck == $scope.selectedTab) {
            return true;
        }
    }

    analyticsMapService.getPumpTypeData().then(function(resp) {
        $scope.pumpTypeList = resp.data.pumpType;
        $scope.selection = "";
        console.log("$scope.pumpTypeList");
        console.log($scope.pumpTypeList);
    });
}]);
