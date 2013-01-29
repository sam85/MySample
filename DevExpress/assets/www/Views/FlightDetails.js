DXTravel.FlightDetails = function(routeData) {
    
    var trip = null,
        flight = null,
        details = ko.observableArray();
    
    DXTravel.tripModelById(routeData.tripId, function(result) {
        trip = result;
        flight = trip.flights[routeData.flightIndex];

        details([{
            key: flight.from + " to " + flight.to,
            items: [
                {
                    key: "Departs:",
                    value: Globalize.format(flight.date, "MMMM d, yyyy"),
                    icon: "images/flight.png",
                    navigation: false
                },
                {
                    key: "Flight Number:",
                    value: flight.number,
                    icon: "images/flight.png",
                    navigation: false
                },
                {
                    key: "Confirmation:",
                    value: flight.confirmNumber,
                    icon: "images/flight.png",
                    navigation: true
                },
                {
                    key: "Terminal:",
                    value: flight.terminal,
                    icon: "images/flight.png",
                    navigation: false
                },
                {
                    key: "Gate:",
                    value: flight.gate,
                    icon: "images/flight.png",
                    navigation: false
                },
                {
                    key: "Seat:",
                    value: flight.seat,
                    icon: "images/flight.png",
                    navigation: false
                }                
            ]
        }]);
    });

    return {
        details: details,

        handleItemClick: function(e) {
            if(e.itemData.value === flight.confirmNumber) {
                var uri = DXTravel.app.router.format({
                    view: "CheckIn",
                    tripId: trip.id,
                    flightIndex: routeData.flightIndex,
                    data: flight.confirmNumber
                });

                DXTravel.app.navigate(uri);
            }
        },

        backAction: {
            view: "TripDetails",
            tripId: routeData.tripId
        },

        editFlight: DXTravel.notImplemented
    };
};