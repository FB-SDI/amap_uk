app.controller('analyticsController', ['$routeParams', '$scope', '$rootScope', '$window', '$location', 'locationMapService', 'getDataService', 'analyticsMapService', function($routeParams, $scope, $rootScope, $window, $location, locationMapService, getDataService, analyticsMapService) {

    var morrisDonut;
    var overallEffDiv = d3.select(document.getElementById('overallEffDiv'));

    $rootScope.activePage = 'analytics';
    $scope.performanceMetrics = [];
    $scope.totalPump = 0;

    $scope.callMetricDirective = false;

    $rootScope.$on("callJSONservice", function() {
        console.log("$rootScope.pumpType" + $rootScope.pumpType);
        overAllEfficiency($rootScope.pumpType);
    });
    if ($rootScope.pumpType) {
        overAllEfficiency($rootScope.pumpType);
    } else {
        overAllEfficiency('');
    }

    $scope.redirectToAsset = function(value) {
        if (value == 'Performance Optimally')
            $rootScope.assetFilter = { "machineStatus": "Performing_Optimally" };
        else if (value == 'Failure Predicted')
            $rootScope.assetFilter = { "machineStatus": "Predicting_Failure" };
        else if (value == 'Failure')
            $rootScope.assetFilter = { "machineStatus": "Failure" };
        else if (value == 'Undergoing Maintanence')
            $rootScope.assetFilter = { "machineStatus": "Undergoing_Maintanence" };
        else if (value == 'No Data')
            $rootScope.assetFilter = { "machineStatus": "NO_DATA" };
        location.replace('/#/dashboard/assetsearch');
    }
    $scope.setMetric = function(selectedMetric) {
        $scope.selectedLabel = selectedMetric;
    }

    $scope.selectedMetricCheck = function(metricSelected) {
        return (metricSelected == $scope.selectedLabel);
    }

    function overAllEfficiency(pumpType) {
        var effValue = 0;

        analyticsMapService.getOverallEffData(pumpType).then(function(resp) {
            effValue = resp.data.overallEfficeincy;
            overallEffDiv = d3.select(document.getElementById('overallEffDiv'));
            start(effValue);
        });
        analyticsMapService.getPerformanceData(pumpType).then(function(resp) {
            console.log(resp.data.performanceMetrics);
            $scope.performanceMetrics = resp.data.performanceMetrics;
            $scope.filteredPerformanceMetrics = [];
            $scope.perfMetricChartData = [];
            $scope.totalPump = 0;
            angular.forEach($scope.performanceMetrics, function(performanceCondition) {
                $scope.totalPump = $scope.totalPump + performanceCondition.units;
            });
            angular.forEach($scope.performanceMetrics, function(performanceCondition) {
                var filteredMetrics = {
                    "label": performanceCondition.label,
                    "percentage": Number((performanceCondition.units / $scope.totalPump * 100).toFixed(1)),
                    "units": performanceCondition.units
                };
                $scope.filteredPerformanceMetrics.push(filteredMetrics);
            });
            angular.forEach($scope.performanceMetrics, function(performanceCondition) {
                var metChartData = {
                    label: performanceCondition.label,
                    value: performanceCondition.units / $scope.totalPump * 100
                };
                $scope.perfMetricChartData.push(metChartData);
            });
            $scope.callMetricDirective = true;
        });

        function start(effValueCheck) {
            var diameter = 0;
            if (document.getElementById('overallEffDiv'))
                diameter = document.getElementById('overallEffDiv').clientWidth;
            if (diameter > 0)
                var rp1 = radialProgress(document.getElementById('overallEffDiv'))
                    .diameter(diameter)
                    .value(effValueCheck)
                    .render();
        }
        angular.element($window).bind('resize', function() {
            if (document.getElementById('overallEffDiv'))
                document.getElementById('overallEffDiv').innerHTML = ""
            start(effValue);
            $scope.$digest();
        });
    }

}]);
