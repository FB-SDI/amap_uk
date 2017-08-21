app.service("analyticsMapService", function($http, $q, $rootScope, $location) {
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
});