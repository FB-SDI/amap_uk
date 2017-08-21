var arc = d3.svg.arc();
app.directive('metricChart', ['$window', function($window) {
    function link(scope, el, attr) {
        if (document.getElementById('metricChart'))
            scope.width = document.getElementById('metricChart').clientWidth;
        if (scope.width > 0)
            draw(scope, el, attr);
        angular.element($window).bind('resize', function() {
            d3.select("#metricChart").select("svg").remove();
            if (document.getElementById('metricChart'))
                scope.width = document.getElementById('metricChart').clientWidth;
            if (scope.width > 0)
                draw(scope, el, attr);
            scope.$digest();
        });
    }
    return {
        link: link,
        restrict: 'E',
        scope: { 'data': '=' }
    };
}]);

function draw(scope, el, attr) {
    var color = d3.scale.ordinal().range(["#51e566", "#f9e75a", "#f25170", "#51aef3", "#fc8c5a"]);
    var data = scope.data;
    var height = 200;
    var min = Math.min(scope.width, height);
    var svg = d3.select(el[0]).append('svg');
    var pie = d3.layout.pie().sort(null);
    // define width of donut
    arc.outerRadius(min / 2 * 0.8)
        .innerRadius(min / 2 * 1);

    pie.value(function(d) { return +d.value; });
    svg.attr({ width: scope.width, height: height });
    var g = svg.append('g')
        // center the donut chart
        .attr('transform', 'translate(' + scope.width / 2 + ',' + height / 2 + ')');

    // add the <path>s 
    var arcs = g.selectAll('path').data(pie(data))
        .enter().append('path')
        .attr('fill-opacity', 1)
        .attr('fill', function(d, i) { return color(i); })
        // store the initial angles
        .each(function(d) { return this._current = d });
    scope.$watch('data', function(newVal, oldVal) {
        // console.log("an element within `data` changed!");
        var duration = 750;
        arcs.data(pie(scope.data)); //.attr('d', arc)
        arcs.transition().duration(duration).attrTween('d', arcTween);
    }, true);
}

function arcTween(a) {
    // see: http://bl.ocks.org/mbostock/1346410
    var i = d3.interpolate(this._current, a);
    this._current = i(0);
    return function(t) {
        return arc(i(t));
    };
}