# HRGGradProject
Graduate Project for HRG
------------------------
Installing & Running
This project comes in a pre-compiled format, all you need to do is run a single .jar file. The steps in doing so are listed below:
1. Download argosHUKD.jar
2. Copy this file into a working directory - we will need to access this directory from the command line. 
3. Open up terminal/cmd, depending on your system. 
4. Change directory to the location of the downloaded JAR file
5. Run the JAR file using java -jar argosHUKD.jar
****If you have an error with JAVA_HOME, follow instructions listed here: https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/*****
6. You should see the following to indicate Spring Boot has initialised:
 :: Spring Boot ::        (v1.3.2.RELEASE)
7.The program has been initialised when you can see a blanking cursor. The last line will be spatcherServlet': initialization completed in (x) ms
8. Open up an internet browser and type in "http://localhost:8080"
9. WAIT!. The program has not been optimised for HTTP requests, so the page will take around 20 seconds to load so please be patient!
10. That's it! You should be able to view the top 10 deals and follow links.
11. (Optional) if you'd like to look at my API directly, type in http://localhost:8080/getDeals.

Viewing Source Code
I've also uploaded my Eclipse project file into github, follow the directory structure to view my Client and Server files. Both are listed in the /src/main/java path. 
