Note: IDK if the instructions here works lol, haven't tested them

## Instructions
- Get the zip folder from the release page
- Extract the zip file
- Make sure you have JDK22 or higher version
- Run this command in the folder directory: `java -jar {jar-file-name}`
- And it should run
- If not, just run it in IntelliJ or something to run it or even fix the bug yourself

- The beatmap folder contains the beatmaps that you can play
- You can even add your own beatmap there, osu mania 4k only though. (There's a class called OszExtractor in the code somewhere, run it.)
- The beatsync.db contains the scores that you set

I know this readme is unhinged, but I am too sleep deprived.
If you are interested in running it but wasn't able to, just contact me through the Issues Page.

If you want to package the code after making changes, 
change the ENVIRONMENT constant in GameConfig to "PROD" from "DEV".
Then run `mvn clean package`. Make sure to change it back to "DEV"
when running it in the IDE though.