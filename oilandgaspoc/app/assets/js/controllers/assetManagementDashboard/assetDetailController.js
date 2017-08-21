app.controller('assetDetailController', ['$routeParams', '$scope', '$rootScope', 'locationMapService', 'getDataService', '$timeout', '$interval', 'assetManagementService', function($routeParams, $scope, $rootScope, locationMapService, getDataService, $timeout, $interval, assetManagementService) {
    $scope.sensorData = [{
            "sensorType": "Thermal Sensor",
            "sensorStatus": "optimal_performance"
        },
        {
            "sensorType": "Pt100",
            "sensorStatus": "optimal_performance"
        },
        {
            "sensorType": "QC250",
            "sensorStatus": "no_data"
        },
        {
            "sensorType": "FLS100",
            "sensorStatus": "optimal_performance"
        },
        {
            "sensorType": "PTC Thermistors",
            "sensorStatus": "no_data"
        },
        {
            "sensorType": "CLS",
            "sensorStatus": "optimal_performance"
        },
        {
            "sensorType": "VLS 100",
            "sensorStatus": "optimal_performance"
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
    $rootScope.activePage = 'assetManagement';
    if (!$rootScope.assetUri) {
        location.replace('/#/dashboard/assetsearch');

    }
    $scope.hourData = [{
            "label": "1 Hour",
            "value": "1h"
        },
        {
            "label": "4 Hour",
            "value": "4h"
        },
        {
            "label": "8 Hour",
            "value": "8h"
        }
    ];
    $scope.selectHour = "1h";
    $scope.$watch('selectHour', function(newHourVal) {
        console.log("newval" + newHourVal);

    });
    $scope.graphselect = "false";
    $scope.$watch('graphselect', function(newVal) {
        console.log(newVal);
        if (newVal == true) {
            assetManagementService.getStoredDischargeGraphData().then(function(resp) {
                $scope.dischargeData = resp["Discharge.pump11"];
                $scope.suctionData = resp["Ingest.pump11"];
                dischargeAndSuctionChart($scope.dischargeData, $scope.suctionData);
            });
            assetManagementService.getStoredEnergyGraphData().then(function(resp) {
                $scope.energyConsumption = resp["EnergyConsumption.pump11"];
                energyConsumptionChart($scope.energyConsumption);
            });


        } else {
            assetManagementService.getPredixDischargeGraphData().then(function(resp) {
                $scope.dischargeData = resp["Discharge.pump9"];
                $scope.suctionData = resp["Ingest.pump9"];
                dischargeAndSuctionChart($scope.dischargeData, $scope.suctionData);
            });
            assetManagementService.getPredixEnergyGraphData().then(function(resp) {
                $scope.energyConsumption = resp["EnergyConsumption.pump9"];
                energyConsumptionChart($scope.energyConsumption);
            });

        }
    })

    var effValue = 0;
    assetManagementService.getPredixDischargeGraphData().then(function(resp) {
        $scope.dischargeData = resp["Discharge.pump9"];
        $scope.suctionData = resp["Ingest.pump9"];
        dischargeAndSuctionChart($scope.dischargeData, $scope.suctionData);
    });
    assetManagementService.getPredixEnergyGraphData().then(function(resp) {
        $scope.energyConsumption = resp["EnergyConsumption.pump9"];
        console.log($scope.energyConsumption);
        energyConsumptionChart($scope.energyConsumption);
    });

    assetManagementService.getAssetInfoData($rootScope.assetUri).then(function(resp) {
        $scope.assetInfo = resp;
        effValue = $scope.assetInfo.analyticAttributes.machineEfficiencyLevel;
        start(effValue);
        $scope.assetLocation = {
            "lat": $scope.assetInfo.latitude,
            "lon": $scope.assetInfo.longitude,
            "status": $scope.assetInfo.analyticAttributes.machineStatus,
        }

        var filter = $scope.assetInfo.status;
        var map = locationMapService.createOutdoorMap($scope.assetLocation);
        if (map)
            locationMapService.createAssetMachineMarkers($scope.assetLocation);

    });
    $scope.runAnalytics = function() {


        console.log($rootScope.assetUri);
        // assetManagementService.getAnalyticsData($rootScope.assetUri, $scope.selectHour).then(function(resp) {
        //     var analyticsStatus = resp;
        //     console.log("ye rha reps" + resp);
        // });
        var machineStatusCode, machineStatusColor;
        assetManagementService.getAssetInfoData($rootScope.assetUri).then(function(resp) {
            $scope.assetInfo = resp;
            effValue = $scope.assetInfo.analyticAttributes.machineEfficiencyLevel;
            start(effValue);
            $scope.assetLocation = {
                "lat": $scope.assetInfo.latitude,
                "lon": $scope.assetInfo.longitude,
                "status": $scope.assetInfo.analyticAttributes.machineStatus,
            }

            var filter = $scope.assetInfo.status;
            var map = locationMapService.createOutdoorMap($scope.assetLocation);
            if (map) {
                locationMapService.createAssetMachineMarkers($scope.assetLocation);
            }

            machineStatusCode = $scope.assetInfo.analyticAttributes.machineStatus;

        }).then(function(resp) {
            console.log(resp + "resp2");
            console.log(machineStatusCode + "machineStatusCode");
            if (machineStatusCode == "PREDICTING_FAILURE") {
                machineStatusColor = "#f9e75a";
                $scope.analyticsMachineStatusData = "Predicting Failure";
            } else if (machineStatusCode == "FAILED") {
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

            assetManagementService.getCavitationData($rootScope.assetUri).then(function(resp) {
                var cavitationValue = parseInt(resp.CavitationFailureNumber);
                cavitationGauge(machineStatusColor, cavitationValue);
            });
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
                    innerRadius: '80%',
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
                    innerRadius: '80%',
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
                        '<span style="font-size:16px;color:silver">DAYS</span></div>'
                },
                tooltip: {
                    valueSuffix: ' Days'
                }
            }]

        }));
    }

    function start(effValueCheck) {
        var rp1 = radialProgress(document.getElementById('overallEffDiv'))
            .diameter(225)
            .value(effValueCheck)
            .render();
    }
    var chart;
    energyConsumptionChart();
    dischargeAndSuctionChart();

    function energyConsumptionChart(energyConsumpGraphData) {
        // var array2 = []
        // angular.forEach(data2, function(data) {
        //     var array1 = [];
        //     array1.push(parseInt(data[0]));
        //     array1.push(parseInt(data[1]));
        //     array2.push(array1);
        // });
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
                name: 'AAPL',
                color: "#b9dc5b",
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
                name: 'Discharge',
                color: "#f77163",
                data: dishargeGraphData,
                tooltip: {
                    valueDecimals: 2
                },
                showInNavigator: true
            }, {
                name: 'Suction',
                color: "#eaa156",
                data: suctionGraphData,
                tooltip: {
                    valueDecimals: 2
                },
                showInNavigator: true
            }]
        });


    }



}]);
