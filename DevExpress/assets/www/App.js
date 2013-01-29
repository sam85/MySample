!function() {
   
    $(function() {        
        DevExpress.devices.forceDevice("iPhone");

        var app = DXTravel.app = new DevExpress.framework.html.HtmlApplication({
            ns: DXTravel,
            themeClasses: "dx-theme-ios dx-theme-ios-typography",
            defaultLayout: DXTravel.config.defaultLayout,
            navigation: DXTravel.config.navigation
        });
        app.router.register("CheckIn/:tripId/:flightIndex/:data", { view: "CheckIn" });
        app.router.register("Map/:tripId/:appointmentIndex", { view: "Map" });
        app.router.register("FlightDetails/:tripId/:flightIndex", { view: "FlightDetails" });
        app.router.register("TripDetails/:tripId", { view: "TripDetails" });
        app.router.register(":view", { view: "Index" }); // Catch-all route

        app.navigate();
    });

    var tripModelById = function(id, callback) {
        return DXTravel.tripById(id, function(trip) {
            callback(toTripModel(trip));
        });
    };

    var toTripModel = function(trip) {
        return new PhoneTripViewModel(trip);
    };

    var PhoneTripViewModel = DXTravel.BaseTripViewModel.inherit({

        ctor: function(trip) {
            this.callBase(trip);            

            var currentSelected = false,
                now = new Date();

            var calcStage = function(item) {
                if(item.date < now)
                    return "past";
                if(currentSelected)
                    return "future";
                currentSelected = true;
                return "current";
            };

            this.detailItems = DevExpress.data.query(this._generateDetailItems(trip))
                .select(function(item) {
                    var stage = calcStage(item),
                        stageCss = "trip-details-item-stage-" + stage;

                    return $.extend({}, item, {
                        stage: stage,
                        stageCss: stageCss,
                        timeString: Globalize.format(item.date, "h:mm tt")
                    });
                })
                .sortBy("date")
                .groupBy(function(item) {
                    return new Date(Math.floor(item.date / DXTravel.MS_PER_DAY) * DXTravel.MS_PER_DAY); // date w/o time
                })
                .select(function(item) {
                    return $.extend({}, item, {
                        key: Globalize.format(item.key, "MMMM d, yyyy")
                    });
                })
                .toArray();
        },

        _flightToDetailItem: function(flight, index) {
            return {                
                type: "flight",
                title: flight.from + " to " + flight.to,
                date: flight.date,
                icon: "images/flight.png",
                action: { view: "FlightDetails", tripId: this.id, flightIndex: index }
            };
        },

        _appointmentToDetailItem: function(item, index) {
            return {
                type: "appointment",
                title: item.name,
                date: item.date,
                icon: "images/marker.png",
                action: { view: "Map", tripId: this.id, appointmentIndex: index }
            };
        },

        _generateDetailItems: function(trip) {        
            var TRIP = this;
            return $.map(trip.flights || [], $.proxy(this._flightToDetailItem, this)).concat(
                $.map(TRIP.tripAppointments() || [], $.proxy(this._appointmentToDetailItem, this))
            );
        },        
                
        editTrip: DXTravel.notImplemented
    });


    $.extend(DXTravel, {
        activeView: ko.observable(-1),
        tripModelById: tripModelById,
        toTripModel: toTripModel
    });
}();