app.service("locationMapService", function($http, $q) {
    var map;
    var markers = [];
    return ({
        createOutdoorMap: createOutdoorMap,
        createMachineMarkers: createMachineMarkers,
        createAssetMachineMarkers: createAssetMachineMarkers
    });

    function createOutdoorMap(jsonData) {
        map = initialiseMap(jsonData);
        return map;
    }

    function initialiseMap(jsonData) {
        map = new google.maps.Map(document.getElementById('outdoorMap'), {
            scrollwheel: false,
            center: { lat: 46.759197, lng: 17.701408 },
            zoom: 3,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            styles: [
                { elementType: 'geometry', stylers: [{ color: '#4e575f' }] },
                { elementType: 'labels.text.stroke', stylers: [{ color: '#000' }] },
                { elementType: 'labels.text.fill', stylers: [{ color: '#000' }] },
                {
                    featureType: 'administrative.locality',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: 'transparent' }]
                },
                {
                    featureType: 'poi',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: 'transparent' }]
                },
                {
                    featureType: 'poi.park',
                    elementType: 'geometry',
                    stylers: [{ color: '#263c3f' }]
                },
                {
                    featureType: 'poi.park',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#6b9a76' }]
                },
                {
                    featureType: 'road',
                    elementType: 'geometry',
                    stylers: [{ color: '#38414e' }]
                },
                {
                    featureType: 'road',
                    elementType: 'geometry.stroke',
                    stylers: [{ color: '#212a37' }]
                },
                {
                    featureType: 'road',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#9ca5b3' }]
                },
                {
                    featureType: 'road.highway',
                    elementType: 'geometry',
                    stylers: [{ color: '#746855' }]
                },
                {
                    featureType: 'road.highway',
                    elementType: 'geometry.stroke',
                    stylers: [{ color: '#1f2835' }]
                },
                {
                    featureType: 'road.highway',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#f3d19c' }]
                },
                {
                    featureType: 'transit',
                    elementType: 'geometry',
                    stylers: [{ color: '#2f3948' }]
                },
                {
                    featureType: 'transit.station',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#d59563' }]
                },
                {
                    featureType: 'water',
                    elementType: 'geometry',
                    stylers: [{ color: '#434b52' }]
                },
                {
                    featureType: 'water',
                    elementType: 'labels.text.fill',
                    stylers: [{ color: '#434b52' }]
                },
                {
                    featureType: 'water',
                    elementType: 'labels.text.stroke',
                    stylers: [{ color: '#434b52' }]
                }
            ]
        });
        return map;
    }

    function createMachineMarkers(locations, filter) {
        var latlng = map.getCenter();
        console.log(filter);
        var marker, i, image, infowindow, locationData;
        console.log(markers.length + "main");
        setMapOnAll(null);

        var items = {
            filter: filter,
            out: []
        };

        if ((filter.FAILURE || filter.PREDICTING_FAILURE || filter.UNDERGOING_MAINTENANCE || filter.NO_DATA || filter.PERFORMING_OPTIMALLY) === false) {
            items.out = locations;
        } else {
            angular.forEach(locations, function(value, key) {
                if (this.filter[value.analyticAttributes.machineStatus] === true) {
                    this.out.push(value);
                }
            }, items);
        }
        locationData = items.out;
        console.log(locationData);
        angular.forEach(locationData, function(location, index) {

            if (location.analyticAttributes.machineStatus == 'FAILURE') {
                image = new google.maps.MarkerImage("../../assets/images/redMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else if (location.analyticAttributes.machineStatus == 'PREDICTING_FAILURE') {
                image = new google.maps.MarkerImage("../../assets/images/yellowMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else if (location.analyticAttributes.machineStatus == 'UNDERGOING_MAINTENANCE') {
                image = new google.maps.MarkerImage("../../assets/images/blueMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else if (location.analyticAttributes.machineStatus == 'NO_DATA') {
                image = new google.maps.MarkerImage("../../assets/images/orangeMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else if (location.analyticAttributes.machineStatus == 'PERFORMING_OPTIMALLY') {
                image = new google.maps.MarkerImage("../../assets/images/greenMapDot.png", null, null, null, new google.maps.Size(
                    35, 35));
            } else {
                image = new google.maps.MarkerImage("../../assets/images/dashboard.png", null, null, null, new google.maps.Size(
                    35, 35));
            }
            marker = new google.maps.Marker({
                position: new google.maps.LatLng(location.latitude, location.longitude),
                map: map,
                icon: image
            });
            markers.push(marker);

            var content = "<div class='contentWindow col-xs-12 row'><h2>" + location.manufacturer + "</h2><div class='col-xs-12 '><div class='machineModelTitle titleSec col-xs-12'>Machine Model</div><div class='machineModelBody infSec col-xs-12'> " + location.modelName + "</div></div><div class='col-xs-12'><div class='avgPowerConTitle titleSec col-xs-12'>Average Power Consumption</div><div class='avgPowerConBody infSec col-xs-12'> " + location.analyticAttributes.averagePowerConsumption + "</div></div><div class='col-xs-12'><div class='effLevelTitle titleSec col-xs-12'>Efficiency Level</div><div class='effLevelBody infSec col-xs-12'>" + location.analyticAttributes.machineEfficiencyLevel + "</div></div></div>";

            var infowindow = new google.maps.InfoWindow({ disableAutoPan: true });















            google.maps.event.addListener(marker, 'mouseover', (function(marker, content, infowindow) {
                return function() {
                    infowindow.setOptions({ pixelOffset: getInfowindowOffset(map, marker) });
                    infowindow.setContent(content);
                    infowindow.open(map, marker);
                };
            })(marker, content, infowindow));
            marker.addListener('mouseout', function() {
                infowindow.close();
            });
            // marker.addListener('click', function() {

            //     console.log(marker);
            // });
            google.maps.event.addListener(infowindow, 'domready', function() {


                var iwOuter = $('.gm-style-iw');

                iwOuter.css('margin-top', '10px');
                // iwOuter.css('margin-left', '2px');

                var contentWindow = $('.contentWindow');

                var contentWindowParent = contentWindow.parent()
                contentWindowParent.css('overflow', 'hidden');
                console.log("contentWindowParent" + contentWindowParent);

                var iwBackground = iwOuter.prev();


                iwBackground.children(':nth-child(2)').css({ 'display': 'none' });


                iwBackground.children(':nth-child(4)').css({ 'display': 'none' });


                //iwOuter.parent().parent().css({ left: '115px' });


                iwBackground.children(':nth-child(1)').attr('style', function(i, s) { return s + 'left: 76px !important;' });
                iwBackground.children(':nth-child(1)').css('display', 'none');


                iwBackground.children(':nth-child(3)').attr('style', function(i, s) { return s + 'left: 76px !important;' });
                iwBackground.children(':nth-child(3)').css('display', 'none');


                iwBackground.children(':nth-child(3)').find('div').children().css({ 'box-shadow': 'rgba(72, 181, 233, 0.6) 0px 1px 6px', 'z-index': '1' });


                var iwCloseBtn = iwOuter.next();


                iwCloseBtn.css({ opacity: '0', right: '38px', top: '3px', 'border-radius': '13px' });


                if ($('.iw-content').height() < 140) {
                    $('.iw-bottom-gradient').css({ display: 'none' });
                }

                iwCloseBtn.mouseout(function() {
                    $(this).css({ opacity: '1' });
                });
            });
            google.maps.event.addListener(map, 'click', function() {
                infowindow.close();
            });






            function getInfowindowOffset(map, marker) {
                var center = getPixelFromLatLng(map.getCenter()),
                    point = getPixelFromLatLng(marker.getPosition()),
                    quadrant = "",
                    offset;
                quadrant += (point.y > center.y) ? "b" : "t";
                quadrant += (point.x < center.x) ? "l" : "r";
                if (quadrant == "tr") {
                    offset = new google.maps.Size(-70, 315);
                } else if (quadrant == "tl") {
                    offset = new google.maps.Size(100, 315);
                } else if (quadrant == "br") {
                    offset = new google.maps.Size(-70, 20);
                } else if (quadrant == "bl") {
                    offset = new google.maps.Size(70, 20);
                }

                return offset;
            };

            function getPixelFromLatLng(latLng) {
                var projection = map.getProjection();
                var point = projection.fromLatLngToPoint(latLng);
                return point;
            }
        });
    }

    function setMapOnAll(map) {
        for (var i = 0; i < markers.length; i++) {
            markers[i].setMap(map);
        }
    }

    function createAssetMachineMarkers(location) {
        var marker, i, image;
        if (location.status == "MAINTENANCE") {
            image = new google.maps.MarkerImage("../../assets/images/blueMapDot.png", null, null, null, new google.maps.Size(
                35, 35));


        } else if (location.status == "FAILURE") {
            image = new google.maps.MarkerImage("../../assets/images/redMapDot.png", null, null, null, new google.maps.Size(
                35, 35));

        } else if (location.status == "PREDICTING_FAILURE") {
            image = new google.maps.MarkerImage("../../assets/images/yellowMapDot.png", null, null, null, new google.maps.Size(
                35, 35));

        } else if (location.status == "NO_DATA") {
            image = new google.maps.MarkerImage("../../assets/images/orangeMapDot.png", null, null, null, new google.maps.Size(
                35, 35));

        } else {
            image = new google.maps.MarkerImage("../../assets/images/greenMapDot.png", null, null, null, new google.maps.Size(
                35, 35));

        }
        marker = new google.maps.Marker({
            position: new google.maps.LatLng(location.lat, location.lon),
            map: map,
            icon: image
        });
        map.setCenter(new google.maps.LatLng(location.lat, location.lon));
        map.setZoom(6);
        markers.push(marker);

    }
});
