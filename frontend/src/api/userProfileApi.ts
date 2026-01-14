import axios from 'axios';

export interface UserProfile {
    userId: number;
    twoFactorEnabled: boolean;
    additionalDataCollecting: boolean;
}

const BASE_URL = '/api/user-profiles';

export const fetchUserProfile = async (userId: number): Promise<UserProfile> => {
    const response = await axios.get<UserProfile>(`${BASE_URL}/${userId}`);
    return response.data;
}

export const updateUserProfile = async (userId: number, profile: UserProfile): Promise<UserProfile> => {
    const response = await axios.put<UserProfile>(`${BASE_URL}/${userId}`, profile);
    return response.data;
}
