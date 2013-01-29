DXTravel.Map = function(routeData) {
    
    var prevAppointment = null,
        appointment = null;

    DXTravel.tripModelById(routeData.tripId, function(trip) {
        prevAppointment = trip.tripAppointments()[routeData.appointmentIndex - 1];
        appointment = trip.tripAppointments()[routeData.appointmentIndex];
    });

    var vm = {
        backAction: {
            view: "TripDetails",
            tripId: routeData.tripId
        },

        editMap: DXTravel.notImplemented,

        markers: ko.observableArray([]),
        routes: ko.observableArray([]),
    };

    if(prevAppointment && prevAppointment.address !== appointment.address) {
        vm.routes([{
            weight: 5,
            locations: [
                prevAppointment.address,
                appointment.address
            ]
        }]);
        vm.markers([
            {position: prevAppointment.address, tooltip: "<b>" + prevAppointment.name + "</b><br />" + Globalize.format(prevAppointment.date, "MMMM d, yyyy h:mm tt")},
            {position: appointment.address, tooltip: "<b>" + appointment.name + "</b><br />" + Globalize.format(appointment.date, "MMMM d, yyyy h:mm tt")}
        ]);
    } else {
        vm.markers([{
            size: "mid",
            label: "S",
            position: appointment.address,
            tooltip: "<b>" + appointment.name + "</b><br />" + Globalize.format(appointment.date, "MMMM d, yyyy h:mm tt")
        }]);
    }

    return vm;
};