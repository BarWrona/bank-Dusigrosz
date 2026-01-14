import axios from 'axios';
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
}

const BASE_URL = '/api/users';

export const fetchUsers = async (): Promise<User[]> => {
    const response = await axios.get<User[]>(BASE_URL);
    return response.data;
}

export const fetchUser = async (id: number): Promise<User> => {
    const response = await axios.get<User>(`${BASE_URL}/${id}`);
    return response.data;
}

export const deleteUser = async (id: number): Promise<void> => {
    await axios.delete(`${BASE_URL}/${id}`);
};

export const createUser = async (user: Omit<User, 'id'>): Promise<User> => {
    const response = await axios.post(BASE_URL, user);
    return response.data;
}

export const updateUser = async (id: number, user: User): Promise<User> => {
    const response = await axios.put(`${BASE_URL}/${id}`, user);
    return response.data;
};
