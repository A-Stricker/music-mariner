# Music Mariner
Music Mariner is an application designed to take conversational input, create a Spotify playlist, and add a custom selection of tracks based on the user's input. 
It interfaces with Spotify's API so that users will instantly see their newly created playlists with the Music Mariner cover image in their Spotify account.
As the playlist is created, the user will see a webpage displaying a message from Music Mariner and the content of the playlist.

## Features
- Conversational input for creating a personalized playlist. (Current this feature is not integrated, at Music Mariner we hope to be incorporating and deploying this feature soon.
        In lei of this feature, there are several preloaded responses in the project as .txt files)
- Integration with Spotify's Web API for playlist and track management.
- Custom playlist cover image upload.
- OAuth2 for Spotify authentication.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Before you begin, ensure you have met the following requirements:
- Java 11 or newer
- Maven (for dependency management and running the application)
- A Spotify Developer account and a registered application to obtain API keys.

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/A-Stricker/music-mariner.git
2. Navigate to the project directory:
  sh
  Copy code
  cd music-mariner
3. Install dependencies:
  sh
  Copy code
  mvn install
4. Set up your application-secret.properties with your Spotify API keys:
  spotify.client.id=YOUR_SPOTIFY_CLIENT_ID
  spotify.client.secret=YOUR_SPOTIFY_CLIENT_SECRET
5. Start the application:
  sh
  Copy code
  mvn spring-boot:run
The service should now be running on http://localhost:8080.

### Usage
Until the conversational input feature is deployed, users can interact with the application using the preloaded .txt files that simulate the conversational output. 
To view or modify these files, navigate to src/main/resources.

### Contributing
For now, we are not accepting contributes to this project.

### Contact
- Amanda Stricker - amanda.stricker@yahoo.com
- Project Link: https://github.com/A-Stricker/music-mariner

### Acknowledgements
- Gratitude to the open-source community.
- Special thanks to Spotify for their Web API and openAI for the ability to create custom GPTs.
- Thanks to every musician who has inspired me in my life-long love of music and entertainment. 
- Finally, I would like to extend my heartfelt thanks to Professor John Crider at Columbus State Community College for fostering a class and environment that deeply encouraged creativity, 
critical thinking, and hands-on learning. His invaluable guidance and unwavering support have not only enriched my academic journey but also significantly contributed to the development of this project. 
His expertise and insights have been a constant source of inspiration, pushing me to explore new ideas and strive for excellence. Thank you, Professor Crider, for your mentorship, encouragement, 
and for believing in my potential. Your impact extends beyond the classroom, shaping the future paths of your students.
