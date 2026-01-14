import { A } from "@solidjs/router";
import styles from '../assets/ControlPanel.module.css'

const ControlPanel = () => {
    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <h1 class={styles.title}>Panel Zarządzania Dusigrosz</h1>
                <p class={styles.subtitle}>Wybierz moduł, którym chcesz zarządzać</p>

                <div class={styles.navGrid}>
                    <A href="/visors" class={styles.card}>
                        <span class={styles.cardIcon}>🕵️‍♂️</span>
                        <div class={styles.cardTitle}>Zarządzanie Visorami</div>
                        <div class={styles.cardDescription}>
                            Dodawaj, edytuj i usuwaj nadzorców systemu.
                        </div>
                    </A>

                    <A href="/users" class={styles.card}>
                        <span class={styles.cardIcon}>👥</span>
                        <div class={styles.cardTitle}>Lista Użytkowników</div>
                        <div class={styles.cardDescription}>
                            Przeglądaj bazę użytkowników i ich szczegóły.
                        </div>
                    </A>

                    <A href="/accounts" class={styles.card}>
                        <span class={styles.cardIcon}>💳</span>
                        <div class={styles.cardTitle}>Konta Bankowe</div>
                        <div class={styles.cardDescription}>
                            Monitoruj stany kont, salda i historię.
                        </div>
                    </A>
                </div>
            </div>
        </div>
    );
};

export default ControlPanel;
