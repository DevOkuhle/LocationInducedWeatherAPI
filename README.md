# LocationInducedWeatherAPI

** Third Party dependencies:

* The repository uses CI/CD pipelining in github, the code is written in YAML languages. This makes it seemless to automate in github, it a very easy less code way to automate repository actions.
* Github send automated emails to my email account for Push Notification.
* Githubs automatically, build the project to check for any errors.
* I did not use any code generation using an online tools, this repo is build is self-built using my knowledge and experience.
* Because of time constraints I could not get to unit testing, but the repository is written in TDD, most of the testable code resided in the ViewModel.

** Architecture and Frameworks

* The App uses MVVM architecture and clean code, the code is written for TDD.
* The app is witten is JetPack compose mainly, with one Fragment that makes use of an XML layout.
* The app uses Dagger-Hilt for dependency injection.
* The app uses only StateFlow and Flow objects and no LiveData
* The app made use of Moshi as an converter factory, mainly because it is fater and more efficient than Gson.
* The App makes use of Room database to record all of the entities, for each time the user has performed a successful OpenWeather API transaction and also when the user add a location as a Favourite.
* The app makes use of Localization as far as possible in the app, resources for string, dimension, util constant classes to abstract premitive values, to promote re-usabbility, easily updatable and retreival code.
* The app makes use of separation of concerns as far as possible, these include the overall project structure, the naming of classes and grouping of data, to promote accessibility.
* The app uses the principal of re-usability and generics, this is to help write shortened code, improve accessibility and easiness on apps resources.
* The uses coroutines to run non-blocking asynchronous service invocations.

** App Functions

The app can perform the following function:
1. On start-up it check whether the user has given the location permision.
   1.1 If the user has not granted the permission, it the launches a Android specific permission request
   1.2 In case the user accept the permission then the user can move on to step number 2.
   1.3 If the user rejectes the permission, then a custom made alert dialog will pop up. This is to educate the user as to why this permission is mandatory and needed.
  1.3(a) When the user presses on the "Grant permission" the app return to step 1.1, Android allows only the user to grant the permission on only two occassions.
  1.3(b) In case the user has declined the app on the two occassions then another dialog pop-up, now this time it inform the user on how to grant permission on the their device Settings, the clickable text automatically navigates exactly to their app settings.

2. In the case the user manages to grant location permission, the app then checks if the GPS is enables.
  2.1 If the GPS is not enable, Android GPS dialog pops up, providing an interface for the user to turn in on.
  2.1(a) If the user declines it, the app does he same check and the Android GPS dialog pop up again, this happens indefinitely up until the user enables the GPS.

3. There after the app will then be able to determine the current location in real-time as it updates every second or so.
4. Once the user's current location coordinates has been captured the app then makes a service call to the OpenWeather API endpoints.
5. The Response can either be a failure or a success.
5.1 In case the Weather Response is a success, then the app maps the given values to displays the weather according to the specified design.
5.1(a) A side note the values for the 5 day forecast are aveages, i.e, in each day there is a forecast for every 3 hours, so the apps displays the average temperature for each day.
5.2 In case the Response is a failure, the app distinguishes failures into three types =, i.e, Internet failure, General Network failure suchs as timeout, and then General Error prone failure.
5.2(a) In case the service failure is due to General Network or Error prone failures, the apps then displays a failure screen, with the message.
5.3(b) In case the service is due to no Internet, then the apps displays the last updated weather values. The no Internet search is strategically placed in the in an API configurations file, meaning the app does not observe the overall internet in the app, but only when the user want to do an internet transaction. If the API call fails due to no internet then uses the dats cached.

6. The user can be able to view MenuItems in the app, on the top left.
6.1 There are 4 Menuitems in the list
   6.1 The first one allows the user to save a favourite locations via ROOM, the app store the Favourite Location Profile as well as Weather Forecast attributes. This helps in preventing the user to add the location more that once. Also, the user, is prompted to give a custom name for thier profile.
   6.2 The seccond one is for viewing saves favourite location list, if the list is greater that 10 favourites location then a Search Bar is added to help the user filter their search.
   6.3 This third one, allows the user to view all their location in google maps, as markers.
   6.4 The fourth one is a Autocomplete search, when the user has selected a serach, it then navigates to view that search in the third's option in google maps.

7. In View Favourites list option, every item in the list is able to run weather forecast using that Favourtie Location attributes, meaning that you can perform the OpenWeather foreasts for  each entry in the list simply by clicking on the item.
7.1 Moreover, when peforming weather API forecast and the user has no internet, the app will go on offline mode and will show the last updated weather forcast that were for that is specific Favourite Location item, and the last updated time of the forecast will be displayed on the top right of the app.

** Graceful 

* The app takes into consideration of writing app that is graful. Errors and failure are anticipated and a plan of action is made prior for each possible failure, to promote usability and aslo to keep the user updated about the apps functionality.
* AlertDialogs are used frequently used in the app to keep the user updated as to answer the why, when and what they might have since they will not be speaking to anyone directly when using the app, aslo it help for new users to get used to using the app, shortening the learning gap.
* Perfomace and anylical information had been intergrated in the app, the app uses Firestore's services to keep in track the performance metrics, crashes, issues, and also to share the code when a new push is made. This helps in understanding the relationship the users have with the app, how often they use, error-proness of the app, the volume of users and usage.
