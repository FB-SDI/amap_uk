app.controller('analyticsController', ['$routeParams', '$scope', '$rootScope', '$window', '$location', 'locationMapService', 'getDataService', 'analyticsMapService', function($routeParams, $scope, $rootScope, $window, $location, locationMapService, getDataService, analyticsMapService) {

    var morrisDonut;
    var overallEffDiv = d3.select(document.getElementById('overallEffDiv'));
    console.log('$rootScope.pumpType' + $rootScope.pumpType);
    $rootScope.activePage = 'analytics';
    $scope.performanceMetrics = [];
    $scope.totalPump = 0;
    $rootScope.assetFilter = "";

    $scope.callMetricDirective = false;

    $rootScope.$on("callJSONservice", function() {
        overAllEfficiency($rootScope.pumpType);
    });
    if ($rootScope.pumpType) {
        overAllEfficiency($rootScope.pumpType);
    } else {
        overAllEfficiency('');
    }
    $scope.setStatusCircle = function(metricLabel) {
        if (metricLabel == "Performing Optimally") {
            return 'optimal_performance';
        } else if (metricLabel == "Failure Predicted") {
            return 'failure_predicted';
        } else if (metricLabel == "Failure") {
            return 'failure';
        } else if (metricLabel == "Undergoing Maintenance") {
            return 'maintenance';
        } else if (metricLabel == "No Data") {
            return 'no_data';
        }
    };
    $scope.redirectToAsset = function(value) {
        if (value == 'Performing Optimally') {
            $rootScope.assetFilter = { "machineStatus": "Performing_Optimally", "pumpType": $rootScope.pumpType };

        } else if (value == 'Failure Predicted')
            $rootScope.assetFilter = { "machineStatus": "Predicting_Failure", "pumpType": $rootScope.pumpType };
        else if (value == 'Failure')
            $rootScope.assetFilter = { "machineStatus": "Failure", "pumpType": $rootScope.pumpType };
        else if (value == 'Undergoing Maintenance')
            $rootScope.assetFilter = { "machineStatus": "Undergoing_Maintanence", "pumpType": $rootScope.pumpType };
        else if (value == 'No Data')
            $rootScope.assetFilter = { "machineStatus": "NO_DATA", "pumpType": $rootScope.pumpType };
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