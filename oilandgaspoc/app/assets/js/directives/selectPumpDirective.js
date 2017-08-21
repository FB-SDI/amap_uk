app.directive('selectPump', function($window) {
    function main(scope, element, attrs) {
        console.log('mySelect directive');

        // Selecting model value
        for (var idx in scope.ops) {
            if (scope.ops[idx].value == scope.selection) {
                scope.selectedOpt = scope.ops[idx];
            }
        }

        // Is a mobile device
        var isMobile = false;
        if (/ipad|iphone|android/gi.test($window.navigator.userAgent)) {
            isMobile = true;
        }

        // Select an option
        scope.selectOpt = function(opt) {
            scope.selection = opt.value;
            //scope.selectedOpt = opt;
            optionsDom.removeClass('active');
            backdrop.removeClass('active');
        };

        scope.$watch('selection', function(newVal) {
            for (var idx in scope.ops) {
                if (scope.ops[idx].value == newVal) {
                    scope.selectedOpt = scope.ops[idx];
                }
            }
            console.log("scope.selectedOpt");
        });

        // DOM References
        var labelDom = element.find('.my-select-label');
        var optionsDom = element.find('.my-select-ops');
        var backdrop = element.find('.my-select-backdrop');
        var mobileSelect = element.find('select');

        // DOM Event Listeners
        labelDom.on('click', function() {
            rePositionOps();
            optionsDom.toggleClass('active');
            backdrop.toggleClass('active');
        });
        backdrop.on('click', function() {
            optionsDom.removeClass('active');
            backdrop.removeClass('active');
        });
        element.on('keydown', function(ev) {
            switch (ev.which) {
                case 37: // left arrow
                case 38: // top arrow
                    preSelectPrev();
                    break;
                case 39: // right arrow
                case 40: // down arrow
                    preSelectNext();
                    break;
                case 13: // enter key
                    preSelectPush();
            }
        });

        // Initialization
        rePositionOps();
        $($window).on('resize', function() {
            rePositionOps();
        });
        if (isMobile) {
            mobileSelect.addClass('active');
        }

        // Positioning options
        function rePositionOps() {
            //optionsDom.width(labelDom.width());
            // optionsDom.css({
            //     top: labelDom.offset().top + labelDom.outerHeight(),
            //         left: labelDom.offset().left
            // });
            // Mobile ops
            mobileSelect.width(labelDom.outerWidth());
            mobileSelect.height(labelDom.outerHeight());
            // mobileSelect.css({
            //     top: labelDom.offset().top,
            //     left: labelDom.offset().left
            // });
        }

        // PreSelection logic:
        //  This controls option selecting and highlighting by pressing the arrow
        //  keys.
        var preSelected = 0;

        function updatePreSelection() {
            optionsDom.children().filter('.preselected').removeClass('preselected');
            optionsDom.find('div').eq(preSelected).addClass('preselected');
            console.log(preSelected);
        }
        updatePreSelection();

        function preSelectNext() {
            console.log(scope.ops.length);
            preSelected = (preSelected + 1) % scope.ops.length;
            updatePreSelection();
        }

        function preSelectPrev() {
            console.log(scope.ops.length);
            preSelected = (preSelected - 1) % scope.ops.length;
            updatePreSelection();
        }

        function preSelectPush() {
            scope.selectOpt(scope.ops[preSelected]);
            scope.$apply();
        }
    }

    return {
        link: main,
        scope: {
            ops: '=selectPump',
            selection: '=selection'
        },
        template: '<div class="my-select-label" tabindex="0" title="{{selectedOpt.label}}"><div class="my-select-label-text">{{selectedOpt.label}}</div><span class="my-select-caret"><svg viewBox="0 0 100 60"><polyline points="10,10 50,50 90,10" style="fill:none;stroke:#f3f3f3;stroke-width:8;stroke-linecap:round;"/></svg></span></div><div class="my-select-backdrop"></div><div class="my-select-ops"><div ng-repeat="o in ops" ng-click="selectOpt(o)">{{o.label}}</div></div><select ng-options="opt.value as opt.label for opt in ops" ng-model="selection"></select>'
    };
});