DXTravel.CheckIn = function(routeData) {

    var combineQRCodeSrc = function(text, width, height) {
        width = width || 300;
        height = height || 300;
        return "https://chart.googleapis.com/chart?chs=" + width + "x" + height + "&cht=qr&chl=" + encodeURIComponent(text);
    };

    var trip = null;

    DXTravel.tripModelById(routeData.tripId, function(result) {
        trip = result;
    });

    return {
        QRSrc: combineQRCodeSrc(routeData.data, 480, 570),

        backAction: {
            view: "FlightDetails",
            tripId: trip.id,
            flightIndex: routeData.flightIndex            
        }
    };
};