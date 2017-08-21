app.controller('ModalController', ['$scope', 'close', 'globalData', 'assetManagementService', '$element', '$rootScope', function($scope, close, globalData, assetManagementService, $element, $rootScope) {
    console.log('this is the ');
    console.log(globalData.assetInfo);
    $scope.showAdvancedSec = false;
    $scope.showAdvanced = function() {
        $scope.showAdvancedSec = !$scope.showAdvancedSec;
        document.getElementById('editAssetDetail').style.marginTop = '-5px';
    }
    $scope.assetEditInfo = angular.copy(globalData.assetInfo);
    globalData.assetInfo = '';
    $scope.uploadme = "../assets/images/machine-1.png";
    $scope.uploadImage = function() {
        var fd = new FormData();
        var imgBlob = dataURItoBlob($scope.uploadme);
        fd.append('file', imgBlob);
        $http.post(
                'imageURL',
                fd, {
                    transformRequest: angular.identity,
                    headers: {
                        'Content-Type': undefined
                    }
                }
            )
            .success(function(response) {
                console.log('success', response);
            })
            .error(function(response) {
                console.log('error', response);
            });
    }
    $scope.removeImage = function() {
        $scope.uploadme = "https://x1.xingassets.com/assets/frontend_minified/img/users/nobody_m.original.jpg";
    };

    function dataURItoBlob(dataURI) {
        var binary = atob(dataURI.split(',')[1]);
        var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
        var array = [];
        for (var i = 0; i < binary.length; i++) {
            array.push(binary.charCodeAt(i));
        }
        return new Blob([new Uint8Array(array)], {
            type: mimeString
        });
    }
    $scope.saveData = function() {
        $scope.assetEditInfo.temperature=parseInt($scope.assetEditInfo.temperature)
        assetManagementService.updateAssetInfoData($scope.assetEditInfo).then(function(resp) {
            if (resp.data.result) {
                //$element.modal('hide');
                $rootScope.$broadcast('refreshAssetDetail', true);

                //  Now close as normal, but give 500ms for bootstrap to animate
                $element.modal('hide');
                console.log("here");
                close("",500);
            } else {

            }
        });
    }
    $scope.closeModal = function() {
        $element.modal('hide');
        console.log("here");
        close("",500);
    }

}]);