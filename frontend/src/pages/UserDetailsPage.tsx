import { type Component, createResource, createSignal, Show } from 'solid-js';
import { useParams, A } from '@solidjs/router';
import { fetchUser } from '../api/userApi';
import { fetchUserProfile, updateUserProfile } from '../api/userProfileApi';
import styles from '../assets/UserDetailsPage.module.css';

const UserDetailsPage: Component = () => {
    const params = useParams();
    const userId = () => parseInt(params.id || '0');
    const [user] = createResource(userId, fetchUser);
    const [profile, { refetch: refetchProfile }] = createResource(userId, fetchUserProfile);

    const [showSupervisorModal, setShowSupervisorModal] = createSignal(false);
    const [showPreferencesModal, setShowPreferencesModal] = createSignal(false);

    const [prefs2FA, setPrefs2FA] = createSignal(false);
    const [prefsData, setPrefsData] = createSignal(false);

    const openPreferences = () => {
        if (profile()) {
            setPrefs2FA(profile()!.twoFactorEnabled);
            setPrefsData(profile()!.additionalDataCollecting);
            setShowPreferencesModal(true);
        }
    };

    const savePreferences = async () => {
        try {
            await updateUserProfile(userId(), {
                userId: userId(),
                twoFactorEnabled: prefs2FA(),
                additionalDataCollecting: prefsData()
            });
            await refetchProfile();
            setShowPreferencesModal(false);
        } catch (e) {
            alert('Nie udało się zapisać preferencji.');
        }
    };

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <header class={styles.header}>
                    <h1 class={styles.title}>Szczegóły Użytkownika</h1>
                    <A href="/users" class={styles.backButton}>← Powrót</A>
                </header>

                <Show when={!user.loading} fallback={<p class={styles.loading}>Ładowanie danych...</p>}>
                    <Show when={user() && !user.error} fallback={<p class={styles.error}>Błąd: Nie znaleziono użytkownika.</p>}>
                        <div class={styles.card}>
                            <div class={styles.sectionHeader}>Dane osobowe</div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>ID:</span>
                                <span class={styles.value}>{user()?.id}</span>
                            </div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>Imię i Nazwisko:</span>
                                <span class={styles.value}>{user()?.firstName} {user()?.lastName}</span>
                            </div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>Nazwa użytkownika:</span>
                                <span class={styles.value}>{user()?.username}</span>
                            </div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>PESEL:</span>
                                <span class={styles.valueMono}>{user()?.pesel}</span>
                            </div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>Telefon:</span>
                                <span class={styles.value}>{user()?.phoneNumber}</span>
                            </div>

                            <div class={styles.buttonGroup}>
                                <button class={styles.supervisorButton} onClick={() => setShowSupervisorModal(true)}>
                                    👨‍💼 Skontaktuj się z opiekunem
                                </button>
                                <button class={styles.preferencesButton} onClick={openPreferences}>
                                    ⚙️ Edytuj preferencje
                                </button>
                            </div>
                        </div>
                    </Show>
                </Show>
            </div>


            <Show when={showSupervisorModal()}>
                <div class={styles.modalOverlay} onClick={() => setShowSupervisorModal(false)}>
                    <div class={styles.modalContent} onClick={(e) => e.stopPropagation()}>
                        <h2 class={styles.modalTitle}>Twój Opiekun</h2>
                        <Show when={user()?.supervisorName} fallback={<p>Brak przypisanego opiekuna.</p>}>
                            <div class={styles.modalRow}>
                                <span class={styles.modalLabel}>Imię i Nazwisko:</span>
                                <span class={styles.modalValue}>{user()?.supervisorName}</span>
                            </div>
                            <div class={styles.modalRow}>
                                <span class={styles.modalLabel}>Telefon:</span>
                                <span class={styles.modalValue}>{user()?.supervisorPhoneNumber}</span>
                            </div>
                        </Show>
                        <button class={styles.closeButton} onClick={() => setShowSupervisorModal(false)}>Zamknij</button>
                    </div>
                </div>
            </Show>

            <Show when={showPreferencesModal()}>
                <div class={styles.modalOverlay}>
                    <div class={styles.modalContent}>
                        <h2 class={styles.modalTitle}>Preferencje Konta</h2>

                        <div class={styles.toggleRow}>
                            <label class={styles.switch}>
                                <input type="checkbox" checked={prefs2FA()} onChange={(e) => setPrefs2FA(e.currentTarget.checked)} />
                                <span class={styles.slider}></span>
                            </label>
                            <span class={styles.toggleLabel}>Uwierzytelnianie dwuetapowe (2FA)</span>
                        </div>

                        <div class={styles.toggleRow}>
                            <label class={styles.switch}>
                                <input type="checkbox" checked={prefsData()} onChange={(e) => setPrefsData(e.currentTarget.checked)} />
                                <span class={styles.slider}></span>
                            </label>
                            <span class={styles.toggleLabel}>Zbieranie dodatkowych danych</span>
                        </div>

                        <div class={styles.modalActions}>
                            <button class={styles.cancelBtn} onClick={() => setShowPreferencesModal(false)}>Anuluj</button>
                            <button class={styles.saveBtn} onClick={savePreferences}>Zapisz</button>
                        </div>
                    </div>
                </div>
            </Show>
        </div>
    );
};

export default UserDetailsPage;
