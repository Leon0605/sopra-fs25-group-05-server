# Habla!: A multilingual ChatApp

## Introduction
Our primary goal for this project was to create a chat app, that would enable users that don't speak the same language to effortlessly chat with each other by integrating a translation service into the chat itself thus allowing to overcome language barriers. 
Our secondary goal was to utilize the chats for language learning by providing a flashcards that can be created from real messages sent. 


## Technologies
- Google Cloud flexible app engine for serverside deployment
- Google Cloud Storage to store profile pictures
- SpringBoot/REST for API
- WebSockets for real time chatting

## High-level components
Below is a list of the core components used for the backend of our application.
### 1. Websockets
The Websockets are a core part of our application since they allow users to chat to each other in real time. The Websockets connection is handled by the [WebSocketConfig](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/configurations/WebSocketConfig.java) which provides an endpoint to establish a direct connection between client and server. It also specifies which client origins are allowed to connect to the server. The [WebSocketChatController](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/WebSocketChatController.java) provides an endpoint where messages can be sent to and where they will be redistributed to the correct users in the correct format. 

### 2. REST Chat Controller
The [RestChatController](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/RestChatController.java) is a second core component for our chat application. It is responsible for handling chat-related REST requests which are not handled by the [WebSockets](#1-websockets). It plays a crucial role for creating chats and restoring previously sent messages providing persistence of chat history when opening up a previously used chat.

### 3. User Service
The [UserService](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java) is a core component of our application which handles all user-related functionalities. It handles both profile management(e.g. [sign-up](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java#L226), [login](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java#L213) and [updating](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java#L126) a profile) and implements a friends system, forming the third pillar of the chatting service next to [Websockets](#1-websockets) and the [RestChatController](#2-rest-chat-controller).

### 4. Flashcard Service
The [FlashcardService](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/FlashcardService.java) is the component that handles flashcard-related functionality which was our secondary goal for this application. It allows user to [create](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/FlashcardService.java#L68) and train with flashcards to learn new languages.

### 5. Azure API
[AzureAPI](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/API/AzureAPI.java) is an [API provided by Microsoft](https://azure.microsoft.com/en-us/products/api-management) and is the external API used for this project. It is responsible for the [translation of chat messages](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/WebSocketChatController.java#L54) but also assists users when [creating flashcards](https://github.com/Leon0605/sopra-fs25-group-05-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/FlashcardService.java#L86) in an unknown language.


## Launch & Deployment
### Setup this Template with your IDE of choice
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

#### IntelliJ
If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license [here](https://www.jetbrains.com/community/education/#students).
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

#### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

### Commands

#### Building with gradlew
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew` , `./gradlew clean build`
-   Linux: `./gradlew` , `./gradlew clean build`
-   Windows: `./gradlew.bat`

> ***IMPORTANT***: This will run all tests before actually building the product. If at least one test fails the building using these commands will also fail

#### Building with gradlew (ignoring tests)
If you have at least one test that does fail you can still build the code using the command:

-   macOS: `./gradlew build -x test`
-   Linux: `./gradlew build -x test`
-   Windows: `./gradlew.bat -x test`

#### Running the Application
```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

#### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`
### Testing
#### Running all tests
You can run tests by either [building](#building-with-gradlew) the application or running the command:

`./gradlew test`

#### Running specific tests
If you don't want to run all tests at the same time and focus on a specific set of tests, you can use the command:

`./gradlew test --tests ` + "ch.uzh.ifi.hase.soprafs24" + <testfile_path>

>Example: `./gradlew test --tests ch.uzh.ifi.hase.soprafs24.service.UserServiceTest` will only run the tests in the file UserServiceTest

You can also run a single test using:

`./gradlew test --tests` + "ch.uzh.ifi.hase.soprafs24" + <testfile_path> + <test_name>

>Example: `./gradlew test --tests ch.uzh.ifi.hase.soprafs24.service.UserServiceTest.findByUserId_invalidUserId_throwsException` will only run the test findByUserId_invalidUserId_throwsException located in the file UserServiceTest 


#### API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

### Deployment
Commits to the [main branch](https://github.com/Leon0605/sopra-fs25-group-05-server/tree/main) will be tried to be deployed to google cloud as configured in the [github workflow files](https://github.com/Leon0605/sopra-fs25-group-05-server/tree/main/.github/workflows)
## Roadmap
Here are some features we envision for future contributions:

1. ***Support of new languages***:

    Currently our application does only support 4 languages. It would make sense to enable new languages to attract possible new users.

2. ***Restructuring of Message Database***:

   In our current code the content of a message is stored in the message object itself. This works fine for now but could cause potential issues as new languages are added as the memory space needed increases significantly. A possibility would be to store the content separately and only storing a reference to the content in the message object itself. This would not only decrease needed memory, as multiple messages could reference the same content reducing redundant information storage but also opening new doors for additional interesting features.

3. ***Machine Learning & AI***

    One of the interesting features mentioned above would be to utilize this new database to train an ML algorithm allowing for new features like User-Based Typo Detection or an AI Chatbot which utilizes the messages to simulate real conversations to enable personalized language learning

4. ***Security***

    When starting to use more sensitive data it would make sense to increase the security of the application with things like Cookies, 2FA, etc.

## Authors and acknowledgments
### Authors

- Christopher Robert Traill: Team Leader, Frontend Developer
- Nikola Pavlovic: Backend Developer
- Andy de Vantéry: Frontend Developer
- Leon Matteo Schwager: Backend Developer

### Acknowledgments
This project was developed as part of the [Software Engineering Lab FS25](https://hasel.dev/teachings/fs25-sopra/) at the University of Zurich.

Special thanks to our teaching assistant Ambros Eberhard for his continuous feedback and guidance throughout the project. 
## License

