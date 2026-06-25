// src/components/Message.js
import React from 'react';

const Message = ({ user, content }) => {
    return (
        <div>
            <strong>{user}:</strong> {content}
        </div>
    );
};

export default Message;
