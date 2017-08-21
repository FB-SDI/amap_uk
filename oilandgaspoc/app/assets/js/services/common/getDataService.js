app.service("getDataService", function($http, $q) {
    return ({
        getGetData: getGetData,
        getPostData: getPostData
    });

    function getGetData(url) {
        var request = $http({
            method: "get",
            url: url
        });
        return (request.then(handleSuccess, handleError));
    }

    function getPostData(url) {

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