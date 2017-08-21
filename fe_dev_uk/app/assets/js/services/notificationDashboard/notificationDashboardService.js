app.service("notificationDashboardService", function($http, $q, $rootScope, $location) {
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