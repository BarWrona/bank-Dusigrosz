import api from './axios';

export interface LoginRequest {
    username: string;
    password: string;
}

export interface JwtResponse {
    token: string;
    type: string;
    id: number;
    username: string;
    role: string;
}

export const login = async (credentials: LoginRequest): Promise<JwtResponse> => {
    const response = await api.post<JwtResponse>('/auth/login', credentials);
    return response.data;
};
