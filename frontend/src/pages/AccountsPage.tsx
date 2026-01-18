import { type Component, createSignal, onMount, For, Show } from 'solid-js';
import styles from '../assets/ControlPanel.module.css';
import { fetchAccounts, type Account } from '../api/accountApi';
import { fetchCurrencies, type Currency } from '../api/currencyApi';

const AccountsPage: Component = () => {
    const [accounts, setAccounts] = createSignal<Account[]>([]);
    const [currencies, setCurrencies] = createSignal<Currency[]>([]);
    const [loading, setLoading] = createSignal(true);
    const [error, setError] = createSignal<string | null>(null);

    onMount(async () => {
        try {
            const [accs, currs] = await Promise.all([fetchAccounts(), fetchCurrencies()]);
            setAccounts(accs);
            setCurrencies(currs);
        } catch (err) {
            console.error(err);
            setError("Nie udało się pobrać listy kont.");
        } finally {
            setLoading(false);
        }
    });

    const getCurrencyCode = (id: number) => currencies().find(c => c.id === id)?.code || id;

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <h1 class={styles.title}>Lista Kont Bankowych</h1>

                <Show when={error()}>
                    <div class={styles.errorMessage}>{error()}</div>
                </Show>

                <Show when={!loading()} fallback={<div>Ładowanie...</div>}>
                    <table class={styles.tableWrapper}>
                        <thead class={styles.tableHeader}>
                            <tr>

                                <th class={styles.tableCell}>IBAN</th>
                                <th class={styles.tableCell}>Waluta</th>
                                <th class={styles.tableCell}>Saldo</th>
                                <th class={styles.tableCell}>Właściciele (IDs)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <For each={accounts()}>
                                {(acc) => (
                                    <tr>

                                        <td class={styles.tableCell}>{acc.iban}</td>
                                        <td class={styles.tableCell}>{getCurrencyCode(acc.currencyId)}</td>
                                        <td class={styles.tableCell}>{acc.balance}</td>
                                        <td class={styles.tableCell}>{acc.userIds?.join(', ')}</td>
                                    </tr>
                                )}
                            </For>
                        </tbody>
                    </table>
                    <Show when={accounts().length === 0}>
                        <p class={styles.emptyMessage}>Brak kont do wyświetlenia.</p>
                    </Show>
                </Show>
            </div>
        </div>
    );
};

export default AccountsPage;
