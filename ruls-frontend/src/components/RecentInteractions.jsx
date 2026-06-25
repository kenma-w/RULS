import React, { useEffect, useState } from 'react';
import axios from 'axios';

const RecentInteractions = ({ userEmail, setSelectedUser }) => {
    const [recent, setRecent] = useState([]);

    useEffect(() => {
        const fetchRecentInteractions = async () => {
            try {
                const response = await axios.post('http://localhost:8081/getRecent', { userEmail });
                setRecent(response.data);
            } catch (error) {
                console.error('Failed to fetch recent interactions', error);
            }
        };
        fetchRecentInteractions();
    }, [userEmail]);

    return (
        <div>
            <h2>Recent Interactions</h2>
            <ul>
                {recent.map((interaction) => (
                    <li key={interaction.userEmail} onClick={() => setSelectedUser(interaction)}>
                        {interaction.message}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default RecentInteractions;
