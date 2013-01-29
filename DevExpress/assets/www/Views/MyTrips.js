DXTravel.MyTrips = function(routeData) {

    var groupedTrips = ko.observableArray([]);

    DXTravel.db.trips.load().done(function(trips) {
        groupedTrips([
            {
                key: "Current",
                items: [DXTravel.toTripModel(DXTravel.getNextTrip(trips))]
            },
            {
                key: "Upcoming",
                items: $.map(DXTravel.getUpcomingTrips(trips), DXTravel.toTripModel)
            }
        ]);
    });

    return {
        groupedTrips: groupedTrips,

        viewShown: function() {
            DXTravel.activeView(1);
        }
    };
};