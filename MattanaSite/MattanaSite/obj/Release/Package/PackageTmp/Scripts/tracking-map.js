
// This example creates a 2-pixel-wide red polyline showing the path of
// the first trans-Pacific flight between Oakland, CA, and Brisbane,
// Australia which was made by Charles Kingsford Smith.
var flightPlanCoordinates = [];
flightPlanCoordinates = @Html.Raw(Json.Encode(ViewBag.Data));
var geocoder;
var map;
var flightPath;
var maker;
function initMap() {
    geocoder = new google.maps.Geocoder();
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 16,
        mapTypeId: 'terrain'
    });

    flightPath = new google.maps.Polyline({
        path: flightPlanCoordinates,
        geodesic: true,
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 1
    });

    flightPath.setMap(map);

    if (flightPlanCoordinates.length > 1) {
        addMarker(flightPlanCoordinates[flightPlanCoordinates.length - 1], map,  '');
        map.setCenter( flightPlanCoordinates[flightPlanCoordinates.length -1]);
    }


}

function clearMaker() {
    maker.setMap(null);
}

function addMarker(location, map, label) {
    // Add the marker at the clicked location, and add the next-available label
    // from the array of alphabetical characters.
    maker = new google.maps.Marker({
        position: location,
        label: label,
        map: map,
        icon: '/content/people.png',
        animation: google.maps.Animation.DROP
    });
    window.setTimeout(marker, 1000);

    geocoder.geocode({'location': location}, function(results, status) {
        if (status === 'OK') {
            if (results[0]) {
                var contentString = results[0].formatted_address;

                var infowindow = new google.maps.InfoWindow({
                    content: contentString
                });
                marker.addListener('click', function() {
                    infowindow.open(map, marker);
                });

            } else {
                window.alert('No results found');
            }
        } else {
            window.alert('Geocoder failed due to: ' + status);
        }
    });

          
}
