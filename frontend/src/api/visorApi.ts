import api from './axios';

export interface Visor {
    id: number;
    firstName: string;
    lastName: string;
    pesel: string;
    phoneNumber: string;
    username: string;
    password?: string;
}

const BASE_URL = '/visors';

export const fetchVisors = async (): Promise<Visor[]> => {
    const response = await api.get<Visor[]>(BASE_URL);
    return response.data;
}

export const fetchVisor = async (id: number): Promise<Visor> => {
    const response = await api.get<Visor>(`${BASE_URL}/${id}`);
    return response.data;
}

export const createVisor = async (visor: Visor): Promise<Visor> => {
    const response = await api.post(BASE_URL, visor);
    return response.data;
}

export const updateVisor = async (id: number, visor: Visor): Promise<Visor> => {
    const response = await api.put(`${BASE_URL}/${id}`, visor);
    return response.data;
};

export const deleteVisor = async (id: number): Promise<void> => {
    await api.delete(`${BASE_URL}/${id}`);
};