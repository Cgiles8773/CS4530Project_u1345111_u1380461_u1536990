https://utah.instructure.com/courses/1173118/pages/drawing-app-client-requirements?module_item_id=28504263 <- Full app requirements
https://utah.instructure.com/courses/1173118/assignments/16216460 <- Phase 1 requirements (Near future goals)
https://utah.instructure.com/courses/1173118/assignments/16216459 <- Link to this phase's requirements
# Group Members
Collin Giles, Eric Nguyen, Josh Nguyen
# Sketches & Wireframes
Link to images
https://github.com/Cgiles8773/CS4530Project_u1345111_u1380461_u1536990/blob/main/Wireframe%26sketches.jpg
# Task Breakdown - What, who, when
### Assignment Description:
  In the first phase, we aim for drawing functionality, and limited data persistence (i.e. the drawings don't need to be saved after the application is closed.)  
  The five key components for phase 1 are as follows:  
  MVVM architecture: The app must use the MVVM pattern - any data that persists across screen rotations must be stored in a ViewModel.  
  Splash screen: One of those screens that shows when you launch an app  
  Drawing capability: Some space for users to draw on the screen. (Note in the full customer requirements, we're asked to make the drawing space square.)  
  Pen customization: Allow the user to modify the pen color, size, and shape (Simple shapes like circle, square, etc.)  
  Unit testing: Automated tests that cover most of our implementation  
  Stretch goal: If all the above are completed, we can add save/load to user drawings.
## Layouts
  activity_splash.xml- intro screen
  
  activity_main.xml- main draw
  
  activity_brush_settings.xml- brush setttings can just leave in main if want, but could be seperate screen too ?, 
  in my head this is a poput 
## Classes

brush - holds color shape size opacity

coord - holds point storage

strokes = whats drawn 

mainactivity - main daw 

draw - connects inputs 
## Tests
  Test data persistence for the screen
  Test pen customization
  Test MVVM architecture functions
  Test yo momma
## Task Assignment
Jacob 
- models
- -connect ui
- toolbar
Collin 
- create repo
- layouts
-testings
Eric
-layouts
- integrate view model state
