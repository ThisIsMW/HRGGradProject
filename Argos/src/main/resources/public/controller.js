/**
 * Adapted from guide at http://spring.io/guides/gs/consuming-rest-jquery/
 * Author: Max Wootton
 */
$(document).ready(function() {
    $.ajax({
        url: "http://localhost:8080/getDeals"
    }).then(function(data) {
    	console.log(data);
       $('.deals-title').append(data.title);
    });
});