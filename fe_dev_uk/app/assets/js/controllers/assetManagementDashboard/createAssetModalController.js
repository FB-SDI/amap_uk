app.controller('createAssetModalController', ['$scope', 'close', 'globalData', 'assetManagementService', '$element', '$rootScope', function($scope, close, globalData, assetManagementService, $element, $rootScope) {
    $scope.assetEditInfo = {
        "uri": "",
        "modelName": "",
        "manufacturer": "",
        "pumpType": "",
        "serialNumber": "",
        "machineHorsePower":"",
        "latitude": "",
        "longitude": "",
        "image_url": "",
        "Temperature":"",
        "suctionPressureRequired":"",
        "maxPowerConsumption":"",
        "lastMaintenanceDate": "",
        "manufacturedDate": "",
        "capacity":"",
        "tsIdentifier_ingest": "",
        "tsIdentifier_discharge": "",
        "tsIdentifier_energyConsumption": "",
        "analyticAttributes": {
            "averagePowerConsumption":"",
            "machineEfficiencyLevel":"",
            "machineStatus": "",
            "cavitation":      ""  
        }
    }

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
    $scope.machineId="";
    $scope.$watch('machineId',function(newMachine){
        $scope.assetEditInfo.uri="/machine/"+newMachine;
    });
    $scope.saveData = function() {
        console.log("$scope.assetEditInfo");
        console.log($scope.assetEditInfo);
        assetManagementService.updateAssetInfoData($scope.assetEditInfo).then(function(resp) {
            if (resp.data.result=="Success") {
                //$element.modal('hide');
                //  Now close as normal, but give 500ms for bootstrap to animate
                $rootScope.$broadcast('refreshAssetSearch', true);
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