# How to use

Fill config.properties fields before launching the tests to set all the needed configuration

To launch the tests, use these commands in the project root dir:

- gradle build
- gradle cucumber

# Dependencies

The least possible dependencies, can be checked in build.gradle file

- 'io.rest-assured:rest-assured:3.2.0'
- 'io.cucumber:cucumber-java:4.0.0'
- 'junit:junit:4.12'
- 'org.seleniumhq.selenium:selenium-java:2.45.0'
- 'org.seleniumhq.selenium:selenium-support:2.45.0'

# Tools

- Java JDK 8
- Visual Studio Code
- SDKMAN
- Gradle

# Comments

All the made code is under the folder 'src'
Locators should have been defined in another file with significative names.
I had a time shortage and needed to investigate Gradle further to get it as I wanted.
Funny enough, the sort tests fails because it has to, one price is misplaced in the page.
This check can be commented to let the next steps run.