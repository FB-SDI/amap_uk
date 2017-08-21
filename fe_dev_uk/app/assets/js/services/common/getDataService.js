app.service("getDataService", function($http,$q,$rootScope) {
    return ({
        getGetData: getGetData,
        getPostData: getPostData
    });

    function getGetData(url) {
        $rootScope.showSpinner = true;
        var request = $http({
            method: "get",
            url: url
        });
        return (request.then(handleSuccess, handleError));
    }

    function getPostData(url) {
        $rootScope.showSpinner = true;
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