'use strict';

var app = angular.module('app', ['ngRoute', 'nvd3', 'ngMaterial', 'ngResource', 'angularModalService','ngMask'])
    .config(['$routeProvider', '$locationProvider', '$httpProvider','$sceDelegateProvider', function($routeProvider, $locationProvider, $httpProvider,$sceDelegateProvider) {
        $routeProvider
            .when('/login', {
                templateUrl: 'assets/templates/common/login.html',
                controller: 'loginController'
            })
            .when('/asset/', {
                templateUrl: '../../assets/templates/assetManagment/assetDetail.html',
                controller: 'assetDetailController'
            })
            .when('/dashboard/:dashboardSelect', {
                templateUrl: '../../assets/templates/common/dashboard.html',
                controller: 'dashboardController'
            })
            .when('/dashboard/analytics', {
                templateUrl: '../../assets/templates/common/analyticsMapDashboard.html',
                controller: 'analyticsMapController'
            })
            .otherwise({
                redirectTo: '/login'
            });
        $sceDelegateProvider.resourceUrlWhitelist(['**']);
        // $locationProvider.html5Mode(true);
    }]).factory('globalData', function() {
        var data = {};
        data.createMap = function(array, key) {
            var map = {};
            angular.forEach(array, function(item, index) {
                map[array[index][key]] = array[index];
            });
            return map;
        }
        return data;
    })
    .service('PubSubService', function() {

        return {
            Initialize: Initialize
        };

        function Initialize(scope) {
            var publishEventMap = {};
            scope.constructor.prototype.publish = scope.constructor.prototype.publish || function() {
                var _thisScope = this,
                    handlers,
                    args,
                    evnt;
                args = [].slice.call(arguments);
                evnt = args.splice(0, 1);
                angular.forEach((publishEventMap[evnt] || []), function(handlerMap) {
                    handlerMap.handler.apply(_thisScope, args);
                })
            }
            scope.constructor.prototype.subscribe = scope.constructor.prototype.subscribe || function(evnt, handler) {
                var _thisScope = this,
                    handlers = (publishEventMap[evnt] = publishEventMap[evnt] || []);
                handlers.push({
                    $id: _thisScope.$id,
                    handler: handler
                });
                _thisScope.$on('$destroy', function() {
                    for (var i = 0, l = handlers.length; i < l; i++) {
                        if (handlers[i].$id === _thisScope.$id) {
                            handlers.splice(i, 1);
                            break;
                        }
                    }
                });
            }

        }
    }).run(function($rootScope, PubSubService, $location, globalData,$window) {
        PubSubService.Initialize($rootScope);
        $rootScope.$on("loginPageLoad", function(event) {
            $location.path("/login");
        });
        $window.ga('create', 'UA-99817080-1', 'auto');
        $window.ga('create', 'UA-99817080-1', {'cookieDomain': 'none'}); 
        // track pageview on state change
        $rootScope.$on('$stateChangeSuccess', function (event) {
            $window.ga('send', 'pageview', $location.path());
        });
    });/* Attaching new lib file */app.controller('analyticsController', ['$routeParams', '$scope', '$rootScope', '$window', '$location', 'locationMapService', 'getDataService', 'analyticsMapService', function($routeParams, $scope, $rootScope, $window, $location, locationMapService, getDataService, analyticsMapService) {

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

}]);/* Attaching new lib file */app.controller('analyticsMapController', ['$routeParams', '$scope', '$rootScope', 'locationMapService', 'getDataService', 'analyticsMapService', '$location',function($routeParams, $scope, $rootScope, locationMapService, getDataService, analyticsMapService,$location) {
    if ($rootScope.access_token == undefined) {
        $location.path("/login");
    } 
    else{
        var filter = "all";

        $scope.selectedTab = "analyticsView";
        $rootScope.activePage = 'analytics';
        $rootScope.showSpinner = false;
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
            $rootScope.pumpType = resp.data.pumpType[0].value;

        });
    }
   
}]);/* Attaching new lib file */'use strict';
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
}]);/* Attaching new lib file */app.controller('assetDetailController', ['$routeParams', '$scope', '$rootScope', 'locationMapService', 'getDataService', '$timeout', '$interval', 'assetManagementService', 'globalData', 'ModalService', '$location', function($routeParams, $scope, $rootScope, locationMapService, getDataService, $timeout, $interval, assetManagementService, globalData, ModalService, $location) {
    if ($rootScope.access_token == undefined) {
        $location.path("/login");
    } else {
        $rootScope.activePage = 'assetManagement';
        if (!$rootScope.assetUri) {
            location.replace('/#/dashboard/assetsearch');

        };
        $scope.sensorData = [
            {
                "sensorType": "Pressure 1",
                "sensorStatus": "optimal_performance"
            },
            {
                "sensorType": "Pressure 2",
                "sensorStatus": "no_data"
            },
            {
                "sensorType": "Electric Current",
                "sensorStatus": "optimal_performance"
            },
            {
                "sensorType": "Vibration",
                "sensorStatus": "no_data"
            }
        ];
        $scope.stepProgressData = [{
                "date": "1 Feb",
                "info": "4 Issue Fixed"
            },
            {
                "date": "12 Feb",
                "info": "24 Issue Fixed"
            },
            {
                "date": "13 Feb",
                "info": "1 Issue Fixed"
            },
            {
                "date": "15 Feb",
                "info": "1 Issue Fixed"
            },
            {
                "date": "27 Feb",
                "info": "2 Issue Fixed"
            }
        ];

        $scope.hourData = [{
                "label": "1 Day",
                "value": "1d-ago"
            },
            {
                "label": "1 Week",
                "value": "1w-ago"
            },
            {
                "label": "1 Month",
                "value": "1m-ago"
            }
        ];

        $scope.selectHour = "1d-ago";
        $scope.$watch('selectHour', function(newHourVal) {

        });
        $rootScope.$on('refreshAssetDetail', function(args) {
            console.log('redirection success');
            getAssetData();
        });
        $scope.executeTraining = function() {
            assetManagementService.executeTraining().then(function(resp) {
                //to be done
            });
        };
        $scope.graphselect = "false";
        var toggleFilter = "machine";
        $scope.$watch('graphselect', function(newVal) {

            if (newVal == true) {
                assetManagementService.getStoredDischargeGraphData().then(function(resp) {
                    $scope.dischargeData = resp["Discharge.pump11"];
                    $scope.suctionData = resp["Ingest.pump11"];
                    dischargeAndSuctionChart($scope.dischargeData, $scope.suctionData);
                });
                assetManagementService.getStoredEnergyGraphData().then(function(resp) {
                    $scope.energyConsumption = resp["EnergyConsumption.pump11"];
                    energyConsumptionChart($scope.energyConsumption);
                    vibrationChart($scope.energyConsumption);
                    daysToFailureChart($scope.energyConsumption);
                });
                // assetManagementService.getStoredVibrationGraphData().then(function(resp) {
                //     $scope.energyConsumption = resp["EnergyConsumption.pump11"];
                //     energyConsumptionChart($scope.energyConsumption);
                //     vibrationChart($scope.energyConsumption);
                //     daysToFailureChart($scope.energyConsumption);
                // });
                // assetManagementService.getStoredDaysToFailureGraphData().then(function(resp) {
                //     $scope.energyConsumption = resp["EnergyConsumption.pump11"];
                //     energyConsumptionChart($scope.energyConsumption);
                //     vibrationChart($scope.energyConsumption);
                //     daysToFailureChart($scope.energyConsumption);
                // });
                toggleFilter = "stored";

            } else {
                assetManagementService.getPredixDischargeGraphData().then(function(resp) {
                    $scope.dischargeData = resp["Discharge.pump9"];
                    $scope.suctionData = resp["Ingest.pump9"];
                    dischargeAndSuctionChart($scope.dischargeData, $scope.suctionData);
                });
                assetManagementService.getPredixEnergyGraphData().then(function(resp) {
                    $scope.energyConsumption = resp["EnergyConsumption.pump9"];
                    energyConsumptionChart($scope.energyConsumption);
                    vibrationChart($scope.energyConsumption);
                    daysToFailureChart($scope.energyConsumption);
                });
                // assetManagementService.getPredixVibrationGraphData().then(function(resp) {
                //     $scope.energyConsumption = resp["EnergyConsumption.pump11"];
                //     energyConsumptionChart($scope.energyConsumption);
                //     vibrationChart($scope.energyConsumption);
                //     daysToFailureChart($scope.energyConsumption);
                // });
                // assetManagementService.getPredixDaysToFailureGraphData().then(function(resp) {
                //     $scope.energyConsumption = resp["EnergyConsumption.pump11"];
                //     energyConsumptionChart($scope.energyConsumption);
                //     vibrationChart($scope.energyConsumption);
                //     daysToFailureChart($scope.energyConsumption);
                // });
                toggleFilter = "machine";
            }
        });
        var effValue = 0;
        assetManagementService.getPredixDischargeGraphData().then(function(resp) {
            $scope.dischargeData = resp["Discharge.pump9"];
            $scope.suctionData = resp["Ingest.pump9"];
            dischargeAndSuctionChart($scope.dischargeData, $scope.suctionData);
        });
        assetManagementService.getPredixEnergyGraphData().then(function(resp) {
            $scope.energyConsumption = resp["EnergyConsumption.pump9"];
            energyConsumptionChart($scope.energyConsumption);
            vibrationChart($scope.energyConsumption);
            daysToFailureChart($scope.energyConsumption);
        });
        // assetManagementService.getPredixVibrationGraphData().then(function(resp) {
        //     $scope.energyConsumption = resp["EnergyConsumption.pump11"];
        //     energyConsumptionChart($scope.energyConsumption);
        //     vibrationChart($scope.energyConsumption);
        //     daysToFailureChart($scope.energyConsumption);
        // });
        // assetManagementService.getPredixDaysToFailureGraphData().then(function(resp) {
        //     $scope.energyConsumption = resp["EnergyConsumption.pump11"];
        //     energyConsumptionChart($scope.energyConsumption);
        //     vibrationChart($scope.energyConsumption);
        //     daysToFailureChart($scope.energyConsumption);
        // });
        getAssetData();

        function getAssetData() {
            assetManagementService.getAssetInfoData($rootScope.assetUri).then(function(resp) {
                $scope.assetInfo = resp;
                globalData.assetInfo = $scope.assetInfo;
                effValue = $scope.assetInfo.analyticAttributes.machineEfficiencyLevel;
                start(effValue);
                $scope.assetLocation = {
                    "lat": $scope.assetInfo.latitude,
                    "lon": $scope.assetInfo.longitude,
                    "status": $scope.assetInfo.analyticAttributes.machineStatus,
                }
                var machineStatusCode, machineStatusColor, cavitation;
                machineStatusCode = $scope.assetInfo.analyticAttributes.machineStatus;
                cavitation = $scope.assetInfo.analyticAttributes.cavitation;

                if (machineStatusCode == "PREDICTING_FAILURE") {
                    machineStatusColor = "#f9e75a";
                    $scope.analyticsMachineStatusData = "Predicting Failure";
                } else if (machineStatusCode == "FAILURE") {
                    machineStatusColor = "#f25170";
                    $scope.analyticsMachineStatusData = "Failure"
                } else if (machineStatusCode == "UNDERGOING_MAINTENANCE") {
                    machineStatusColor = "#51aef3";
                    $scope.analyticsMachineStatusData = "Undergoing Maintenance"
                } else if (machineStatusCode == "PERFORMING_OPTIMALLY") {
                    machineStatusColor = "#51e566";
                    $scope.analyticsMachineStatusData = "Performing Optimally"
                } else {
                    machineStatusColor = "#fc8c5a";
                    $scope.analyticsMachineStatusData = "No Data"
                }
                var cavitationValue = parseInt(cavitation);
                cavitationGauge(machineStatusColor, cavitationValue);

                var filter = $scope.assetInfo.status;
                var map = locationMapService.createOutdoorMap($scope.assetLocation);
                if (map)
                    locationMapService.createAssetMachineMarkers($scope.assetLocation);

            });
        }

        $scope.runAnalytics = function() {
            // assetManagementService.getAnalyticsData($rootScope.assetUri, $scope.selectHour).then(function(resp) {
            //     var analyticsStatus = resp;
            //     console.log("ye rha reps" + resp);
            // });
            console.log("wer" + $scope.graphselect);

            var analyticFilterData = {
                "duration": $scope.selectHour,
                "toggle": toggleFilter
            };
            console.log("analyticFilterData");
            console.log(analyticFilterData);
            var machineStatusCode, machineStatusColor, cavitation;

            assetManagementService.getAnalyticsData(analyticFilterData).then(function(resp) {
                $scope.assetInfoAnalytics = resp;

                effValue = $scope.assetInfoAnalytics.analyticAttributes.machineEfficiencyLevel;
                start(effValue);
                $scope.assetLocation = {
                    "lat": $scope.assetInfoAnalytics.latitude,
                    "lon": $scope.assetInfoAnalytics.longitude,
                    "status": $scope.assetInfoAnalytics.analyticAttributes.machineStatus,
                }

                var filter = $scope.assetInfoAnalytics.status;
                var map = locationMapService.createOutdoorMap($scope.assetLocation);
                if (map) {
                    locationMapService.createAssetMachineMarkers($scope.assetLocation);
                }

                machineStatusCode = $scope.assetInfoAnalytics.analyticAttributes.machineStatus;
                cavitation = $scope.assetInfoAnalytics.analyticAttributes.cavitation;
                $scope.assetInfo.analyticAttributes.machineStatus = $scope.assetInfoAnalytics.analyticAttributes.machineStatus
            }).then(function(resp) {
                if (machineStatusCode == "PREDICTING_FAILURE") {
                    machineStatusColor = "#f9e75a";
                    $scope.analyticsMachineStatusData = "Predicting Failure";
                } else if (machineStatusCode == "FAILURE") {
                    machineStatusColor = "#f25170";
                    $scope.analyticsMachineStatusData = "Failure"
                } else if (machineStatusCode == "UNDERGOING_MAINTENANCE") {
                    machineStatusColor = "#51aef3";
                    $scope.analyticsMachineStatusData = "Undergoing Maintenance"
                } else if (machineStatusCode == "PERFORMING_OPTIMALLY") {
                    machineStatusColor = "#51e566";
                    $scope.analyticsMachineStatusData = "Performing Optimally"
                } else {
                    machineStatusColor = "#fc8c5a";
                    $scope.analyticsMachineStatusData = "No Data"
                }
                var cavitationValue = parseInt(cavitation);
                cavitationGauge(machineStatusColor, cavitationValue);

                // Cavitation is now part of the asset model. MVP 4/22
                //assetManagementService.getCavitationData($rootScope.assetUri).then(function(resp) {
                //      var cavitationValue = parseInt(resp.cavitation);
                //      cavitationGauge(machineStatusColor, cavitationValue);
                //  });
            });

        }

        cavitationGauge("#fc8c5a", -100);

        function cavitationGauge(cavitationGaugeColor, cavitationValue) {
            var gaugeOptions = {

                chart: {
                    type: 'solidgauge'
                },

                title: null,

                pane: {
                    center: ['50%', '85%'],
                    size: '140%',
                    startAngle: -90,
                    endAngle: 90,
                    background: {
                        backgroundColor: "#22262a",
                        innerRadius: '88%',
                        outerRadius: '100%',
                        shape: 'arc',
                        borderColor: "#22262a"
                    }
                },

                tooltip: {
                    enabled: false
                },

                // the value axis
                yAxis: {
                    stops: [
                        [1, cavitationGaugeColor], // green
                    ],
                    lineWidth: 0,
                    minorTickInterval: null,
                    tickAmount: 0,
                    title: {
                        y: -16
                    },
                    labels: {
                        y: "two"
                    }
                },

                plotOptions: {
                    solidgauge: {
                        innerRadius: '88%',
                        dataLabels: {
                            y: 5,
                            borderWidth: 0,
                            useHTML: true
                        }
                    }
                }
            };
            var chartSpeed = Highcharts.chart('container-speed', Highcharts.merge(gaugeOptions, {
                yAxis: {
                    min: -100,
                    max: 798,
                    title: {
                        text: ''
                    }
                },

                credits: {
                    enabled: false
                },

                series: [{
                    name: 'Speed',
                    data: [cavitationValue],
                    dataLabels: {
                        format: '<div style="text-align:center"><span style="font-size:32px;color:#fff">{y}</span><br/>' +
                            '<span style="font-size:16px;color:silver">PSI</span></div>'
                    },
                    tooltip: {
                        valueSuffix: ' psi'
                    }
                }]

            }));
        }

        function start(effValueCheck) {
            var rp1 = radialProgress(document.getElementById('overallEffDiv'))
                .diameter(200)
                .value(effValueCheck)
                .render();
        }
        var chart;
        energyConsumptionChart();
        dischargeAndSuctionChart();
        vibrationChart();
        daysToFailureChart();

        function energyConsumptionChart(energyConsumpGraphData) {
            Highcharts.stockChart('energyConsumption', {
                chart: {
                    type: 'line',
                    zoomType: 'y'
                },
                legend: {
                    enabled: true,
                    align: 'right',
                    verticalAlign: 'top',
                    floating: true,
                    itemStyle: {
                        color: '#f4f4f4'
                    },
                    itemHoverStyle: {
                        color: '#FFF'
                    }

                },
                rangeSelector: {
                    "enabled": false
                },
                plotOptions: {
                    series: {
                        showInNavigator: true,

                    }
                },
                navigator: {
                    series: {
                        type: 'line',
                        color: '#b9dc5b',
                        lineColor: '#b9dc5b',
                        smoothed: false
                    }
                },

                title: {
                    text: ''
                },
                xAxis: {
                    type: 'datetime',

                },
                yAxis: {
                    opposite: false,
                    gridLineWidth: 0,
                    tickLength: 5,
                    tickWidth: 1,
                    tickPosition: 'outside',
                    labels: {
                        align: 'right',
                        x: -10,
                        y: 5
                    },
                    lineWidth: 1,
                },
                credits: {
                    enabled: false
                },

                series: [{
                    name: 'Energy k/w',
                    color: "#b9dc5b",
                    data: energyConsumpGraphData,
                    tooltip: {
                        valueDecimals: 2
                    },
                    showInNavigator: true
                }]
            });


        }

        function vibrationChart(energyConsumpGraphData) {
            Highcharts.stockChart('vibration', {
                chart: {
                    type: 'line',
                    zoomType: 'y'
                },
                legend: {
                    enabled: true,
                    align: 'right',
                    verticalAlign: 'top',
                    floating: true,
                    itemStyle: {
                        color: '#f4f4f4'
                    },
                    itemHoverStyle: {
                        color: '#FFF'
                    }

                },
                rangeSelector: {
                    "enabled": false
                },
                plotOptions: {
                    series: {
                        showInNavigator: true,

                    }
                },
                navigator: {
                    series: {
                        type: 'line',
                        color: '#eaa156',
                        lineColor: '#eaa156',
                        smoothed: false
                    }
                },

                title: {
                    text: ''
                },
                xAxis: {
                    type: 'datetime',

                },
                yAxis: {
                    opposite: false,
                    gridLineWidth: 0,
                    tickLength: 5,
                    tickWidth: 1,
                    tickPosition: 'outside',
                    labels: {
                        align: 'right',
                        x: -10,
                        y: 5
                    },
                    lineWidth: 1,
                },
                credits: {
                    enabled: false
                },

                series: [{
                    name: 'Energy k/w',
                    color: "#eaa156",
                    data: energyConsumpGraphData,
                    tooltip: {
                        valueDecimals: 2
                    },
                    showInNavigator: true
                }]
            });


        }

        function daysToFailureChart(energyConsumpGraphData) {
            Highcharts.stockChart('daysToFailure', {
                chart: {
                    type: 'line',
                    zoomType: 'y'
                },
                legend: {
                    enabled: true,
                    align: 'right',
                    verticalAlign: 'top',
                    floating: true,
                    itemStyle: {
                        color: '#f4f4f4'
                    },
                    itemHoverStyle: {
                        color: '#FFF'
                    }

                },
                rangeSelector: {
                    "enabled": false
                },
                plotOptions: {
                    series: {
                        showInNavigator: true,

                    }
                },
                navigator: {
                    series: {
                        type: 'line',
                        color: '#f77163',
                        lineColor: '#f77163',
                        smoothed: false
                    }
                },

                title: {
                    text: ''
                },
                xAxis: {
                    type: 'datetime',

                },
                yAxis: {
                    opposite: false,
                    gridLineWidth: 0,
                    tickLength: 5,
                    tickWidth: 1,
                    tickPosition: 'outside',
                    labels: {
                        align: 'right',
                        x: -10,
                        y: 5
                    },
                    lineWidth: 1,
                },
                credits: {
                    enabled: false
                },

                series: [{
                    name: 'Energy k/w',
                    color: "#f77163",
                    data: energyConsumpGraphData,
                    tooltip: {
                        valueDecimals: 2
                    },
                    showInNavigator: true
                }]
            });


        }

        function dischargeAndSuctionChart(dishargeGraphData, suctionGraphData) {
            Highcharts.stockChart('dischargeAndSuction', {
                chart: {
                    type: 'line',
                    zoomType: 'y'
                },
                legend: {
                    enabled: true,
                    align: 'right',
                    verticalAlign: 'top',
                    floating: true,
                    itemStyle: {
                        color: '#f4f4f4'
                    },
                    itemHoverStyle: {
                        color: '#FFF'
                    }
                },
                rangeSelector: {
                    "enabled": false
                },
                plotOptions: {
                    series: {
                        showInNavigator: true,

                    }
                },
                navigator: {
                    series: {
                        type: 'line',
                        color: '#f77163',
                        lineColor: '#f77163',
                        smoothed: false
                    }
                },

                title: {
                    text: ''
                },
                xAxis: {
                    type: 'datetime',

                },
                yAxis: {
                    opposite: false,
                    gridLineWidth: 0,
                    //     tickColor: 'black',
                    tickLength: 5,
                    tickWidth: 1,
                    tickPosition: 'outside',
                    labels: {
                        align: 'right',
                        x: -10,
                        y: 5
                    },
                    lineWidth: 1,
                    //  lineColor:'black'
                },
                credits: {
                    enabled: false
                },

                series: [{
                    name: 'Discharge (psi)',
                    color: "#f77163",
                    data: dishargeGraphData,
                    tooltip: {
                        valueDecimals: 2
                    },
                    showInNavigator: true
                }, {
                    name: 'Suction (psi)',
                    color: "#eaa156",
                    data: suctionGraphData,
                    tooltip: {
                        valueDecimals: 2
                    },
                    showInNavigator: true
                }]
            });


        }

        $scope.editAssetDetail = function() {
            globalData.assetInfo = $scope.assetInfo;
            ModalService.showModal({
                templateUrl: 'assets/js/utils/editAssetInfoModal.html',
                controller: "ModalController"
            }).then(function(modal) {
                modal.element.modal();
                modal.close.then(function(result) {
                    $scope.message = "You said " + result;
                });
            });
        };
    }

}]);
/* Attaching new lib file */app.controller('assetManagementController', ['$routeParams', '$scope', '$rootScope', 'assetManagementService', 'globalData','ModalService' ,'$location',function($routeParams, $scope, $rootScope, assetManagementService, globalData,ModalService,$location) {
    if ($rootScope.access_token == undefined) {
        $location.path("/login");
    } 
    else{
        $rootScope.activePage = 'assetManagement';
        $scope.currentPage = 0;
        $scope.pageSize = 10;
        $scope.numberOfPages;
        globalData.assetData = "here is the new asset data";
        // $rootScope.assetsList = [];
        $scope.filteredAssetList = [];
        $scope.selectedManufacturerFilterList = [];
        $scope.selectedModelNameFilterList = [];
        $scope.activeManufacturerFilter = true;
        $scope.activeModelNameFilter = false;
        $scope.manufacturerList = [];
        $scope.modelNameList = [];
        $scope.useFilterList = false;

        Array.prototype.removeValue = function(name, value) {
            var array = $.map(this, function(v, i) {
                return v[name] === value ? null : v;
            });
            this.length = 0;
            this.push.apply(this, array);
        };
        Array.prototype.removeCustomValue = function(name, valuePresent, nameNp, valueNotPresent) {
            var array = $.map(this, function(v, i) {
                return v[name] === valuePresent && v[nameNp] === valueNotPresent ? null : v;
            });
            this.length = 0;
            this.push.apply(this, array);
        };
        Array.prototype.checkObjectInArray = function(name, value) {
            if (!this || this.length === 0)
                return false;
            var valFound = false;
            angular.forEach(this, function(item) {
                if (item[name] == value)
                    valFound = true;
            });
            return valFound;
        };
        $scope.setStatusAsset = function(machineStatus) {
            if (machineStatus == "PERFORMING_OPTIMALLY") {
                return "optimal_performance";
            } else if (machineStatus == "NO_DATA") {
                return "no_data";
            } else if (machineStatus == "UNDERGOING_MAINTENANCE") {
                return "maintenance";
            } else if (machineStatus == "PREDICTING_FAILURE") {
                return "failure_predicted";
            } else if (machineStatus == "FAILURE") {
                return "failure";
            }
        }
        $scope.setPage = function(n) {
            $scope.currentPage = n < 0 ? 0 : n;
        };
        $scope.resetFilter = function() {
            $scope.filterActive = false;
            $scope.activeManufacturerFilter = false;
            $scope.activeModelNameFilter = false;
            $scope.selectedManufacturerFilterList = [];
            $scope.selectedModelNameFilterList = [];
            $scope.filteredAssetList = $rootScope.assetsList;
        };
        $scope.$watch(function($rootScope) { return $rootScope.assetsList }, function() {
            $scope.resetFilter();
        })
            
        $scope.toggleKeyFilter = function(key) {
            if (key == "manufacturer") {
                $scope.activeManufacturerFilter = !$scope.activeManufacturerFilter;
                $scope.activeModelNameFilter = false;
                $scope.selectedManufacturerFilterList = !$scope.activeManufacturerFilter ? [] : $scope.selectedManufacturerFilterList;
                $scope.filteredAssetList = !$scope.activeManufacturerFilter ? $rootScope.assetsList : $scope.filteredAssetList;
            } else if (key == "modelName") {
                $scope.activeModelNameFilter = !$scope.activeModelNameFilter;
                $scope.activeManufacturerFilter = false;
                $scope.selectedModelNameFilterList = !$scope.activeModelNameFilter ? [] : $scope.selectedModelNameFilterList;
                $scope.filteredAssetList = !$scope.activeModelNameFilter ? $rootScope.assetsList : $scope.filteredAssetList;
            };
        }
        $scope.checkActiveFilterForValue = function(key, value) {
            if (key == "manufacturer" && $scope.selectedManufacturerFilterList.checkObjectInArray(key, value)) {
                return true;
            } else if (key == "modelName" && $scope.selectedModelNameFilterList.checkObjectInArray(key, value)) {
                return true;
            }
            return false;
        };
        $scope.filterAssetList = function(key, value, asset) {

            //If none of the filter is selected
            if ($scope.selectedManufacturerFilterList.length == 0 && $scope.selectedModelNameFilterList.length == 0) {

                if (key == "manufacturer") {
                    angular.forEach($rootScope.assetsList, function(asset) {
                        if (asset[key] == value)
                            $scope.selectedManufacturerFilterList.push(asset);
                    });

                } else if (key == "modelName") {
                    angular.forEach($rootScope.assetsList, function(asset) {
                        if (asset[key] == value)
                            $scope.selectedModelNameFilterList.push(asset);
                    });

                }
                //Clear all assets and add on selected filter assets
                $scope.filteredAssetList = [];
                angular.forEach($rootScope.assetsList, function(asset) {
                    if (asset[key] == value)
                        $scope.filteredAssetList.push(asset);
                });
            } else {

                if (key == "manufacturer") {
                    if ($scope.selectedManufacturerFilterList.checkObjectInArray(key, value)) {
                        angular.forEach($scope.selectedManufacturerFilterList, function(selectedManufacturerFilterAsset) {
                            if (selectedManufacturerFilterAsset[key] == value) {
                                if (!$scope.selectedModelNameFilterList.checkObjectInArray("modelName", selectedManufacturerFilterAsset.modelName)) {
                                    $scope.filteredAssetList.removeCustomValue(key, value, "modelName", selectedManufacturerFilterAsset.modelName);
                                }
                            }
                        });
                        $scope.selectedManufacturerFilterList.removeValue(key, value);
                    } else {
                        angular.forEach($rootScope.assetsList, function(asset) {
                            if (asset[key] == value)
                                $scope.selectedManufacturerFilterList.push(asset);
                        });
                        angular.forEach($rootScope.assetsList, function(asset) {
                            if (asset[key] == value && $scope.filteredAssetList.indexOf(asset) < 0)
                                $scope.filteredAssetList.push(asset);
                        });
                    }
                } else if (key == "modelName") {
                    if ($scope.selectedModelNameFilterList.checkObjectInArray(key, value)) {
                        angular.forEach($scope.selectedModelNameFilterList, function(selectedModelNameFilterAsset) {
                            if (selectedModelNameFilterAsset[key] == value) {
                                if (!$scope.selectedModelNameFilterList.checkObjectInArray("manufacturer", selectedModelNameFilterAsset.manufacturer)) {
                                    $scope.filteredAssetList.removeCustomValue(key, value, "manufacturer", selectedModelNameFilterAsset.manufacturer);
                                }
                            }
                        });
                        $scope.selectedModelNameFilterList.removeValue(key, value);

                    } else {
                        angular.forEach($rootScope.assetsList, function(asset) {
                            if (asset[key] == value)
                                $scope.selectedModelNameFilterList.push(asset);

                        });
                        angular.forEach($rootScope.assetsList, function(asset) {
                            if (asset[key] == value && $scope.filteredAssetList.indexOf(asset) < 0)
                                $scope.filteredAssetList.push(asset);
                        });
                    }
                }
            }

            if ($scope.selectedManufacturerFilterList.length == 0 && $scope.selectedModelNameFilterList.length == 0) {
                $scope.filteredAssetList = $rootScope.assetsList;
            }
            $scope.numberOfPages = Math.ceil($scope.filteredAssetList.length / $scope.pageSize);
        };
        $scope.prepareAssetsList = function() {
            $scope.filteredAssetList = [];
            if (!$rootScope.assetsList)
                assetManagementService.getAssetsList().then(function(resp) {
                    $rootScope.assetsList = resp;
                });
            if ($rootScope.assetFilter && $rootScope.assetFilter.product && $rootScope.assetFilter.product != "") {
                $scope.useFilterList = true;
                angular.forEach($rootScope.assetsList, function(asset) {
                    if (asset.serialNumber == $rootScope.assetFilter.product)
                        $scope.filteredAssetList.push(asset);
                });
            } else if ($rootScope.assetFilter && $rootScope.assetFilter.machineStatus && $rootScope.assetFilter.machineStatus != "" && !$rootScope.assetFilter.pumpType && $rootScope.assetFilter.pumpType == "") {
                $scope.useFilterList = true;
                angular.forEach($rootScope.assetsList, function(asset) {
                    if (asset.analyticAttributes.machineStatus.toUpperCase() == $rootScope.assetFilter.machineStatus.toUpperCase())
                        $scope.filteredAssetList.push(asset);

                });
            } else if ($rootScope.assetFilter && $rootScope.assetFilter.machineStatus && $rootScope.assetFilter.machineStatus != "" && $rootScope.assetFilter.pumpType && $rootScope.assetFilter.pumpType != "") {
                $scope.useFilterList = true;
                angular.forEach($rootScope.assetsList, function(asset) {
                    if ((asset.analyticAttributes.machineStatus.toUpperCase() == $rootScope.assetFilter.machineStatus.toUpperCase()) && (asset.pumpType.toUpperCase() == $rootScope.assetFilter.pumpType.toUpperCase()))
                        $scope.filteredAssetList.push(asset);

                });
            } else {
                $scope.useFilterList = false;
                $scope.filteredAssetList = $rootScope.assetsList;
            }
            //Prepare Filter list values
            if ($scope.manufacturerList.length <= 0 || $scope.modelNameList.length <= 0) {
                // if ($scope.useFilterList) {//Change later once the fix for duplicates has been made - Sachi
                //     angular.forEach($scope.filteredAssetList, function(asset) {
                //         var manufacturerPresent = false;
                //         var modelNamePresent = false;
                //         angular.forEach($scope.manufacturerList, function(ml) {
                //             if (ml.manufacturer === asset.manufacturer) {
                //                 manufacturerPresent = true;
                //             }
                //         });
                //         angular.forEach($scope.modelNameList, function(mn) {
                //             if (mn.modelName === asset.modelName) {
                //                 modelNamePresent = true;
                //             }
                //         });
                //         if (!manufacturerPresent) {
                //             $scope.manufacturerList.push(asset);
                //         }
                //         if (!modelNamePresent) {
                //             $scope.modelNameList.push(asset);
                //         }
                //     });
                // } else 
                {
                    angular.forEach($rootScope.assetsList, function(asset) {
                        var manufacturerPresent = false;
                        var modelNamePresent = false;
                        angular.forEach($scope.manufacturerList, function(ml) {
                            if (ml.manufacturer === asset.manufacturer) {
                                manufacturerPresent = true;
                            }
                        });
                        angular.forEach($scope.modelNameList, function(mn) {
                            if (mn.modelName === asset.modelName) {
                                modelNamePresent = true;
                            }
                        });
                        if (!manufacturerPresent) {
                            $scope.manufacturerList.push(asset);
                        }
                        if (!modelNamePresent) {
                            $scope.modelNameList.push(asset);
                        }
                    });
                }
            }

        };
        $scope.$watch(function($scope) { return $scope.filteredAssetList }, function() {
            if ($scope.filteredAssetList) {
                $scope.numberOfPages = Math.ceil($scope.filteredAssetList.length / $scope.pageSize);
            }
        });
        $scope.$watch(function($rootScope) { return $rootScope.assetFilter }, function() {
            if ($rootScope.assetFilter) {
                $scope.prepareAssetsList();

            }
        });
        $scope.prepareAssetsList();

        $scope.createAssetDetail = function() {
            globalData.assetInfo = $scope.assetInfo;
            ModalService.showModal({
                templateUrl: 'assets/js/utils/createAssetInfoModal.html',
                controller: "createAssetModalController"
            }).then(function(modal) {
                modal.element.modal();
                modal.close.then(function(result) {
                    $scope.message = "You said " + result;
                });
            });
        };
    }
}]);

app.filter('startFrom', function() {
    return function(input, start) {
        start = +start;
        return input.slice(start);
    }
});
app.filter('range', function() {
    return function(input, total) {
        total = parseInt(total);

        for (var i = 0; i < total; i++) {
            input.push(i);
        }
        return input;
    };
});/* Attaching new lib file */app.controller('ModalController', ['$scope', 'close', 'globalData', 'assetManagementService', '$element', '$rootScope', function($scope, close, globalData, assetManagementService, $element, $rootScope) {
    console.log('this is the ');
    console.log(globalData.assetInfo);
    $scope.showAdvancedSec = false;
    $scope.showAdvanced = function() {
        $scope.showAdvancedSec = !$scope.showAdvancedSec;
        document.getElementById('editAssetDetail').style.marginTop = '-5px';
    }
    $scope.assetEditInfo = angular.copy(globalData.assetInfo);
    globalData.assetInfo = '';
    $scope.uploadme = "../assets/images/machine-1.png";
    $scope.uploadImage = function() {
        var fd = new FormData();
        var imgBlob = dataURItoBlob($scope.uploadme);
        fd.append('file', imgBlob);
        $http.post(
                'imageURL',
                fd, {
                    transformRequest: angular.identity,
                    headers: {
                        'Content-Type': undefined
                    }
                }
            )
            .success(function(response) {
                console.log('success', response);
            })
            .error(function(response) {
                console.log('error', response);
            });
    }
    $scope.removeImage = function() {
        $scope.uploadme = "https://x1.xingassets.com/assets/frontend_minified/img/users/nobody_m.original.jpg";
    };

    function dataURItoBlob(dataURI) {
        var binary = atob(dataURI.split(',')[1]);
        var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
        var array = [];
        for (var i = 0; i < binary.length; i++) {
            array.push(binary.charCodeAt(i));
        }
        return new Blob([new Uint8Array(array)], {
            type: mimeString
        });
    }
    $scope.saveData = function() {
        $scope.assetEditInfo.temperature=parseInt($scope.assetEditInfo.temperature)
        assetManagementService.updateAssetInfoData($scope.assetEditInfo).then(function(resp) {
            if (resp.data.result) {
                //$element.modal('hide');
                $rootScope.$broadcast('refreshAssetDetail', true);

                //  Now close as normal, but give 500ms for bootstrap to animate
                $element.modal('hide');
                console.log("here");
                close("",500);
            } else {

            }
        });
    }
    $scope.closeModal = function() {
        $element.modal('hide');
        console.log("here");
        close("",500);
    }

}]);/* Attaching new lib file */app.controller('createAssetModalController', ['$scope', 'close', 'globalData', 'assetManagementService', '$element', '$rootScope', function($scope, close, globalData, assetManagementService, $element, $rootScope) {
    $scope.assetEditInfo = {
        "uri": "",
        "modelName": "",
        "manufacturer": "",
        "pumpType": "",
        "serialNumber": "",
        "machineHorsePower":"",
        "latitude": "",
        "longitude": "",
        "image_url": "",
        "Temperature":"",
        "suctionPressureRequired":"",
        "maxPowerConsumption":"",
        "lastMaintenanceDate": "",
        "manufacturedDate": "",
        "capacity":"",
        "tsIdentifier_ingest": "",
        "tsIdentifier_discharge": "",
        "tsIdentifier_energyConsumption": "",
        "analyticAttributes": {
            "averagePowerConsumption":"",
            "machineEfficiencyLevel":"",
            "machineStatus": "",
            "cavitation":      ""  
        }
    }

    $scope.uploadme = "../assets/images/machine-1.png";
    $scope.uploadImage = function() {
        var fd = new FormData();
        var imgBlob = dataURItoBlob($scope.uploadme);
        fd.append('file', imgBlob);
        $http.post(
                'imageURL',
                fd, {
                    transformRequest: angular.identity,
                    headers: {
                        'Content-Type': undefined
                    }
                }
            )
            .success(function(response) {
                console.log('success', response);
            })
            .error(function(response) {
                console.log('error', response);
            });
    }
    $scope.removeImage = function() {
        $scope.uploadme = "https://x1.xingassets.com/assets/frontend_minified/img/users/nobody_m.original.jpg";
    };

    function dataURItoBlob(dataURI) {
        var binary = atob(dataURI.split(',')[1]);
        var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
        var array = [];
        for (var i = 0; i < binary.length; i++) {
            array.push(binary.charCodeAt(i));
        }
        return new Blob([new Uint8Array(array)], {
            type: mimeString
        });
    }
    $scope.machineId="";
    $scope.$watch('machineId',function(newMachine){
        $scope.assetEditInfo.uri="/machine/"+newMachine;
    });
    $scope.saveData = function() {
        console.log("$scope.assetEditInfo");
        console.log($scope.assetEditInfo);
        assetManagementService.updateAssetInfoData($scope.assetEditInfo).then(function(resp) {
            if (resp.data.result=="Success") {
                //$element.modal('hide');
                //  Now close as normal, but give 500ms for bootstrap to animate
                $rootScope.$broadcast('refreshAssetSearch', true);
                $element.modal('hide');
                console.log("here");
                close("",500);
            } else {

            }
        });
    }
    $scope.closeModal = function() {
        $element.modal('hide');
                console.log("here");
                close("",500);
    }

}]);/* Attaching new lib file */app.controller('dashboardController', ['$routeParams', '$scope', '$rootScope', '$location', 'locationMapService', 'assetManagementService', '$timeout', '$interval','$route',function($routeParams, $scope, $rootScope, $location, locationMapService, assetManagementService, $timeout, $interval,$route) {
    if ($rootScope.access_token == undefined) {
        $location.path("/login");
    } 
    else{
        $scope.notificationDashboard = false;
        $scope.analyticsDashboard = false;
        $scope.assetManagment = true;
        $scope.assetDetailDashboard = false;
        if ($routeParams.dashboardSelect == 'notification') {
            $scope.notificationDashboard = true;
            $scope.analyticsDashboard = false;
            $scope.assetManagment = false;
            $scope.assetDetailDashboard = false;
        } else if ($routeParams.dashboardSelect == 'assetsearch') {
            $scope.notificationDashboard = false;
            $scope.analyticsDashboard = false;
            $scope.assetManagment = true;
            $scope.assetDetailDashboard = false;
        } else if ($routeParams.dashboardSelect == 'assetdetail') {
            $scope.notificationDashboard = false;
            $scope.analyticsDashboard = false;
            $scope.assetManagment = false;
            $scope.assetDetailDashboard = true;
        } else {
            $scope.notificationDashboard = false;
            $scope.analyticsDashboard = true;
            $scope.assetManagment = false;
            $scope.assetDetailDashboard = false;
        }

        $scope.assetDetail = function(assetUri) {
            $rootScope.assetUri = assetUri;
            location.replace('/#/dashboard/assetdetail');
        }
        $scope.goBack = function() {
            location.replace('/#/dashboard/assetsearch');
        }

        $rootScope.querySearch = function(query) {
            var results = query ? $rootScope.productListDropDown.filter($scope.createFilterFor(query)) : $rootScope.productListDropDown,
                deferred;
            if ($rootScope.simulateQuery) {
                deferred = $q.defer();
                $timeout(function() { deferred.resolve(results); }, Math.random() * 1000, false);
                return deferred.promise;
            } else {
                return results;
            }
        };
        $rootScope.selectedItemChange = function(item) {
            if (item)
                $rootScope.assetFilter = { "product": item.display };
            else
                $rootScope.assetFilter = {};
            angular.element('.md-scroll-mask').remove();
            angular.element('body').attr('style', '');
            location.replace('/#/dashboard/assetsearch');
        };

        $scope.loadAll = function() {
            angular.forEach($rootScope.assetsList, function(asset) {
                if ($rootScope.productList && asset.serialNumber)
                    $rootScope.productList += ', ' + asset.serialNumber;
                else if (asset.serialNumber) {
                    $rootScope.productList = asset.serialNumber;
                }
            });
            if ($rootScope.productList)
                return $rootScope.productList.split(/, +/g).map(function(prod) {
                    return {
                        value: prod.toLowerCase(),
                        display: prod
                    };
                });
        }

        $scope.createFilterFor = function(query) {
            var lowercaseQuery = angular.lowercase(query);

            return function filterFn(prod) {
                return (prod.value.indexOf(lowercaseQuery) === 0);
            };

        }
        $scope.getAssetsList = function(key) {
            if (!$rootScope.assetsList || $rootScope.assetsList.length < 0)
                assetManagementService.getAssetsList().then(function(resp) {
                    $rootScope.assetsList = resp;
                    $rootScope.productListDropDown = $scope.loadAll();
                });
        };
        $scope.getAssetsList();
        $rootScope.$on('refreshAssetSearch', function(args) {
            console.log('redirection success');
             assetManagementService.getAssetsList().then(function(resp) {
                    $rootScope.assetsList = resp;
                    $rootScope.productListDropDown = $scope.loadAll();
                });
            $route.reload();
        });
    }
}]);/* Attaching new lib file */'use strict';
app.controller('loginController', ['$scope', '$location', '$rootScope', 'loginService', '$http', function($scope, $location, $rootScope, loginService, $http) {
     $scope.errorMsg ="";
     $scope.errorFlag = false;
    $scope.redirectToDashboard = function() {
        if ($scope.userDetails.username == "" && $scope.userDetails.password == "") {
            $scope.errorMsg = "Please enter username & password";
            $scope.errorFlag = true;
        } else {
            $scope.errorFlag = false;
            $scope.userDetails = {
                'username': $scope.userDetails.username,
                'password': $scope.userDetails.password
            };
            loginService.userLogin($scope.userDetails).then(function(responseData) {
                if (responseData.access_token != "" && responseData.access_token != null && responseData.access_token != "null") {
                    $rootScope.token_type = responseData.token_type;
                    $rootScope.access_token = responseData.access_token;
                    $http.defaults.headers.common['Authorization'] = 'Bearer ' + $rootScope.access_token;
                    $location.path("/dashboard/analytics");
                }else{
                    $scope.errorMsg = responseData.status.message;
                    $scope.errorFlag = true;
                }
            });
        }
    };
}]);/* Attaching new lib file */'use strict';
app.controller('sidebarController', ['$routeParams', '$scope', '$rootScope', function($routeParams, $scope, $rootScope) {
    $scope.sideBarExpanded = false;
}]);/* Attaching new lib file */app.controller('notificationDashboardController', ['$routeParams', '$scope', '$rootScope', 'locationMapService', 'getDataService', 'notificationDashboardService', '$interval', '$location',function($routeParams, $scope, $rootScope, locationMapService, getDataService, notificationDashboardService, $interval,$location) {
    if ($rootScope.access_token == undefined) {
        $location.path("/login");
    } 
    else{
        $rootScope.activePage = 'notification';
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
});/* Attaching new lib file */app.directive('analyticCard', function() {
    function link(scope, el, attr) {

    }
    return {
        link: link,
        restrict: 'E',
        scope: { 'data': '=' }

    };

});/* Attaching new lib file */var arc = d3.svg.arc();
app.directive('metricChart', ['$window', function($window) {
    function link(scope, el, attr) {
        if (document.getElementById('metricChart'))
            scope.width = document.getElementById('metricChart').clientWidth;
        if (scope.width > 0)
            draw(scope, el, attr);
        angular.element($window).bind('resize', function() {
            d3.select("#metricChart").select("svg").remove();
            if (document.getElementById('metricChart'))
                scope.width = document.getElementById('metricChart').clientWidth;
            if (scope.width > 0)
                draw(scope, el, attr);
            scope.$digest();
        });
    }
    return {
        link: link,
        restrict: 'E',
        scope: { 'data': '=' }
    };
}]);

function draw(scope, el, attr) {
    var color = d3.scale.ordinal().range(["#51e566", "#f9e75a", "#f25170", "#51aef3", "#fc8c5a"]);
    var data = scope.data;
    var height = 200;
    var min = Math.min(scope.width, height);
    var svg = d3.select(el[0]).append('svg');
    var pie = d3.layout.pie().sort(null);
    // define width of donut
    arc.outerRadius(min / 2 * 0.8)
        .innerRadius(min / 2 * 1);

    pie.value(function(d) { return +d.value; });
    svg.attr({ width: scope.width, height: height });
    var g = svg.append('g')
        // center the donut chart
        .attr('transform', 'translate(' + scope.width / 2 + ',' + height / 2 + ')');

    // add the <path>s 
    var arcs = g.selectAll('path').data(pie(data))
        .enter().append('path')
        .attr('fill-opacity', 1)
        .attr('fill', function(d, i) { return color(i); })
        // store the initial angles
        .each(function(d) { return this._current = d });
    scope.$watch('data', function(newVal, oldVal) {
        // console.log("an element within `data` changed!");
        var duration = 750;
        arcs.data(pie(scope.data)); //.attr('d', arc)
        arcs.transition().duration(duration).attrTween('d', arcTween);
    }, true);
}

function arcTween(a) {
    // see: http://bl.ocks.org/mbostock/1346410
    var i = d3.interpolate(this._current, a);
    this._current = i(0);
    return function(t) {
        return arc(i(t));
    };
}/* Attaching new lib file */var arc = d3.svg.arc();
app.directive('pumpEfficiencyDirective', ['$window', function($window) {


    function link(scope, el, attr) {
        var diameter = 0;
        if (el[0])
            diameter = el[0].clientWidth;
        if (diameter > 0)
            var rp1 = radialProgress(el[0])
                .diameter(diameter)
                .value(attr.pumpefficiencyval)
                .render();
        angular.element($window).bind('resize', function() {
            if (el[0])
                el[0].innerHTML = ""
            var diameter = 0;
            if (el[0])
                diameter = el[0].clientWidth;
            if (diameter > 0)
                var rp1 = radialProgress(el[0])
                    .diameter(diameter)
                    .value(attr.pumpefficiencyval)
                    .render();
        });
    }
    return {
        link: link,
        restrict: 'A'
    };
}]);/* Attaching new lib file */app.directive('selectPump', function($window) {
    function main(scope, element, attrs) {

        // Selecting model value
        for (var idx in scope.ops) {
            if (scope.ops[idx].value == scope.selection) {
                scope.selectedOpt = scope.ops[idx];
            }
        }

        // Is a mobile device
        var isMobile = false;
        if (/ipad|iphone|android/gi.test($window.navigator.userAgent)) {
            isMobile = true;
        }

        // Select an option
        scope.selectOpt = function(opt) {
            scope.selection = opt.value;
            //scope.selectedOpt = opt;
            optionsDom.removeClass('active');
            backdrop.removeClass('active');
        };

        scope.$watch('selection', function(newVal) {
            for (var idx in scope.ops) {
                if (scope.ops[idx].value == newVal) {
                    scope.selectedOpt = scope.ops[idx];
                }
            }
        });

        // DOM References
        var labelDom = element.find('.my-select-label');
        var optionsDom = element.find('.my-select-ops');
        var backdrop = element.find('.my-select-backdrop');
        var mobileSelect = element.find('select');

        // DOM Event Listeners
        labelDom.on('click', function() {
            rePositionOps();
            optionsDom.toggleClass('active');
            backdrop.toggleClass('active');
        });
        backdrop.on('click', function() {
            optionsDom.removeClass('active');
            backdrop.removeClass('active');
        });
        element.on('keydown', function(ev) {
            switch (ev.which) {
                case 37: // left arrow
                case 38: // top arrow
                    preSelectPrev();
                    break;
                case 39: // right arrow
                case 40: // down arrow
                    preSelectNext();
                    break;
                case 13: // enter key
                    preSelectPush();
            }
        });

        // Initialization
        rePositionOps();
        $($window).on('resize', function() {
            rePositionOps();
        });
        if (isMobile) {
            mobileSelect.addClass('active');
        }

        // Positioning options
        function rePositionOps() {
            //optionsDom.width(labelDom.width());
            // optionsDom.css({
            //     top: labelDom.offset().top + labelDom.outerHeight(),
            //         left: labelDom.offset().left
            // });
            // Mobile ops
            mobileSelect.width(labelDom.outerWidth());
            mobileSelect.height(labelDom.outerHeight());
            // mobileSelect.css({
            //     top: labelDom.offset().top,
            //     left: labelDom.offset().left
            // });
        }

        // PreSelection logic:
        //  This controls option selecting and highlighting by pressing the arrow
        //  keys.
        var preSelected = 0;

        function updatePreSelection() {
            optionsDom.children().filter('.preselected').removeClass('preselected');
            optionsDom.find('div').eq(preSelected).addClass('preselected');
        }
        updatePreSelection();

        function preSelectNext() {
            preSelected = (preSelected + 1) % scope.ops.length;
            updatePreSelection();
        }

        function preSelectPrev() {
            preSelected = (preSelected - 1) % scope.ops.length;
            updatePreSelection();
        }

        function preSelectPush() {
            scope.selectOpt(scope.ops[preSelected]);
            scope.$apply();
        }
    }

    return {
        link: main,
        scope: {
            ops: '=selectPump',
            selection: '=selection'
        },
        template: '<div class="my-select-label" tabindex="0" title="{{selectedOpt.label}}"><div class="my-select-label-text">{{selectedOpt.label}}</div><span class="my-select-caret"><svg viewBox="0 0 100 60"><polyline points="10,10 50,50 90,10" style="fill:none;stroke:#f3f3f3;stroke-width:8;stroke-linecap:round;"/></svg></span></div><div class="my-select-backdrop"></div><div class="my-select-ops"><div ng-repeat="o in ops" ng-click="selectOpt(o)">{{o.label}}</div></div><select ng-options="opt.value as opt.label for opt in ops" ng-model="selection"></select>'
    };
});/* Attaching new lib file */app.service("analyticsMapService", function($http, $q, $rootScope, $location) {
    return ({
        getPumpTypeData: getPumpTypeData,
        getMapLocationData: getMapLocationData,
        getOverallEffData: getOverallEffData,
        getPerformanceData: getPerformanceData
    });

    function getPumpTypeData() {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                'Content-Type': 'application/json',
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetType"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getMapLocationData() {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Authorization": token,
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetData/machine"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getOverallEffData(pumpType) {
        if (pumpType != undefined && pumpType != "undefined") {
            $rootScope.showSpinner = true;
            var token = $rootScope.token_type + ' ' + $rootScope.access_token;
            var request = $http({
                method: "GET",
                header: {
                    "Authorization": token,
                    "Access-Control-Allow-Origin": "*",
                    "Access-Control-Allow-Credentials": "true",
                    "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                    "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                    "Content-Type": "application/json;charset:UTF-8"
                },
                url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getOverallEfficiency/" + pumpType

            });

            return (request.then(handleSuccess, handleError));
        }
    }

    function getPerformanceData(pumpType) {
        if (pumpType != undefined && pumpType != "undefined") {
            $rootScope.showSpinner = true;
            var token = $rootScope.token_type + ' ' + $rootScope.access_token;
            var request = $http({
                method: "GET",
                header: {
                    "Authorization": token,
                    "Access-Control-Allow-Origin": "*",
                    "Access-Control-Allow-Credentials": "true",
                    "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                    "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                    "Content-Type": "application/json;charset:UTF-8"
                },
                url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getPerformanceMetric/" + pumpType
            });
            return (request.then(handleSuccess, handleError));
        }
    }

    function handleError(response) {
        $rootScope.showSpinner = false;
        var ret = '';
        if (!angular.isObject(response.data) || !response.data.message) {
            ret = ($q.reject("An unknown error occurred."));
        }
        if (ret.length === 0) {
            ret = ($q.reject(response.data.message));
        }
        return ret;
    }

    function handleSuccess(response) {
        $rootScope.showSpinner = false;
        return (response.data);
    }
});/* Attaching new lib file */app.service("assetManagementService", function($http, $q, $rootScope, $location) {
    return ({
        getPredixDischargeGraphData: getPredixDischargeGraphData,
        getPredixEnergyGraphData: getPredixEnergyGraphData,
        getStoredDischargeGraphData: getStoredDischargeGraphData,
        getStoredEnergyGraphData: getStoredEnergyGraphData,
        getPredixVibrationGraphData: getPredixVibrationGraphData,
        getPredixDaysToFailureGraphData: getPredixDaysToFailureGraphData,
        getStoredVibrationGraphData: getStoredVibrationGraphData,
        getStoredDaysToFailureGraphData: getStoredDaysToFailureGraphData,
        getAssetInfoData: getAssetInfoData,
        getAssetsList: getAssetsList,
        getAnalyticsData: getAnalyticsData,
        getCavitationData: getCavitationData,
        updateAssetInfoData: updateAssetInfoData,
        executeTraining: executeTraining
    });

    function getPredixVibrationGraphData() {
        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getEnergyConsumptionGraph/machine/1"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getPredixDaysToFailureGraphData() {
        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getTimeseriesGraph/machine/1/tsIdentifier_vibration"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getStoredVibrationGraphData() {
        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getTimeseriesGraph/machine/1/tsIdentifier_daystofailure"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getStoredDaysToFailureGraphData() {
        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getEnergyConsumptionGraph/machine/1"
        });
        return (request.then(handleSuccess, handleError));
    }

    function executeTraining() {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Authorization": token,
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/analytics/train/machine/1?timeDuration=1"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getAnalyticsData(analyticsFilterData) {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "POST",
            data: analyticsFilterData,
            header: {
                "Authorization": token,
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/analytics"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getCavitationData(machineUri) {
        if (machineUri != undefined && machineUri != "undefined") {
            $rootScope.showSpinner = true;
            var token = $rootScope.token_type + ' ' + $rootScope.access_token;
            var request = $http({
                method: "GET",
                header: {
                    "Authorization": token,
                    "Access-Control-Allow-Origin": "*",
                    "Access-Control-Allow-Credentials": "true",
                    "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                    "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                    "Content-Type": "application/json;charset:UTF-8"
                },
                url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/predictedCavitationFailure" + machineUri
            });
            return (request.then(handleSuccess, handleError));
        }
    }

    function getPredixDischargeGraphData() {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Authorization": token,
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getDischargePressureGraph/machine/1"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getPredixEnergyGraphData() {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getEnergyConsumptionGraph/machine/1"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getStoredDischargeGraphData() {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getDischargePressureGraph/machine/3"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getStoredEnergyGraphData() {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getEnergyConsumptionGraph/machine/3"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getAssetsList() {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "GET",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetData/machine"
        });
        return (request.then(handleSuccess, handleError));

    }

    function getAssetInfoData(assetUri) {
        if (assetUri != undefined && assetUri != "undefined") {
            $rootScope.showSpinner = true;
            var token = $rootScope.token_type + ' ' + $rootScope.access_token;
            var request = $http({
                method: "GET",
                header: {
                    "Access-Control-Allow-Origin": "*",
                    "Access-Control-Allow-Credentials": "true",
                    "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                    "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                    "Content-Type": "application/json;charset:UTF-8"
                },
                url: 'https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetDataByAssetId' + assetUri
            });
            return (request.then(handleSuccess, handleError));
        }
    }

    function updateAssetInfoData(updatedAssetData) {

        $rootScope.showSpinner = true;
        var token = $rootScope.token_type + ' ' + $rootScope.access_token;
        var request = $http({
            method: "POST",
            header: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                "Content-Type": "application/json;charset:UTF-8"
            },
            data: updatedAssetData,
            url: 'https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/addUpdateAssetData/machine'
        });
        return (request.then(handleSuccess, handleError));

    }


    function handleError(response) {
        $rootScope.showSpinner = false;
        var ret = '';
        if (!angular.isObject(response.data) || !response.data.message) {
            ret = ($q.reject("An unknown error occurred."));
        }
        if (ret.length === 0) {
            ret = ($q.reject(response.data.message));
        }
        return ret;
    }

    function handleSuccess(response) {
        $rootScope.showSpinner = false;
        return (response.data);
    }
});
/* Attaching new lib file */app.service("getDataService", function($http,$q,$rootScope) {
    return ({
        getGetData: getGetData,
        getPostData: getPostData
    });

    function getGetData(url) {
        $rootScope.showSpinner = true;
        var request = $http({
            method: "get",
            url: url
        });
        return (request.then(handleSuccess, handleError));
    }

    function getPostData(url) {
        $rootScope.showSpinner = true;
    }

    function handleError(response) {
        $rootScope.showSpinner = false;
        var ret = '';
        if (!angular.isObject(response.data) || !response.data.message) {
            ret = ($q.reject("An unknown error occurred."));
        }
        if (ret.length === 0) {
            ret = ($q.reject(response.data.message));
        }
        return ret;
    }

    function handleSuccess(response) {
        $rootScope.showSpinner = false;
        return (response.data);
    }
});/* Attaching new lib file */app.service("loginService", function($http, $q, $rootScope) {
    return ({
        userLogin: userLogin
    });

    function userLogin(userDetails) {
         $rootScope.showSpinner = true;
        var request = $http({
            method: "POST",
            url: 'https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/login',
            data: {
                userDetails: userDetails
            }
        });
        return (request.then(handleSuccess, handleError));
    }

    function handleError(response) {
        var ret = '';
        if (!angular.isObject(response.data) || !response.data.message) {
            ret = ($q.reject("An unknown error occurred."));
        }
        if (ret.length == 0) {
            ret = ($q.reject(response.data.message));
        }
        return ret;
    }

    function handleSuccess(response) {
        $rootScope.showSpinner = false;
        return (response.data);
    }
});/* Attaching new lib file */app.service("locationMapService", function($http, $q, $location,$rootScope) {
    var map;
    var markers = [];
    return ({
        createOutdoorMap: createOutdoorMap,
        createMachineMarkers: createMachineMarkers,
        createAssetMachineMarkers: createAssetMachineMarkers
    });

    function createOutdoorMap(jsonData) {
        map = initialiseMap(jsonData);
        return map;
    }

    function initialiseMap(jsonData) {
        map = new google.maps.Map(document.getElementById('outdoorMap'), {
            scrollwheel: false,
            center: { lat: 46.759197, lng: 17.701408 },
            zoom: 3,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            styles: [
                { elementType: 'geometry', stylers: [{ color: '#4e575f' }] },
                { elementType: 'labels.text.stroke', stylers: [{ color: '#000' }] },
                { elementType: 'labels.text.fill', stylers: [{ color: '#000' }] },
                {
                    featureType: 'administrative.locality',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: 'transparent' }]
                },
                {
                    featureType: 'poi',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: 'transparent' }]
                },
                {
                    featureType: 'poi.park',
                    elementType: 'geometry',
                    stylers: [{ color: '#263c3f' }]
                },
                {
                    featureType: 'poi.park',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#6b9a76' }]
                },
                {
                    featureType: 'road',
                    elementType: 'geometry',
                    stylers: [{ color: '#38414e' }]
                },
                {
                    featureType: 'road',
                    elementType: 'geometry.stroke',
                    stylers: [{ color: '#212a37' }]
                },
                {
                    featureType: 'road',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#9ca5b3' }]
                },
                {
                    featureType: 'road.highway',
                    elementType: 'geometry',
                    stylers: [{ color: '#746855' }]
                },
                {
                    featureType: 'road.highway',
                    elementType: 'geometry.stroke',
                    stylers: [{ color: '#1f2835' }]
                },
                {
                    featureType: 'road.highway',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#f3d19c' }]
                },
                {
                    featureType: 'transit',
                    elementType: 'geometry',
                    stylers: [{ color: '#2f3948' }]
                },
                {
                    featureType: 'transit.station',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#d59563' }]
                },
                {
                    featureType: 'water',
                    elementType: 'geometry',
                    stylers: [{ color: '#434b52' }]
                },
                {
                    featureType: 'water',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#434b52' }]
                },
                {
                    featureType: 'water',
                    elementType: 'labels.text.stroke',
                    stylers: [{ color: '#434b52' }]
                }
            ]
        });
        return map;
    }

    function createMachineMarkers(locations, filter) {
        var latlng = map.getCenter();
        var marker, i, image, infowindow, locationData;
        setMapOnAll(null);

        var items = {
            filter: filter,
            out: []
        };

        if ((filter.FAILURE || filter.PREDICTING_FAILURE || filter.UNDERGOING_MAINTENANCE || filter.NO_DATA || filter.PERFORMING_OPTIMALLY) === false) {
            items.out = locations;
        } else {
            angular.forEach(locations, function(value, key) {
                if (this.filter[value.analyticAttributes.machineStatus] === true) {
                    this.out.push(value);
                }
            }, items);
        }
        locationData = items.out;
        angular.forEach(locationData, function(location, index) {

            if (location.analyticAttributes.machineStatus == 'FAILURE') {
                image = new google.maps.MarkerImage("../../assets/images/redMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else if (location.analyticAttributes.machineStatus == 'PREDICTING_FAILURE') {
                image = new google.maps.MarkerImage("../../assets/images/yellowMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else if (location.analyticAttributes.machineStatus == 'UNDERGOING_MAINTENANCE') {
                image = new google.maps.MarkerImage("../../assets/images/blueMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else if (location.analyticAttributes.machineStatus == 'NO_DATA') {
                image = new google.maps.MarkerImage("../../assets/images/orangeMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else if (location.analyticAttributes.machineStatus == 'PERFORMING_OPTIMALLY') {
                image = new google.maps.MarkerImage("../../assets/images/greenMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else {
                image = new google.maps.MarkerImage("../../assets/images/dashboard.png", null, null, null, new google.maps.Size(
                    35, 35));
            }
            marker = new google.maps.Marker({
                position: new google.maps.LatLng(location.latitude, location.longitude),
                map: map,
                icon: image
            });
            markers.push(marker);

            var content = "<div class='contentWindow col-xs-12 row'><h2>" + location.manufacturer + "</h2><div class='col-xs-12 '><div class='machineModelTitle titleSec col-xs-12'>Machine Model</div><div class='machineModelBody infSec col-xs-12'> " + location.modelName + "</div></div><div class='col-xs-12'><div class='avgPowerConTitle titleSec col-xs-12'>Average Power Consumption</div><div class='avgPowerConBody infSec col-xs-12'> " + location.analyticAttributes.averagePowerConsumption + "</div></div><div class='col-xs-12'><div class='effLevelTitle titleSec col-xs-12'>Efficiency Level</div><div class='effLevelBody infSec col-xs-12'>" + location.analyticAttributes.machineEfficiencyLevel + "</div></div></div>";

            var infowindow = new google.maps.InfoWindow({ disableAutoPan: true });

            google.maps.event.addListener(marker, 'click', (function(marker, content, infowindow) {
                return function() {
                    infowindow.setOptions({ pixelOffset: getInfowindowOffset(map, marker) });
                    infowindow.setContent(content);
                    infowindow.open(map, marker);
                };
            })(marker, content, infowindow));
            marker.addListener('mouseout', function() {
                infowindow.close();
            });
            // marker.addListener('click', function() {

            // });
            google.maps.event.addListener(infowindow, 'domready', function() {


                var iwOuter = $('.gm-style-iw');

                iwOuter.css('margin-top', '10px');
                // iwOuter.css('margin-left', '2px');

                var contentWindow = $('.contentWindow');

                var contentWindowParent = contentWindow.parent()
                contentWindowParent.css('overflow', 'hidden');

                var iwBackground = iwOuter.prev();


                iwBackground.children(':nth-child(2)').css({ 'display': 'none' });


                iwBackground.children(':nth-child(4)').css({ 'display': 'none' });


                //iwOuter.parent().parent().css({ left: '115px' });


                iwBackground.children(':nth-child(1)').attr('style', function(i, s) { return s + 'left: 76px !important;' });
                iwBackground.children(':nth-child(1)').css('display', 'none');


                iwBackground.children(':nth-child(3)').attr('style', function(i, s) { return s + 'left: 76px !important;' });
                iwBackground.children(':nth-child(3)').css('display', 'none');


                iwBackground.children(':nth-child(3)').find('div').children().css({ 'box-shadow': 'rgba(72, 181, 233, 0.6) 0px 1px 6px', 'z-index': '1' });


                var iwCloseBtn = iwOuter.next();


                iwCloseBtn.css({ opacity: '0', right: '38px', top: '3px', 'border-radius': '13px' });


                if ($('.iw-content').height() < 140) {
                    $('.iw-bottom-gradient').css({ display: 'none' });
                }

                iwCloseBtn.mouseout(function() {
                    $(this).css({ opacity: '1' });
                });
            });
            google.maps.event.addListener(map, 'click', function() {
                infowindow.close();
            });






            function getInfowindowOffset(map, marker) {
                var center = getPixelFromLatLng(map.getCenter()),
                    point = getPixelFromLatLng(marker.getPosition()),
                    quadrant = "",
                    offset;
                quadrant += (point.y > center.y) ? "b" : "t";
                quadrant += (point.x < center.x) ? "l" : "r";
                if (quadrant == "tr") {
                    offset = new google.maps.Size(-70, 315);
                } else if (quadrant == "tl") {
                    offset = new google.maps.Size(100, 315);
                } else if (quadrant == "br") {
                    offset = new google.maps.Size(-70, 20);
                } else if (quadrant == "bl") {
                    offset = new google.maps.Size(70, 20);
                }

                return offset;
            };

            function getPixelFromLatLng(latLng) {
                var projection = map.getProjection();
                var point = projection.fromLatLngToPoint(latLng);
                return point;
            }
        });
    }

    function setMapOnAll(map) {
        for (var i = 0; i < markers.length; i++) {
            markers[i].setMap(map);
        }
    }

    function createAssetMachineMarkers(location) {
        var marker, i, image;
        if (location.status == "MAINTENANCE") {
            image = new google.maps.MarkerImage("../../assets/images/blueMapDot.png", null, null, null, new google.maps.Size(
                35, 35));


        } else if (location.status == "FAILURE") {
            image = new google.maps.MarkerImage("../../assets/images/redMapDot.png", null, null, null, new google.maps.Size(
                35, 35));

        } else if (location.status == "PREDICTING_FAILURE") {
            image = new google.maps.MarkerImage("../../assets/images/yellowMapDot.png", null, null, null, new google.maps.Size(
                35, 35));

        } else if (location.status == "NO_DATA") {
            image = new google.maps.MarkerImage("../../assets/images/orangeMapDot.png", null, null, null, new google.maps.Size(
                35, 35));

        } else {
            image = new google.maps.MarkerImage("../../assets/images/greenMapDot.png", null, null, null, new google.maps.Size(
                35, 35));

        }
        marker = new google.maps.Marker({
            position: new google.maps.LatLng(location.lat, location.lon),
            map: map,
            icon: image
        });
        map.setCenter(new google.maps.LatLng(location.lat, location.lon));
        map.setZoom(6);
        markers.push(marker);

    }
});/* Attaching new lib file */app.service("notificationDashboardService", function($http, $q, $rootScope, $location) {
    return ({
        getNotificationData: getNotificationData
    });

    function getNotificationData() {
            $rootScope.showSpinner = true;
            var token = $rootScope.token_type + ' ' + $rootScope.access_token;
            var request = $http({
                method: "GET",
                header: {
                    "Access-Control-Allow-Origin": "*",
                    "Access-Control-Allow-Credentials": "true",
                    "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                    "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                    "Content-Type": "application/json;charset:UTF-8"
                },
                url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getNotificationsByCriteria"
            });
            return (request.then(handleSuccess, handleError));
        
    }

    function handleError(response) {
        $rootScope.showSpinner = false;
        var ret = '';
        if (!angular.isObject(response.data) || !response.data.message) {
            ret = ($q.reject("An unknown error occurred."));
        }
        if (ret.length === 0) {
            ret = ($q.reject(response.data.message));
        }
        return ret;
    }

    function handleSuccess(response) {
        $rootScope.showSpinner = false;
        return (response.data);
    }
});