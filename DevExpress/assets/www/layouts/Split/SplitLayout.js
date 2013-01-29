(function($, DX, undefined) {

    DX.framework.html.SplitLayoutController = DX.framework.html.DefaultLayoutController.inherit({

        init: function(options) {
            this.callBase(options);
            this._navigationManager.navigating.add($.proxy(this._onNavigating, this));
        },

        _onNavigating: function(args) {
            var self = this;
            if(this.hasFirstView) {
                args.options.target = DX.framework.NavigationManager.NAVIGATION_TARGETS.current;
            }
        },

        _showViewImpl: function(viewInfo, target) {
            var self = this;
            var leftSide = this.$viewPort.find(".left-content");
            var leftFooter = this.$viewPort.find(".left-footer");
            var $markup = viewInfo.renderResult.$markup;
            var content = $markup.find(".content");
            var toolbar = $markup.find(".footer-toolbar");
            var showViewInFrame = viewInfo.model.targetFrame === "navigation";

            if(leftSide.length && !showViewInFrame) {
                self.callBase.apply(self, arguments);
                $markup.find(".left-content").append(leftSide.children());
                $markup.find(".left-footer").append(leftFooter.children());
                this.$viewPort.append($markup);
            }
            else {
                self.callBase.apply(self, arguments);
                this.$viewPort.append($markup);
                content.appendTo($markup.find(".left-content"));
                toolbar.appendTo($markup.find(".left-footer"));
                this.hasFirstView = true;
            }

            $markup.show();
            return $.Deferred().resolve().promise();
        }
    });

    DX.framework.html.layoutControllers.split = new DX.framework.html.SplitLayoutController();

})(jQuery, DevExpress);