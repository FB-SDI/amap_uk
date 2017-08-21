'use strict';
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
}]);