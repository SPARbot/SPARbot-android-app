# SPARbot-android-app

This repository contains Android project files for Video Calling and Virtual Reality.

# Video Calling Application

This application provides the user to communicate with another person through video calling over the internet. We have used Sinch API to implement video calling feature for the telepresencce robot. We have used WebRTC PeerConnection module to connect directly to another peer. The following steps are taken to place a video call:

1) Login with username into the application.

2) Enter the username of the person whom you wish to call.

3) If the other person is logged in, then a call is connected when the call button is pressed.

4) Pressing the End Call button terminates the call and call details are displayed to the caller.

#Virtual Reality Application

Android application to provide VR experience to the user. This application communicates with the Raspberry Pi 3 to receive the live camera feed from RasPi 3 camera and display a stereoscopic image to the user on the mobile. Google Cardboard SDK is used to achieve VR experience on the Android application. The following steps are taken to start Virtual Reality Application:

1) Login to the VR Application.

2) Enter the IP address of the Raspberry Pi 3. This creates a connection between the application and the RasPi 3.

3) Press OK button and the camera feed from the RasPi 3 integrated camera is visible on the application.

4) The steroscopic video is available on the application and when the user pulls the trigger of the VR Goggles, The camera moves with respect to the movement of the uer's head.

