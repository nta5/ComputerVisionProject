# ComputerVisionProject

Project overview: 

This project uses various computer vision libraries to do the following:
  1) Extract text from the view of a camera or image.
  2) Use facial detection to obtain someone's emotion from a live camera or photo.
Additionally, this project uses a client server paradigm utilizing web sockets. Any type of data obtained from a client is sent to a
node js web socket server which then broadcasts it back to all the clients. If you want to view all the incoming data from each client,
you can go to the incoming data page.


Project setup instructions:

1. Server:
  a) Begin by installing node js as that is what we are running our server on: https://nodejs.org/en/download/
  b) make sure you have all the packages installed; open up the command line in the "ComputerVisionServer" and type the following: npm i
  c) to start the server type: node server.js
  
2. Client:
  a) All our clients are on android for this project, so make sure you have android studio installed and open it with that.
  b) Make sure you have the opencv-4.5.5.jar file in the app/libs. If for any reason it is missing, you can download it here: https://opencv.org/releases/
  c) Add the following dependencies to your build.gradle file:
  i)   implementation files('libs/opencv-4.5.5.jar')
  ii)  implementation 'com.google.android.gms:play-services-vision:20.1.3'
  iii) implementation 'com.google.mlkit:face-detection:16.1.5'
  iv)  implementation 'com.squareup.okhttp3:mockwebserver:4.10.0'
  d) Import the opencv android sdk into the project by downloading opencv-4.6.0-android-sdk.zip, unzip it, 
     then Android Studio > File > New > Import Module > enter path to folder "OpenCV-android-sdk/sdk".
  e) There is a socket listener class which contains the url for the server. Make sure to set the IP as your own. The port is set as 8080.
