import { A } from "@solidjs/router";
import styles from '../assets/ControlPanel.module.css'
import { authStore } from '../store/auth';
import { Show } from "solid-js";

const ControlPanel = () => {
    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <div class={styles.headerFlex}>
                    <h1 class={styles.title}>Panel Zarządzania Dusigrosz</h1>
                    <button class={styles.logoutButton} onClick={() => authStore.logout()}>Wyloguj</button>
                </div>
                <p class={styles.subtitle}>Wybierz moduł, którym chcesz zarządzać</p>

                <div class={styles.navGrid}>
                    <Show when={authStore.user()?.role === 'ADMIN'}>
                        <A href="/visors" class={styles.card}>
                            <span class={styles.cardIcon}>🕵️‍♂️</span>
                            <div class={styles.cardTitle}>Zarządzanie Visorami</div>
                            <div class={styles.cardDescription}>
                                Dodawaj, edytuj i usuwaj nadzorców systemu.
                            </div>
                        </A>
                    </Show>

                    <A href="/users" class={styles.card}>
                        <span class={styles.cardIcon}>👥</span>
                        <span class={styles.cardTitle}>Użytkownicy</span>
                        <span class={styles.cardDescription}>Zarządzaj listą użytkowników</span>
                    </A>

                    <Show when={authStore.user()?.role === 'ADMIN'}>
                        <A href="/users/new" class={styles.card}>
                            <span class={styles.cardIcon}>➕</span>
                            <span class={styles.cardTitle}>Dodaj Użytkownika</span>
                            <span class={styles.cardDescription}>Utwórz nowe konto</span>
                        </A>
                    </Show>

                    <A href="/accounts" class={styles.card}>
                        <span class={styles.cardIcon}>💳</span>
                        <div class={styles.cardTitle}>Konta Bankowe</div>
                        <div class={styles.cardDescription}>
                            Monitoruj stany kont, salda i historię.
                        </div>
                    </A>

                    <Show when={authStore.user()?.role === 'ADMIN'}>
                        <A href="/transfers" class={styles.card}>
                            <span class={styles.cardIcon}>💸</span>
                            <div class={styles.cardTitle}>Wykonaj Przelew</div>
                            <div class={styles.cardDescription}>
                                Zlecaj przelewy i sprawdzaj kursy walut.
                            </div>
                        </A>
                    </Show>

                    <A href="/transfers/list" class={styles.card}>
                        <span class={styles.cardIcon}>📋</span>
                        <div class={styles.cardTitle}>Historia Przelewów</div>
                        <div class={styles.cardDescription}>
                            Przeglądaj historię wszystkich transakcji.
                        </div>
                    </A>
                </div>
            </div>
        </div>
    );
};

export default ControlPanel;
