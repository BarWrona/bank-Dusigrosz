import { type Component, createResource, createSignal, For, Show } from 'solid-js';
import { useNavigate } from '@solidjs/router';
import { fetchUsers, deleteUser } from '../api/userApi';
import styles from '../assets/UsersPage.module.css';

const UsersPage: Component = () => {
    const [users, { refetch }] = createResource(fetchUsers);
    const [deleteConfirmationId, setDeleteConfirmationId] = createSignal<number | null>(null);
    const navigate = useNavigate();

    const confirmDelete = async (id: number) => {
        try {
            await deleteUser(id);
            await refetch();
            setDeleteConfirmationId(null);
        } catch (error) {
            alert('Błąd podczas usuwania użytkownika');
        }
    };

    const cancelDelete = () => {
        setDeleteConfirmationId(null);
    }

    return (
        <div class={styles.pageWrapper}>
            <main class={styles.container}>
                <header class={styles.header}>
                    <h1 class={styles.title}>Lista Użytkowników</h1>
                    <div class={styles.headerActions}>
                        <button class={styles.backButton} onClick={() => navigate('/control-panel')}>
                            ← Strona Główna
                        </button>
                        <button class={styles.addButton} onClick={() => navigate('/users/new')}>
                            + Dodaj nowego
                        </button>
                    </div>
                </header>

                <Show when={!users.loading} fallback={<p class={styles.info}>Ładowanie danych...</p>}>
                    <Show when={!users.error} fallback={
                        <p class={`${styles.info} ${styles.error}`}>Błąd: {users.error.message}</p>
                    }>
                        <div class={styles.tableWrapper}>
                            <table class={styles.table}>
                                <thead>
                                    <tr>
                                        <th>Id</th>
                                        <th>Imię</th>
                                        <th>Nazwisko</th>
                                        <th>Username</th>
                                        <th class={styles.actionsHeader}>Akcje</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <For each={users()}>
                                        {(user) => (
                                            <tr>
                                                <td class={styles.idCell}>{user.id}</td>
                                                <td>{user.firstName}</td>
                                                <td>{user.lastName}</td>
                                                <td>{user.username}</td>
                                                <td class={styles.actionsCell}>
                                                    <button class={styles.detailsButton} onClick={() => navigate(`/users/details/${user.id}`)}>
                                                        Szczegóły
                                                    </button>
                                                    <button class={styles.editButton} onClick={() => navigate(`/users/edit/${user.id}`)}>
                                                        Edytuj
                                                    </button>
                                                    <div class={styles.deleteWrapper}>
                                                        <button class={styles.deleteButton}
                                                            onClick={() => setDeleteConfirmationId(user.id)}>
                                                            Usuń
                                                        </button>
                                                        <Show when={deleteConfirmationId() === user.id}>
                                                            <div class={styles.confirmationPopup}>
                                                                <p class={styles.popupText}>Czy na pewno?</p>
                                                                <div class={styles.popupActions}>
                                                                    <button class={styles.cancelBtn} onClick={cancelDelete}>Nie</button>
                                                                    <button class={styles.confirmBtn} onClick={() => confirmDelete(user.id)}>Tak</button>
                                                                </div>
                                                            </div>
                                                        </Show>
                                                    </div>
                                                </td>
                                            </tr>
                                        )}
                                    </For>
                                </tbody>
                            </table>
                        </div>
                    </Show>
                </Show>
            </main>
        </div>
    );
};

export default UsersPage;
