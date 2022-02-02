import React, { useState } from 'react';

const useAuth = () => {
    const [user, setUser] = useState({
        name: "",
        id: "",
    });


    return user
}

export default useAuth;
