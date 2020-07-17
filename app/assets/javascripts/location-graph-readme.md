# Updating the json graph for the location autocomplete

Replace `/assets/javascripts/location-autocomplete-graph.json` with the json from https://github.com/alphagov/govuk-country-and-territory-autocomplete/tree/master/dist

Go to a page with the country input and run the below js in the console.
It will output new json to the console. Replace the `location-autocomplete-graph.json` contents with this.

<pre><code>
fetch('/binding-tariff-application/assets/javascripts/location-autocomplete-graph.json')
.then(function(response) {
    return response.json();
})
.then(function(locJson) {

    //console.log(typeof locJson)
    var permittedCountries = document.querySelectorAll('select option[value]:not([value=""])');
    var arrPermittedCountries = []
    Array.from(permittedCountries).forEach(function(c){
        arrPermittedCountries.push(c.value)
    })
    var arrPermittedGraph = {};
    locs = locJson
    for (var key in locJson) {
        if (locJson.hasOwnProperty(key)) {
            var obj = locJson[key];
            // exclude territories
            if(key.indexOf("territory") == -1){
                // exclude countries which don't point to a country in the select
                if(key.indexOf("country:") != -1){
                    // loop allowed countries
                    if(arrPermittedCountries.includes(key.replace("country:", ""))){
                        arrPermittedGraph[key] = obj
                    }
                }
                if(key.indexOf("nym:") != -1){
                    // loop allowed countries
                    var fromCountry = obj.edges.from
                    //console.log(fromCountry)
                    if(fromCountry.length > 0){
                        if(arrPermittedCountries.includes(fromCountry[0].replace("country:", ""))){
                            arrPermittedGraph[key] = obj
                        }
                    }
                }
                if(key.indexOf("uk:") != -1){
                    // push all uk countries to uk
                    arrPermittedGraph[key] = obj
                }
            }
        }
    }
    console.log(JSON.stringify(arrPermittedGraph))
})
</code></pre>