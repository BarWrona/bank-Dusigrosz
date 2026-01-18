import api from './axios';
import { type UserProfile } from './userProfileApi';

export interface User {
    id: number;
    firstName: string;
    lastName: string;
    pesel: string;
    phoneNumber: string;
    username: string;
    supervisorName?: string;
    supervisorPhoneNumber?: string;
    profile?: UserProfile;
    password?: string;
}

const BASE_URL = '/users';

export const fetchUsers = async (): Promise<User[]> => {
    const response = await api.get<User[]>(BASE_URL);
    return response.data;
}

export const fetchUser = async (id: number): Promise<User> => {
    const response = await api.get<User>(`${BASE_URL}/${id}`);
    return response.data;
}

export const deleteUser = async (id: number): Promise<void> => {
    await api.delete(`${BASE_URL}/${id}`);
};

export const createUser = async (user: Omit<User, 'id'>): Promise<User> => {
    const response = await api.post(BASE_URL, user);
    return response.data;
}

export const registerUser = async (user: Omit<User, 'id'>): Promise<User> => {
    const response = await api.post('/auth/register', user);
    return response.data;
}

export const updateUser = async (id: number, user: User): Promise<User> => {
    const response = await api.put(`${BASE_URL}/${id}`, user);
    return response.data;
};
