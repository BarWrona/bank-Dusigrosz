import { createResource, For, Show } from "solid-js";
import { fetchMyAccounts } from "../api/accountApi";
import { fetchMyTransfers } from "../api/transferApi";
import { A } from "@solidjs/router";
import { authStore } from "../store/auth";
import styles from "../assets/UserDashboard.module.css";

const UserDashboard = () => {
    const [accounts] = createResource(fetchMyAccounts);
    const [transfers] = createResource(fetchMyTransfers);

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <header class={styles.header}>
                    <h1 class={styles.title}>Witaj, {authStore.user()?.username}</h1>
                    <div class={styles.actions}>
                        <A href="/transfers" class={styles.btnPrimary}>Nowy Przelew</A>
                        <button onClick={() => authStore.logout()} class={styles.btnSecondary}>Wyloguj</button>
                    </div>
                </header>

                <section class={styles.section}>
                    <div class={styles.sectionTitle}>
                        <span>💳</span> Moje Konta
                    </div>

                    <Show when={!accounts.loading} fallback={<div class={styles.emptyState}>Ładowanie kont...</div>}>
                        <div class={styles.grid}>
                            <For each={accounts()}>
                                {(account) => (
                                    <div class={styles.card}>
                                        <div class={styles.cardHeader}>
                                            <div class={styles.cardIcon}>🏦</div>
                                            <div class={styles.cardLabel}>{account.currencyCode}</div>
                                        </div>
                                        <div class={styles.cardLabel}>Numer Konta</div>
                                        <div style={{ "font-family": "monospace", "margin-bottom": "1rem" }}>{account.iban}</div>

                                        <div class={styles.cardLabel}>Dostępne Środki</div>
                                        <div class={styles.cardValue}>{account.balance.toFixed(2)} {account.currencyCode}</div>
                                    </div>
                                )}
                            </For>
                        </div>
                        <Show when={accounts()?.length === 0}>
                            <div class={styles.emptyState}>Nie masz jeszcze żadnych kont.</div>
                        </Show>
                    </Show>
                </section>

                <section class={styles.section}>
                    <div class={styles.sectionTitle}>
                        <span>📊</span> Ostatnie Transakcje
                    </div>

                    <div class={styles.tableWrapper}>
                        <table class={styles.table}>
                            <thead>
                                <tr>
                                    <th>Tytuł</th>
                                    <th>Wysłano</th>
                                    <th>Otrzymano</th>
                                    <th>Data</th>
                                </tr>
                            </thead>
                            <tbody>
                                <For each={transfers()}>
                                    {(transfer) => (
                                        <tr>
                                            <td>{transfer.title}</td>
                                            <td class={styles.amountNegative}>
                                                {(transfer.amountSent ?? 0) > 0 ? `-${(transfer.amountSent ?? 0).toFixed(2)}` : '-'}
                                            </td>
                                            <td class={styles.amountPositive}>
                                                {(transfer.amountReceived ?? 0) > 0 ? `+${(transfer.amountReceived ?? 0).toFixed(2)}` : '-'}
                                            </td>
                                            <td>{new Date(transfer.createdAt || "").toLocaleDateString()}</td>
                                        </tr>
                                    )}
                                </For>
                                <Show when={transfers()?.length === 0 && !transfers.loading}>
                                    <tr>
                                        <td colspan="4" class={styles.emptyState}>Brak historii transakcji.</td>
                                    </tr>
                                </Show>
                            </tbody>
                        </table>
                        <Show when={transfers.loading}>
                            <div class={styles.emptyState}>Ładowanie historii...</div>
                        </Show>
                    </div>
                </section>
            </div>
        </div>
    );
};

export default UserDashboard;
