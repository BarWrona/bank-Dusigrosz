import api from './axios';

export interface Account {
    iban: string;
    currencyId: number;
    currencyCode: string;
    balance: number;
    userIds: number[];
}

export interface CreateAccountDto {
    currencyId: number;
    userIds: number[];
    balance: number;
    iban: string;
}

const BASE_URL = '/accounts';

export const fetchAccounts = async (): Promise<Account[]> => {
    const response = await api.get<Account[]>(BASE_URL);
    return response.data;
};

export const fetchMyAccounts = async (): Promise<Account[]> => {
    const response = await api.get<Account[]>(`${BASE_URL}/my`);
    return response.data;
};

export const fetchAccount = async (id: number): Promise<Account> => {
    const response = await api.get<Account>(`${BASE_URL}/${id}`);
    return response.data;
};

export const createAccount = async (account: CreateAccountDto): Promise<Account> => {
    const response = await api.post(BASE_URL, account);
    return response.data;
};

export const deleteAccount = async (id: number): Promise<void> => {
    await api.delete(`${BASE_URL}/${id}`);
};
