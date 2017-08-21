app.service("loginService", function($http, $q, $rootScope) {
    return ({
        userLogin: userLogin
    });

    function userLogin(userDetails) {
         $rootScope.showSpinner = true;
        var request = $http({
            method: "POST",
            url: 'https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/login',
            data: {
                userDetails: userDetails
            }
        });
        return (request.then(handleSuccess, handleError));
    }

    function handleError(response) {
        var ret = '';
        if (!angular.isObject(response.data) || !response.data.message) {
            ret = ($q.reject("An unknown error occurred."));
        }
        if (ret.length == 0) {
            ret = ($q.reject(response.data.message));
        }
        return ret;
    }

    function handleSuccess(response) {
        $rootScope.showSpinner = false;
        return (response.data);
    }
});