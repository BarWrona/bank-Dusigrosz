import { type Component, createSignal, onMount, For, Show } from 'solid-js';
import styles from '../assets/ControlPanel.module.css';
import { fetchTransfers, type TransferDto } from '../api/transferApi';

const TransfersListPage: Component = () => {
    const [transfers, setTransfers] = createSignal<TransferDto[]>([]);
    const [loading, setLoading] = createSignal(true);
    const [error, setError] = createSignal<string | null>(null);

    onMount(async () => {
        try {
            const data = await fetchTransfers();
            setTransfers(data);
        } catch (err) {
            console.error(err);
            setError("Nie udało się pobrać listy przelewów.");
        } finally {
            setLoading(false);
        }
    });

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <h1 class={styles.title}>Historia Wszystkich Przelewów</h1>

                <Show when={error()}>
                    <div class={styles.errorMessage}>{error()}</div>
                </Show>

                <Show when={!loading()} fallback={<div>Ładowanie...</div>}>
                    <table class={styles.tableWrapper}>
                        <thead class={styles.tableHeader}>
                            <tr>
                                <th class={styles.tableCell}>Data</th>
                                <th class={styles.tableCell}>Tytuł</th>
                                <th class={styles.tableCell}>Nadawca</th>
                                <th class={styles.tableCell}>Odbiorca</th>
                                <th class={styles.tableCell}>Kwota (Wysłana)</th>
                                <th class={styles.tableCell}>Kwota (Otrzymana)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <For each={transfers()}>
                                {(t) => (
                                    <tr>
                                        <td class={styles.tableCell}>{t.createdAt ? new Date(t.createdAt as string).toLocaleString() : '-'}</td>
                                        <td class={styles.tableCell}>{t.title}</td>
                                        <td class={styles.tableCell}>{t.senderIban}</td>
                                        <td class={styles.tableCell}>{t.receiverIban}</td>
                                        <td class={styles.tableCell}>{t.amountSent}</td>
                                        <td class={styles.tableCell}>{t.amountReceived}</td>
                                    </tr>
                                )}
                            </For>
                        </tbody>
                    </table>
                    <Show when={transfers().length === 0}>
                        <p class={styles.emptyMessage}>Brak przelewów do wyświetlenia.</p>
                    </Show>
                </Show>
            </div>
        </div>
    );
};

export default TransfersListPage;
