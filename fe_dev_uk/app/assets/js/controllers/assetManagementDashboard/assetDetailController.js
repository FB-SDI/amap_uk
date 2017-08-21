app.controller('assetDetailController', ['$routeParams', '$scope', '$rootScope', 'locationMapService', 'getDataService', '$timeout', '$interval', 'assetManagementService', 'globalData', 'ModalService', '$location', function($routeParams, $scope, $rootScope, locationMapService, getDataService, $timeout, $interval, assetManagementService, globalData, ModalService, $location) {
    if ($rootScope.access_token == undefined) {
        $location.path("/login");
    } else {
        $rootScope.activePage = 'assetManagement';
        if (!$rootScope.assetUri) {
            location.replace('/#/dashboard/assetsearch');

        };
        $scope.sensorData = [{
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
                    $scope.dischargeData = resp["Discharge.pump92"];
                    $scope.suctionData = resp["Ingest.pump92"];
                    dischargeAndSuctionChart($scope.dischargeData, $scope.suctionData);
                });
                assetManagementService.getStoredEnergyGraphData().then(function(resp) {
                    $scope.energyConsumption = resp["EnergyConsumption.pump92"];
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