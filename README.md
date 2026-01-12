Tripplor is an Android app that we created for the CS498 Mobile Computing course. 
The idea behind the app is to help last-minute travelers quickly find nearby attractions, 
view useful information about them, and easily organize daily plans. The app uses the 
user's location to display nearby points of interest and connects to Firebase to store 
data such as user accounts, plans, and reviews.

Features 
1. User registration, login and password reset 
2. Home screen showing nearby attractions using Geoapify API
3. Explore screen with all attractions and filters
4. Map screen with attraction markers
5. Attraction details with description, photos and option to add the place to today's
   or tomorrow's plan
6. Reviews section (view + add reviews)
7. Plans screen to organize the user's day
8. Profile screen to view and edit user information
9. Push notifications for reminders

Technologies used#
1.  Android Studio
2. Firebase Firestore
3. Firebase Authentication
4. Geoapify API 
5. Google Maps SDK 
6. Java

Installation instructions
To run the app, you need:
1.  Android Studio 
2. A Firebase project with a google-services.json file added to its folder application 
3. A Geoapify API key 
4. A Google Maps API key if you are using maps
After adding the keys and syncing Gradle, the project can be run on an emulator or physical device.

Future expansion
Tripplor has the scalability to grow with features such as a real-time map showing visitor 
density, personalized travel suggestions based on user preferences, and digital tour packages
in collaboration with tour guides.