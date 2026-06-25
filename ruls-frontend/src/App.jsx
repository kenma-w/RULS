import React, { useState } from 'react';
import Login from './components/Login.jsx';
import Register from './components/Register.jsx';
import Chat from './components/Chat.jsx';
import UserList from './components/UserList.jsx';
import RecentInteractions from './components/RecentInteractions.jsx';
import './App.css';

const App = () => {
    const [user, setUser] = useState(null);
    const [selectedUser, setSelectedUser] = useState(null);

    return (
        <div>
            {!user ? (
                <>
                    <Login setUser={setUser} />
                    <Register setUser={setUser} />
                </>
            ) : (
                <div>
                    <h1>Welcome, {user.name}!</h1> {/* Display logged-in username */}
                    <UserList setSelectedUser={setSelectedUser} />
                    <RecentInteractions userEmail={user.email} setSelectedUser={setSelectedUser} />
                    {selectedUser && <Chat user={user} selectedUser={selectedUser} />}
                </div>
            )}
        </div>
    );
};

export default App;
