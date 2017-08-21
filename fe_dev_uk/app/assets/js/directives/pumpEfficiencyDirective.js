var arc = d3.svg.arc();
app.directive('pumpEfficiencyDirective', ['$window', function($window) {


    function link(scope, el, attr) {
        var diameter = 0;
        if (el[0])
            diameter = el[0].clientWidth;
        if (diameter > 0)
            var rp1 = radialProgress(el[0])
                .diameter(diameter)
                .value(attr.pumpefficiencyval)
                .render();
        angular.element($window).bind('resize', function() {
            if (el[0])
                el[0].innerHTML = ""
            var diameter = 0;
            if (el[0])
                diameter = el[0].clientWidth;
            if (diameter > 0)
                var rp1 = radialProgress(el[0])
                    .diameter(diameter)
                    .value(attr.pumpefficiencyval)
                    .render();
        });
    }
    return {
        link: link,
        restrict: 'A'
    };
}]);