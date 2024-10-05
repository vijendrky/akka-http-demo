Steps: Test the API
Now you can test the JWT-based authentication with your Akka-HTTP API.

1. Login to Get a JWT Token
Make a POST request to /api/login with a username:

bash
Copy code
curl -X POST http://localhost:8080/api/login -d 'user1'
Response:

php
Copy code
Your JWT token: <JWT_TOKEN>
2. Access a Protected Route
Use the JWT token to access a secure route, by including the token in the Authorization header:

bash
Copy code
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/api/secure
Response:

kotlin
Copy code
Hello, user1! You have access to this secure route.
If the JWT token is missing or invalid, the API will return a 401 Unauthorized error.
