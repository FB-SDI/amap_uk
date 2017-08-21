app.service("notificationDashboardService", function($http, $q) {
    return ({
        getNotificationData: getNotificationData
    });

    function getNotificationData() {
        var request = $http({
            method: "get",
            url: "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getNotificationsByCriteria"
                // url: "../../assets/data/notificationData.json"
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