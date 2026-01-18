import api from './axios';

export interface TransferDto {
    title: string;
    // Add other fields if needed for display
    senderIban?: string;
    receiverIban?: string;
    amountSent?: number;
    amountReceived?: number;
    createdAt?: string;
}

export interface TransferRequest {
    senderIban: string;
    receiverIban: string;
    amountSent: number;
    title: string;
}

const BASE_URL = '/transfers';

export const executeTransfer = async (request: TransferRequest): Promise<string> => {
    const response = await api.post(BASE_URL, request);
    return response.data;
};

export const fetchTransfers = async (): Promise<TransferDto[]> => {
    const response = await api.get<TransferDto[]>(BASE_URL);
    return response.data;
};

export const fetchMyTransfers = async (): Promise<TransferDto[]> => {
    const response = await api.get<TransferDto[]>(`${BASE_URL}/my`);
    return response.data;
};
