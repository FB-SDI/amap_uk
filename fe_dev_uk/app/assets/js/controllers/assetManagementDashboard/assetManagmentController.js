app.controller('assetManagementController', ['$routeParams', '$scope', '$rootScope', 'assetManagementService', 'globalData','ModalService' ,'$location',function($routeParams, $scope, $rootScope, assetManagementService, globalData,ModalService,$location) {
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
});