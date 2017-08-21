app.service("analyticsMapService", function($http, $q) {
    return ({
        getPumpTypeData: getPumpTypeData,
        getMapLocationData: getMapLocationData,
        getOverallEffData: getOverallEffData,
        getPerformanceData: getPerformanceData
    });

    function getPumpTypeData() {
        var request = $http({
            method: "get",
             url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetType"
            //url: "../../assets/data/pumpType.json"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getMapLocationData() {
        var request = $http({
            method: "get",
            // url: "../../assets/data/mapLocation.json"
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetData/machine"
        });
        return (request.then(handleSuccess, handleError));
    }

    function getOverallEffData(pumpType) {
        var request = $http({
            method: "get",
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getOverallEfficiency/" + pumpType

            //url: "../../assets/data/overallEfficiencyData.json"
        });
        console.log(request);
        return (request.then(handleSuccess, handleError));
    }

    function getPerformanceData(pumpType) {
        var request = $http({
            method: "get",
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getPerformanceMetric/" + pumpType
                // url: "../../assets/data/performanceData.json"
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
