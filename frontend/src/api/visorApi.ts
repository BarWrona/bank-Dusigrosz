import axios from 'axios';

export interface Visor {
    id: number;
    firstName: string;
    lastName: string;
    pesel: string;
    phoneNumber: string;
}

const BASE_URL = '/api/visors';

export const fetchVisors = async (): Promise<Visor[]> => {
    const response = await axios.get<Visor[]>(BASE_URL);
    return response.data;
}

export const fetchVisor = async (id: number): Promise<Visor> => {
    const response = await axios.get<Visor>(`${BASE_URL}/${id}`);
    return response.data;
}

export const createVisor = async (visor: Visor): Promise<Visor> => {
    const response = await axios.post(BASE_URL, visor);
    return response.data;
}

export const updateVisor = async (id: number, visor: Visor): Promise<Visor> => {
    const response = await axios.put(`${BASE_URL}/${id}`, visor);
    return response.data;
};

export const deleteVisor = async (id: number): Promise<void> => {
    await axios.delete(`${BASE_URL}/${id}`);
};