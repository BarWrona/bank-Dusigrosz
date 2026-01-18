import { type Component, createSignal, For, onMount, Show } from 'solid-js';
import { useNavigate } from '@solidjs/router';
import styles from '../assets/AccountForm.module.css';
import { fetchUsers, type User } from '../api/userApi';
import { fetchCurrencies, type Currency } from '../api/currencyApi';
import { createAccount } from '../api/accountApi';
import { authStore } from '../store/auth';

const AccountFormPage: Component = () => {
    const navigate = useNavigate();

    const [users, setUsers] = createSignal<User[]>([]);
    const [currencies, setCurrencies] = createSignal<Currency[]>([]);
    const [loading, setLoading] = createSignal(false);
    const [error, setError] = createSignal<string | null>(null);

    const [selectedUserId, setSelectedUserId] = createSignal<string>("");
    const [selectedCurrencyId, setSelectedCurrencyId] = createSignal<string>("");

    onMount(async () => {
        try {
            const user = authStore.user();
            const promises: Promise<any>[] = [fetchCurrencies()];

            if (user?.role !== 'USER') {
                promises.push(fetchUsers());
            } else if (user.id) {
                setSelectedUserId(user.id.toString());
            }

            const results = await Promise.all(promises);
            setCurrencies(results[0]);
            if (results[1]) {
                setUsers(results[1]);
            }
        } catch (err) {
            console.error(err);
            setError("Błąd ładowania danych.");
        }
    });

    const handleSubmit = async (e: Event) => {
        e.preventDefault();
        setError(null);

        if (!selectedUserId() || !selectedCurrencyId()) {
            setError("Wybierz użytkownika i walutę.");
            return;
        }

        setLoading(true);
        try {
            await createAccount({
                userIds: [Number(selectedUserId())],
                currencyId: Number(selectedCurrencyId()),
                balance: 0,
                iban: ""
            });
            navigate('/transfers');
        } catch (err: any) {
            console.error(err);
            setError(err.response?.data?.message || "Nie udało się utworzyć konta.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <header class={styles.header}>
                    <h1 class={styles.title}>Utwórz Nowe Konto</h1>
                </header>

                <form class={styles.form} onSubmit={handleSubmit}>

                    <Show when={authStore.user()?.role !== 'USER'}>
                        <div class={styles.formGroup}>
                            <select
                                class={styles.select}
                                id="user"
                                value={selectedUserId()}
                                onChange={(e) => setSelectedUserId(e.currentTarget.value)}
                                required
                            >
                                <option value="" disabled selected></option>
                                <For each={users()}>
                                    {(user) => (
                                        <option value={user.id}>{user.firstName} {user.lastName} ({user.pesel})</option>
                                    )}
                                </For>
                            </select>
                            <label class={styles.label} for="user">Użytkownik</label>
                        </div>
                    </Show>

                    <div class={styles.formGroup}>
                        <select
                            class={styles.select}
                            id="currency"
                            value={selectedCurrencyId()}
                            onChange={(e) => setSelectedCurrencyId(e.currentTarget.value)}
                            required
                        >
                            <option value="" disabled selected></option>
                            <For each={currencies()}>
                                {(currency) => (
                                    <option value={currency.id}>{currency.code} - {currency.name}</option>
                                )}
                            </For>
                        </select>
                        <label class={styles.label} for="currency">Waluta</label>
                    </div>

                    <Show when={error()}>
                        <div class={styles.globalError}>{error()}</div>
                    </Show>

                    <div class={styles.actions}>
                        <button type="button" class={`${styles.button} ${styles.cancelButton}`} onClick={() => navigate('/transfers')}>
                            Anuluj
                        </button>
                        <button type="submit" class={`${styles.button} ${styles.saveButton}`} disabled={loading()}>
                            {loading() ? 'Tworzenie...' : 'Utwórz Konto'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AccountFormPage;
