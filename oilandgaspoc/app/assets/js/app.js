'use strict';

var app = angular.module('app', ['ngRoute', 'nvd3', 'ngMaterial'])
    .config(['$routeProvider', '$locationProvider', '$httpProvider', function($routeProvider, $locationProvider, $httpProvider) {
        $routeProvider
            .when('/asset/', {
                templateUrl: '../../assets/templates/assetManagment/assetDetail.html',
                controller: 'assetDetailController'
            })
            .when('/dashboard/:dashboardSelect', {
                templateUrl: '../../assets/templates/common/dashboard.html',
                controller: 'dashboardController'
            })
            .otherwise({
                redirectTo: '/dashboard/analytics'
            });
        $httpProvider.defaults.useXDomain = true;
        //$locationProvider.html5Mode(true);
    }]);