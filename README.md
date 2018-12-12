# Nexus
Our android application provides a map, search bar, and two buttons that redirect to Uber or Lyft respectively. On the map, the user's current location is displayed using a pin (using Google's Map API), and the user can enter the end destination in the search bar, also dropping a pin (using Google's places API). Then, using calls to the Uber and Lyft APIs through our EC2-hosted node server, information on the price and time estimate from the current location to the desired destination. Once the information on the buttons is updated, the user can tap either button and be redirected to the corresponding application with the start and end address preloaded to make ordering a ride more seamless.

# APIs Used
Uber: https://developer.uber.com/

Lyft: https://www.lyft.com/developers

Google Maps for Android: https://developers.google.com/maps/documentation/android-sdk/intro

Google Places: https://developers.google.com/places/web-service/autocomplete
