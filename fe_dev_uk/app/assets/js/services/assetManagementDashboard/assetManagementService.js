app.service("assetManagementService", function($http, $q, $rootScope, $location) {
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
