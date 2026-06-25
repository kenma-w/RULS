import React, { useEffect, useState } from 'react';
import axios from 'axios';

const UserList = ({ setSelectedUser }) => {
    const [users, setUsers] = useState([]);
    const [search, setSearch] = useState('');

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await axios.post('http://localhost:8081/searchUsers', { data: search }); // Change to 8081 or 8082
                setUsers(response.data);
            } catch (error) {
                console.error('Failed to fetch users', error);
            }
        };
        fetchUsers();
    }, [search]);

    return (
        <div>
            <input
                type="text"
                placeholder="Search users"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
            />
            <ul>
                {users.map((user) => (
                    <li key={user.email} onClick={() => setSelectedUser(user)}>
                        {user.name}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default UserList;
