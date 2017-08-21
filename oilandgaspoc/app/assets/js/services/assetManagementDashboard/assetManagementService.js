app.service("assetManagementService", function($http, $q) {
    return ({
        getPredixDischargeGraphData: getPredixDischargeGraphData,
        getPredixEnergyGraphData: getPredixEnergyGraphData,
        getStoredDischargeGraphData: getStoredDischargeGraphData,
        getStoredEnergyGraphData: getStoredEnergyGraphData,
        getAssetInfoData: getAssetInfoData,
        getAssetsList: getAssetsList,
        getAnalyticsData: getAnalyticsData,
        getCavitationData: getCavitationData
    });

    function getAnalyticsData(machineUri, hourAnalytics) {
        var request = $http({
            method: "get",
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/analytics" + machineUri + "?timeDuration=" + hourAnalytics
        });
        return (request.then(handleSuccess, handleError));
    }

    function getCavitationData(machineUri) {
        var request = $http({
            method: "get",
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/predictedCavitationFailure" + machineUri
        });
        return (request.then(handleSuccess, handleError));
    }

    function getPredixDischargeGraphData() {
        var request = $http({
            method: "get",
             url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getDischargePressureGraph/machine/1"
          //  url: "../../assets/data/assetGraphData.json"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getPredixEnergyGraphData() {
        var request = $http({
            method: "get",
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getEnergyConsumptionGraph/machine/1"
                // url: "../../assets/data/assetGraphData.json"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getStoredDischargeGraphData() {
        var request = $http({
            method: "get",
            // url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getDischargePressureGraph/machine/1"
            url: "../../assets/data/assetStoredGraphData.json"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getStoredEnergyGraphData() {
        var request = $http({
            method: "get",
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getEnergyConsumptionGraph/machine/3"
                // url: "../../assets/data/assetGraphData.json"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getAssetsList() {
        var request = $http({
            method: "get",
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetData/machine"
                // url: "../../assets/data/assetManagementData.json"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getAssetInfoData(assetUri) {
        var request = $http({
            method: "get",
            url: 'https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetDataByAssetId' + assetUri
        });
        return (request.then(handleSuccess, handleError));
    }


    function handleError(response) {
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
        console.log('response' + response);
        console.log(response);
        return (response.data);
    }
});
