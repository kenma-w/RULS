import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';

const Chat = ({ user, selectedUser }) => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const stompClientRef = useRef(null);
    const [connected, setConnected] = useState(false);

    useEffect(() => {
        if (!selectedUser) return;

        // Clear messages when switching users
        setMessages([]);

        // Fetch previous messages between the user and selectedUser
        const fetchMessages = async () => {
            try {
                const response = await axios.post(`http://localhost:8081/getMsg`, {
                    senderId: user.email,
                    receiverId: selectedUser.email
                });
                console.log('Fetched messages:', response.data);
                setMessages(response.data);
            } catch (error) {
                console.error('Failed to fetch messages', error);
            }
        };

        fetchMessages();

        // Initialize WebSocket connection
        const socket = new SockJS(`http://localhost:8081/ws`);
        const stompClient = new Client({
            webSocketFactory: () => socket,
            debug: (str) => console.log(str),
            reconnectDelay: 5000,
        });

        stompClient.onConnect = () => {
            console.log('Connected');
            setConnected(true);
            stompClient.subscribe(`/user/${user.email}/queue/reply`, (msg) => {
                console.log('Received message from WebSocket:', msg.body);
                const receivedMessage = JSON.parse(msg.body);
                if (receivedMessage.senderId === selectedUser.email) {
                    setMessages((prevMessages) => [...prevMessages, receivedMessage]);
                }
            });
        };

        stompClient.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

        stompClient.onWebSocketClose = () => {
            console.log('WebSocket connection closed');
            setConnected(false);
        };

        stompClient.activate();
        stompClientRef.current = stompClient;

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
            }
        };
    }, [user.email, selectedUser]);

    const sendMessage = (e) => {
        e.preventDefault();
        if (!stompClientRef.current || !connected) {
            console.error('WebSocket connection is not established.');
            return;
        }

        const chatMessage = { senderId: user.email, receiverId: selectedUser.email, content: message };
        console.log('Sending message:', chatMessage);
        stompClientRef.current.publish({
            destination: '/app/chat.sendMessage',
            body: JSON.stringify(chatMessage),
        });
        setMessage('');
    };

    return (
        <div>
            <h2>Chat with {selectedUser.name}</h2>
            <div>
                {messages.map((msg, index) => (
                    <div key={index}>
                        <strong>{msg.senderId === user.email ? 'You' : selectedUser.name}:</strong> {msg.content}
                    </div>
                ))}
            </div>
            <form onSubmit={sendMessage}>
                <input
                    type="text"
                    placeholder="Message"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    disabled={!connected}
                />
                <button type="submit" disabled={!connected}>Send</button>
                {!connected && <p>Connecting...</p>}
            </form>
        </div>
    );
};

export default Chat;
