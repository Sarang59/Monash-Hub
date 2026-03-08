MonashHub Android App By Group4_Lab04

A modern Android application developed with Jetpack Compose and Material3, providing an intuitive user experience for Monash students with all-in-one principle that supports student's life. 

📱Features : 

Sign up using Firebase Authentication & save user data to Firebase Firestore Database.
Login using Firebase or Google.
Create profile and set Avatar.
Account settings with different themes options.
Search peers.
Follow and Unfollow peers on Search Fragment.
Navigate peers from Search to searched peer Profile.
Allow a peer to Edit Account Info.
Upload image to Google Drive with Firebase Database and Retrieve & Display.
Upload new post picture with content.
Allow a peer to share the post to any other platforms as well.
Allows a peer to see the posts from other peers as well according to their interests.
Likes and Total Number of Likes.
Save and Total Number of Saves.
Display user Own Posts on Profile Page.
Allow the peer to see their own statistics as well.
Allow to chat with the other peers through chat fragment.
Display Notifications for any new messages.
Displays the different Job opportunities with the different filters and sort.
Display all the four campus map for easy navigation.


🛠️ Tech Stack

| Component                               | Technology                                        |
|-----------------------------------------|---------------------------------------------------|
| Language                                | Kotlin                                            |
| IDE                                     | Android Studio                                    |
| Backend                                 | Firebase Firestore & Firebase Auth                |
| Maps                                    | Google Maps SDK                                   |
| Route Navigation And Places Suggestions | OpenRouteService Api with Retrofit                |
| UI Design                               | Material Design                                   |
| Authentication                          | Firebase Authentication along with google and OTP |
| Job Opportunities                       | Ardunzo Api with Retrofit                         |
| Chat and Notifications                  | Room Database                                     |



📦 Project Setup

Prerequisites: 

- Android Studio Hedgehog (or newer)
- Android SDK Target Version: 35
- Java Version: 11
- Kotlin Version: 2.0.21
- Firebase Setup for firestore database along with google authentication 
- Google Play Services enabled
- Permissions for Fine_Location, Coarse_Location, internet access, Read and Write External Storage
- Google Maps sdk along with api key
- Google Drive api enabled with service account for photos uploading on cloud
- OpenRouteService api with the Key
- Ardunzo Api key For job and description

Emulator Requirements:

- System Image: `Pixel 6 API 34` with Google Play Services
- Internet Access: Required for Firebase, Drive, Maps, and Jobs feed
- ACCESS_FINE_LOCATION and ACCESS_COARSE_Location – Required For real-time navigation along with setting the start location manually (if runs on emulator) for navigation routes. 

🚀 Steps To Build: 
There are 2 ways mentioned below to get the source code - one by downloading the zip and other by cloning the repository using Monash ID.

Get source code by downloading the zip:
If you are downloading the source code then follow the steps mentioned below to set up the IDE.

- Open gitlab and select download zip option from code dropdown.
- Extract the zip folder and store it at your preferred location in your device.
- Now, open Android Studio and select the open folder option in it.
- Select the extracted folder and Android Studio will open the source code.
- Sync the Gradle Files (if not auto synced) and wait for some time and then if runs on emulator set the location of emulator.

Get source code by cloning the Git repository:
If you are cloning the repository then follow the steps mentioned below to set up the IDE.

- Open Android Studio IDE and select “Get from VCS”.
- If you're inside an open project, go to: File → New → Project from Version Control.
- Now, open gitlab and copy the HTTPS URL from the code dropdown options.
- Paste the copied URL in the Android Studio URL section.
- Click the clone button.
- The system will ask for your Username and password.
- Please enter your Monash email id as username and gitlab personal access token as password.
- If you do not have your personal token then you will be required to generate it before cloning the repository.
- After the successful authentication, the repository will be cloned.
- Now Sync the Gradle Files (if not auto synced) and wait for some time and then if runs on emulator set the location of emulator.


How to execute the code:
To run the code, open mainActivity.kt and press the run button at the top with the emulator selected and the name of app visible.

Troubleshooting

Target SDK not found or incompatible SDK version.
Issue: Android Studio throws an error related to missing or incompatible Target SDK.
Solution: Go to File → Project Structure → SDK Location and Ensure that the SDK Version is set to API 34 (Android 14) or later.
          If not installed:
          Open SDK Manager (toolbar icon or Tools → SDK Manager) and Install API Level 34 (Android 14) and click Apply and Sync the project again.


Google Play Services not available in emulator.
Issue: Features like MapView, Post Sharing, or Route Navigation fail due to missing Google Play services.
Solution: Ensure that you're using a Google Play-enabled emulator. Go to Tools → Device Manager and Create or edit a virtual device (e.g., Pixel 6). 
          Choose a system image labeled with "Google APIs" or "Google Play". 
          Launch the emulator again.

Alternatively, test the app on a real Android device with Google Play services enabled.

Gradle Sync or Plugin Compatibility Issues
Issue: Gradle sync fails due to version mismatch or plugin incompatibility.
Solution: Ensure your Gradle Plugin version is compatible with Compose and Material3. 
          Open build.gradle (Project-level) and check: classpath 'com.android.tools.build:gradle:8.3.1'
          Then check your build.gradle (App-level) for: compileSdk = 34  and composeCompilerVersion = "1.5.10"
          Click File → Sync Gradle.

Git Authentication issue while cloning the repository.
Solution: Make sure you are using your Monash id to login. Also, check if the personal access token is not expired. If the token has expired then generate the new token using gitlab. If the git authentication still fails then you can download the zip of source code as mentioned above.