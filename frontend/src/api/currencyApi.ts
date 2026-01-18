import api from './axios';

export interface Currency {
    id: number;
    code: string;
    name: string;
    exchangeRate: number;
}

const BASE_URL = '/currencies';

export const fetchCurrencies = async (): Promise<Currency[]> => {
    const response = await api.get<Currency[]>(BASE_URL);
    return response.data;
};

export const fetchCurrency = async (code: string): Promise<Currency> => {
    const response = await api.get<Currency>(`${BASE_URL}/${code}`);
    return response.data;
};

export const updateExchangeRates = async (): Promise<void> => {
    await api.post(`${BASE_URL}/update-rates`);
};
