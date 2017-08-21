app.controller('dashboardController', ['$routeParams', '$scope', '$rootScope', '$location', 'locationMapService', 'assetManagementService', '$timeout', '$interval','$route',function($routeParams, $scope, $rootScope, $location, locationMapService, assetManagementService, $timeout, $interval,$route) {
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
}]);