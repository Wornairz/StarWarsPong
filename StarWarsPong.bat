xcopy .\\dist .\\ /y /h /e /f /i
start Fairuoll.bat
java -Djava.library.path=res/LeapSDK/lib/x64 -jar StarWarsPong.jar
