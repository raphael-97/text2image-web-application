# Text2Image web application

Welcome to my learning project.
The primary goal of this project is to gain a understanding of the entire frontend and backend development process. 
I aim to create a web application capable of generating images using text2Image AI models.

For this purpose, I am utilizing the inference API provided by Hugging Face.
Moving forward, I plan to expand the functionality of the application to include additional features such as Text2Video.

The application allows administrators to add new models from Hugging Face by providing 
the inference URL. The created models will then be visible to the users of the application. 
Users can generate images and save them to their personal galleries.

Please note that this is my first project involving frontend development, and it may not have the best practices in frontend.
I know there are authentication libraries like NextAuth, but I implemented it 
myself to gain a better understanding of Spring Boot.

### Explore page

![Explore page](/showcase/explore_page.png)

### Gallery page

![Gallery page](/showcase/gallery_page.png)

## Tech Stack

### Frameworks

- **Spring Boot** (Authorization and resource server)
- **React/Next.js** (Next.js full stack framework)
- **Tailwind CSS** (Utility-first CSS framework)
- **Docker** (Containerization technology)
- **NextUI** (UI library)

### Languages

- **Java**
- **Typescript**
- **JSX/TSX**
- **CSS**

### APIs

- **Huggingface** (Inference with AI models)

### Features

- Register/Login
- JWT Authentication
- Refresh tokens
- OAuth2 (Social Login)
- Text2Image generation with AI
- Personal gallery
- Store images on cloud 
- or locally on the resource server

## Getting started


1. Clone the project
```
git clone https://github.com/Raphael-97/Text2Image-Web-Application.git
```

2. Install Docker from [here](https://www.docker.com/products/docker-desktop/)

3. For social login with Google get your Google client id and client secret from here: 
[Google API console](https://console.developers.google.com/projectselector/apis/credentials?pli=1)

4. For AI inference get your Hugging Face API access token from here: 
[Hugging Face Token](https://huggingface.co/settings/tokens)

5. Go to 
[application.properties](https://github.com/Raphael-97/Text2Image-Web-Application/blob/main/backend/src/main/resources/application.properties)
and put your google client id and secret in here: 
```
# Google social login properties
spring.security.oauth2.client.registration.google.client-id=YOUR GOOGLE CLIENT ID HERE
spring.security.oauth2.client.registration.google.client-secret=YOUR GOOGLE CLIENT SECRET HERE
```
and your Hugging Face API access token here:
```
# Huggingface properties
app.huggingface.api.token=YOUR HUGGINGFACE API TOKEN HERE
```

6. Optionally you can decide if you want to store the generated images to Digital Ocean:
```
# Use local filesystem to save images or Cloud Space / Bucket
app.storage.local=true
app.storage.local.path=src/main/resources/static/

# Digital Ocean
app.storage.cloud.access-key=YOUR SPACE/BUCKET ACCESS KEY HERE
app.storage.cloud.secret-key=YOUR SPACE/BUCKET SECRET KEY HERE
app.storage.cloud.region=YOUR SPACE/BUCKET LOCATION HERE            eg. fra1
app.storage.cloud.endpoint=YOUR SPACE/BUCKET ENDPOINT HERE          eg. https://fra1.digitaloceanspaces.com
app.storage.cloud.bucketname=YOUR SPACE/BUCKET NAME
```

If `app.storage.local` is true, it will store the images locally in your Spring Boot app
and ignores the following properties, otherwise you store the images to your Digital Ocean Space
and the property keys need to be set. 

7. Now run in the root of this project:
```
# It can take a while, because the Nextjs app builds after the container has started.
docker compose up
```

8. In your browser, navigate to `http://localhost:3000` 


Have Fun :) 

If you have any questions, please feel free to ask by opening an issue in this repository.