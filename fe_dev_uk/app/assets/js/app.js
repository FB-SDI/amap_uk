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
    });