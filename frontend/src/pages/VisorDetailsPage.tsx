import { type Component, createResource, Show } from 'solid-js';
import { useParams, A } from '@solidjs/router';
import { fetchVisor } from '../api/visorApi';
import styles from '../assets/VisorDetailsPage.module.css';

const VisorDetailsPage: Component = () => {
    const params = useParams();
    const [visor] = createResource(() => parseInt(params.id || '0'), fetchVisor);

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <header class={styles.header}>
                    <h1 class={styles.title}>Szczegóły Visora</h1>
                    <A href="/visors" class={styles.backButton}>← Powrót</A>
                </header>

                <Show when={!visor.loading} fallback={<p class={styles.loading}>Ładowanie danych...</p>}>
                    <Show when={visor() && !visor.error} fallback={<p class={styles.error}>Błąd: Nie znaleziono visora.</p>}>
                        <div class={styles.card}>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>ID:</span>
                                <span class={styles.value}>{visor()?.id}</span>
                            </div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>Imię:</span>
                                <span class={styles.value}>{visor()?.firstName}</span>
                            </div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>Nazwisko:</span>
                                <span class={styles.value}>{visor()?.lastName}</span>
                            </div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>PESEL:</span>
                                <span class={styles.valueMono}>{visor()?.pesel}</span>
                            </div>
                            <div class={styles.detailRow}>
                                <span class={styles.label}>Telefon:</span>
                                <span class={styles.value}>{visor()?.phoneNumber}</span>
                            </div>
                        </div>
                    </Show>
                </Show>
            </div>
        </div>
    );
};

export default VisorDetailsPage;
