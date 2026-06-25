# RULS
the texting app scalable to 10 million users 


RULS: Project Description and Overview 

RULS is a comprehensive chat application designed to provide real-time communication capabilities. It leverages a combination of modern technologies and tools to ensure efficient performance, scalability, and a smooth user experience.

How RULS Works

RULS integrates various components to deliver seamless real-time communication. Here's a high-level overview of the workflow:

Frontend:

Built using React.js (Node.js runtime) and serves as the user interface.

Establishes WebSocket connections with the backend for real-time communication.

Sends user input (like messages) to the backend and displays received messages.

Backend:

Developed using Spring Boot (Java).

Handles WebSocket connections, processes user messages, and interacts with other services like Redis, Kafka, and MongoDB.

Provides APIs for the frontend to interact with (e.g., authentication, chat operations).

Database:

Uses MongoDB to store user data, chat messages, and other relevant information.

Ensures data persistence and reliability.

Cache:

Implements Redis for caching frequently accessed data, improving performance by reducing database load.

Message Queue:

Utilizes Kafka for handling asynchronous message processing and real-time message delivery.

WebSocket Communication:

Enables real-time bidirectional communication between the frontend and backend, ensuring messages are delivered instantly.



Detailed Workflow
User Interaction:

User A sends a message via the frontend UI.

The frontend establishes a WebSocket connection with the backend server.

Backend Processing:

The backend receives the message, processes it, and checks the user's status using Redis.

The message is stored in MongoDB for persistence.

Real-Time Delivery:

Kafka handles the real-time delivery of the message.

The message is delivered to User B when they come online or if they are already online via WebSocket.

Asynchronous Processing:

Kafka ensures asynchronous processing, making the system scalable and efficient.


Technologies Used

Frontend: React.js(Node.js runtime)

Backend: Spring Boot (Java)

Database: MongoDB

Cache: Redis

Message Queue: Kafka

WebSocket Communication: For real-time bidirectional communication


Conclusion

RULS is a robust chat application that leverages modern technologies to provide real-time communication, efficient message delivery, and scalable architecture. The integration of Docker ensures consistency across environments, making it easy to deploy and maintain.
