(function($, DX, undefined) {

    var NAVIGATION_TOGGLE_DURATION = 400;

    DX.framework.html.SlideOutController = DX.framework.html.DefaultLayoutController.inherit({

        init: function(options) {
            this.callBase(options);
            this._navigationManager.navigating.add($.proxy(this._onNavigating, this));
        },

        _onNavigating: function(args) {
            var self = this;
            if(this._isNavigationVisible) {
                args.cancel = true;
                this._toggleNavigation(this.$viewPort.children()).done(function() {
                    self._disableTransitions = true;
                    self._navigationManager.navigate(args.uri);
                });
            }
        },

        _isPlaceholderEmpty: function (viewInfo) {
            var $markup = viewInfo.renderResult.$markup;
            var toolbar = $markup.find(".layout-toolbar").data("dxToolbar");
            var items = toolbar.option("items");
            var backCommands = $.grep(items, function (item) {
                return item.align == "left";
            });
            return !backCommands.length;
        },

        _showViewImpl: function(viewInfo, target) {
            var self = this;
            self._initNavigation(viewInfo.renderResult.$markup);
            if(self._isPlaceholderEmpty(viewInfo)) {
                self._addNavigationButton(viewInfo);
            }
            if(!self._navigationInited) {
                self.currentViewIndex = 0;
                self._navigationInited = true;
            }
            var promise = self.callBase(viewInfo, target);
            promise.done(function() {
                self._disableTransitions = false;
            });
            return promise;
        },

        _changeView: function(viewInfo) {
            var $toolbarBottom = viewInfo.renderResult.$markup.find(".layout-toolbar-bottom, .view-toolbar-bottom"),
                toolbarBottom = $toolbarBottom.data("dxToolbar");
            if(toolbarBottom && toolbarBottom.option("items").length) {
                viewInfo.renderResult.$markup.find(".layout-content").addClass("has-toolbar-bottom");
            }
            this.callBase.apply(this, arguments);
        },

        _addNavigationButton: function(viewInfo) {
            var $markup = viewInfo.renderResult.$markup;
            var $toolbar = $markup.find(".layout-toolbar");
            var command = new DX.framework.dxCommand({
                action: $.proxy(this._toggleNavigation, this, $markup),
                location: "menu",
                icon: "menu"
            });
            viewInfo.model.commands.push(command);
            DX.framework.html.commandToDXWidgetAdapters.dxToolbar.addCommand($toolbar, command, { showIcon: true, showText: false });
        },

        _initNavigation: function($markup) {
            this._isNavigationVisible = false;
            var navigationWidth = this._getNavigationWidth();
            this._initSwipeable($markup);
        },

        _initSwipeable: function($markup) {
            var self = this;
            var $layoutFrame = this._getLayoutFrame($markup);

            if(!$layoutFrame.data("dxSwipeable")) {
                var $navigation = this._getNavigation($markup);
                var navigationWidth = self._getNavigationWidth();

                $layoutFrame.dxSwipeable({
                    elastic: false,
                    start: function(e) {
                        e.maxLeftOffset = self._isNavigationVisible ? 1 : 0;
                        e.maxRightOffset = self._isNavigationVisible ? 0 : 1;

                        if(self.currentViewIndex > 0) {
                            var $previousView = self._navigationManager.getViewByIndex(self.currentViewIndex - 1).renderResult.$markup;
                            var $prevLayoutFrame = self._getLayoutFrame($previousView);
                            $layoutFrame.parent().append($prevLayoutFrame);
                            $layoutFrame.css({ "z-index": 1 });

                            $prevLayoutFrame.css({ "z-index": 0, position: "absolute" });
                            DX.fx.setProp($prevLayoutFrame, "left", 0);

                            $prevLayoutFrame.show();
                            self._initSwipeable($prevLayoutFrame);
                        }
                    },
                    update: function(e) {
                        var left = (e.offset + self._isNavigationVisible) * navigationWidth;
                        DX.fx.setProp($layoutFrame, "left", left);
                    },
                    end: function(e) {
                        if(e.offset !== 0) {
                            self._isNavigationVisible = e.offset > 0;
                        }
                    },
                    itemWidthFunc: function() {
                        return navigationWidth;
                    }
                });
            }
        },

        _getNavigation: function($markup) {
            return $markup.find(".dx-navigation-list");
        },

        _getLayoutFrame: function($markup) {
            return $markup.find(".layout-frame");
        },

        _getNavigationWidth: function() {
            var maxWidth = 300;
            var width = this.$viewPort.width() - 40;
            return width > maxWidth ? maxWidth : width;
        },

        _toggleNavigation: function($markup) {
            var self = this;
            var $navigation = this._getNavigation($markup),
                $layoutFrame = this._getLayoutFrame($markup);

            var promise = DX.fx.animate({
                $element: $layoutFrame,
                to: { left: this._isNavigationVisible ? 0 : this._getNavigationWidth() },
                duration: NAVIGATION_TOGGLE_DURATION
            });
            self._isNavigationVisible = !self._isNavigationVisible;

            return promise;
        }

    });

    DX.framework.html.layoutControllers.slideout = new DX.framework.html.SlideOutController();

})(jQuery, DevExpress);