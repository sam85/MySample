DXTravel.Index = function(routeData) {

    var nextTrip = ko.observable();

    DXTravel.db.trips.load().done(function(trips) {
        nextTrip(DXTravel.toTripModel(DXTravel.getNextTrip(trips)));
    });

    return {
        viewShown: function() {
            DXTravel.activeView(0);
        },

        nextTrip: nextTrip,

        trips: [ nextTrip() ], 

        mainMenu: [
            {
                text: "My Trips",
                iconScr: "images/my-trips.png",
                action: {
                    view: "MyTrips"
                }
            },

            {
                text: "Find a trip",
                iconScr: "images/trip-search.png",
                action: DXTravel.notImplemented
            },

            {
                text: "Suggested trips",
                iconScr: "images/suggested-trips.png",
                action: DXTravel.notImplemented
            }
        ]
    };
};