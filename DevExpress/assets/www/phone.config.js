// NOTE object below must be a valid JSON
window.DXTravel = $.extend(true, window.DXTravel, {
    "config": {
        "defaultLayout": "default",
        "navigation": [
            {
                "title": "Home",
                "iconSrc": "images/home.png",
                "action": "#Index"
            },
            {
                "title": "My",
                "iconSrc": "images/my.png",
                "action": "#MyTrips"
            },
            {
                "title": "Find",
                "iconSrc": "images/search.png",
                "action": DXTravel.notImplemented
            },
            {
                "title": "Suggested",
                "iconSrc": "images/suggested.png",
                "action": DXTravel.notImplemented
            }
        ]
    }
});