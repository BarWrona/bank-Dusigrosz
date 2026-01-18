import { type Component, createSignal, For, onMount, Show } from 'solid-js';
import { useNavigate } from '@solidjs/router';
import styles from '../assets/TransferPage.module.css';
import { fetchCurrencies, updateExchangeRates, type Currency } from '../api/currencyApi';
import { fetchUsers, type User } from '../api/userApi';
import { fetchAccounts, type Account } from '../api/accountApi';
import { executeTransfer } from '../api/transferApi';
import { authStore } from '../store/auth';
import { fetchMyAccounts } from '../api/accountApi';

const TransferPage: Component = () => {
    const navigate = useNavigate();

    const [currencies, setCurrencies] = createSignal<Currency[]>([]);
    const [lastUpdated, setLastUpdated] = createSignal<string>('');
    const [users, setUsers] = createSignal<User[]>([]);
    const [allAccounts, setAllAccounts] = createSignal<Account[]>([]);


    const [selectedUserId, setSelectedUserId] = createSignal<string>("");
    const [senderAccountIban, setSenderAccountIban] = createSignal<string>("");
    const [receiverAccountIban, setReceiverAccountIban] = createSignal<string>("");
    const [amount, setAmount] = createSignal<string>("");
    const [title, setTitle] = createSignal<string>("");

    const [message, setMessage] = createSignal<{ text: string, type: 'success' | 'error' } | null>(null);


    onMount(async () => {
        try {
            await updateExchangeRates();
            await loadCurrencies();
            if (authStore.user()?.role === 'USER') {
                const currentUser = authStore.user();
                if (currentUser) {
                    setSelectedUserId(String(currentUser.id));
                }
            } else {
                const usersData = await fetchUsers();
                setUsers(usersData);
            }
            await loadAccounts();

            setLastUpdated(new Date().toLocaleString());
        } catch (err) {
            console.error("Failed to load initial data", err);
        }
    });

    const loadCurrencies = async () => {
        const data = await fetchCurrencies();
        setCurrencies(data);
    };

    const loadAccounts = async () => {
        if (authStore.user()?.role === 'USER') {
            const data = await fetchMyAccounts();
            setAllAccounts(data);
            return;
        }
        try {
            const data = await fetchAccounts();
            setAllAccounts(data);
        } catch (e) {
            console.error("Failed to load accounts", e);
        }
    }

    const userAccounts = () => {
        const uid = Number(selectedUserId());
        if (!uid) return [];
        if (authStore.user()?.role === 'USER') {
            return allAccounts();
        }
        return allAccounts().filter(acc => acc.userIds.includes(uid));
    };

    const otherAccounts = () => {
        return allAccounts().filter(acc => acc.iban !== senderAccountIban());
    };

    const getCurrencyCode = (currencyId: number) => {
        return currencies().find(c => c.id === currencyId)?.code || '?';
    };

    const handleTransfer = async (e: Event) => {
        e.preventDefault();
        setMessage(null);

        const sender = allAccounts().find(a => a.iban === senderAccountIban());
        const receiver = allAccounts().find(a => a.iban === receiverAccountIban());

        if (!sender || !receiver || !amount()) {
            setMessage({ text: "Wybierz konta i wpisz kwotę", type: 'error' });
            return;
        }

        try {
            await executeTransfer({
                senderIban: sender.iban,
                receiverIban: receiver.iban,
                amountSent: parseFloat(amount()),
                title: title()
            });
            setMessage({ text: "Przelew wysłany pomyślnie!", type: 'success' });
            await loadAccounts();
            setAmount("");
            setTitle("");
        } catch (err: any) {
            console.error(err);
            const errorData = err.response?.data;
            const errorMessage = errorData?.message || (typeof errorData === 'string' ? errorData : "Błąd transferu");
            setMessage({ text: errorMessage, type: 'error' });
        }
    };

    return (
        <div class={styles.pageContainer}>
            <aside class={styles.sidebar}>
                <div class={styles.sidebarTitle}>Kursy Walut</div>
                <div class={styles.currencyTableWrapper}>
                    <table class={styles.currencyTable}>
                        <thead>
                            <tr>
                                <th>Kod</th>
                                <th>Kurs</th>
                            </tr>
                        </thead>
                        <tbody>
                            <For each={currencies()}>
                                {(currency) => (
                                    <tr>
                                        <td>{currency.code}</td>
                                        <td>{currency.exchangeRate.toFixed(4)}</td>
                                    </tr>
                                )}
                            </For>
                        </tbody>
                    </table>
                </div>
                <div class={styles.lastUpdated}>
                    Ostatnia aktualizacja:<br />
                    {lastUpdated()}
                </div>
            </aside>

            <main class={styles.mainContent}>
                <div class={styles.panel}>
                    <div class={styles.panelHeader}>
                        <h1 class={styles.panelTitle}>Wykonaj Przelew</h1>
                    </div>

                    <Show when={message()}>
                        <div class={message()?.type === 'success' ? styles.successMessage : styles.errorMessage}>
                            {message()?.text}
                        </div>
                    </Show>

                    <form onSubmit={handleTransfer}>
                        <div class={styles.formSection}>
                            <h3 class={styles.sectionTitle}>1. Wybierz Nadawcę</h3>
                            <div class={styles.formGroup}>
                                <Show when={authStore.user()?.role !== 'USER'} fallback={<>
                                    <input type="text" value={authStore.user()?.username} disabled class={styles.input} placeholder=" " />
                                    <label class={styles.label}>Użytkownik</label>
                                </>}>
                                    <select
                                        class={`${styles.select} ${selectedUserId() ? styles.hasValue : ''}`}
                                        value={selectedUserId()}
                                        onChange={(e) => {
                                            setSelectedUserId(e.currentTarget.value);
                                            setSenderAccountIban("");
                                        }}
                                    >
                                        <option value=""></option>
                                        <For each={users()}>
                                            {(user) => (
                                                <option value={user.id}>{user.firstName} {user.lastName} ({user.username})</option>
                                            )}
                                        </For>
                                    </select>
                                    <label class={styles.label}>Użytkownik</label>
                                </Show>
                            </div>
                        </div>


                        <Show when={selectedUserId()}>
                            <div class={styles.formSection}>
                                <div class={styles.panelHeaderSmall}>
                                    <h3 class={styles.sectionTitleNoMargin}>2. Konto Źródłowe</h3>
                                    <button
                                        type="button"
                                        class={styles.createAccountButton}
                                        onClick={() => navigate('/accounts/new')}
                                    >
                                        + Nowe Konto
                                    </button>
                                </div>

                                <div class={styles.accountList}>
                                    <For each={userAccounts()} fallback={<div class={styles.noAccountsMsg}>Brak kont dla tego użytkownika.</div>}>
                                        {(account) => (
                                            <div
                                                class={`${styles.accountOption} ${senderAccountIban() === account.iban ? styles.selected : ''}`}
                                                onClick={() => setSenderAccountIban(account.iban)}
                                            >
                                                <div class={styles.accountDetails}>
                                                    <span class={styles.accountIban}>{account.iban || 'Brak IBAN'}</span>
                                                    <span class={styles.accountBalance}>Dostępne: {account.balance} {getCurrencyCode(account.currencyId)}</span>
                                                </div>
                                                <Show when={senderAccountIban() === account.iban}>
                                                    <span>✔️</span>
                                                </Show>
                                            </div>
                                        )}
                                    </For>
                                </div>
                            </div>
                        </Show>

                        <Show when={senderAccountIban()}>
                            <div class={styles.formSection}>
                                <h3 class={styles.sectionTitle}>3. Konto Docelowe i Kwota</h3>

                                <div class={styles.formGroup}>
                                    <div class={styles.inputGroupVertical}>
                                        <div class={styles.inputWrapper}>
                                            <input
                                                type="text"
                                                class={styles.input}
                                                value={receiverAccountIban()}
                                                onInput={(e) => setReceiverAccountIban(e.currentTarget.value)}
                                                placeholder=" "
                                                list="accounts-list"
                                                required
                                                id="receiverIban"
                                            />
                                            <label class={styles.label} for="receiverIban">Konto Odbiorcy (IBAN)</label>
                                        </div>
                                        <datalist id="accounts-list">
                                            <For each={otherAccounts()}>
                                                {(account) => (
                                                    <option value={account.iban}>
                                                        {account.iban} ({account.balance} {getCurrencyCode(account.currencyId)})
                                                    </option>
                                                )}
                                            </For>
                                        </datalist>
                                        {authStore.user()?.role === 'USER' && (
                                            <small class={styles.helpText}>Wpisz IBAN lub wybierz jedno ze swoich innych kont.</small>
                                        )}
                                    </div>
                                </div>

                                <div class={styles.formGroup}>
                                    <input
                                        type="number"
                                        class={styles.input}
                                        value={amount()}
                                        onInput={(e) => setAmount(e.currentTarget.value)}
                                        min="0.01"
                                        step="0.01"
                                        required
                                        placeholder=" "
                                        id="amount"
                                    />
                                    <label class={styles.label} for="amount">Kwota</label>
                                </div>

                                <div class={styles.formGroup}>
                                    <input
                                        type="text"
                                        class={styles.input}
                                        value={title()}
                                        onInput={(e) => setTitle(e.currentTarget.value)}
                                        required
                                        placeholder=" "
                                        id="title"
                                    />
                                    <label class={styles.label} for="title">Tytuł przelewu</label>
                                </div>

                                <button type="submit" class={styles.transferButton}>
                                    Wyślij Przelew
                                </button>
                            </div>
                        </Show>

                    </form>
                </div>
            </main >
        </div >
    );
};

export default TransferPage;
