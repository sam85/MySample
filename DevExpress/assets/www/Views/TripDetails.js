DXTravel.TripDetails = function(routeData) {

    var trip = ko.observable();

    DXTravel.tripModelById(routeData.tripId, function(result) {
        trip(result);
    });
    
    return {
        trip: trip,
        editTrip: DXTravel.notImplemented
    };
};