import { type Component, createResource, createSignal, For, Show } from 'solid-js';
import { useNavigate } from '@solidjs/router';
import { fetchVisors, deleteVisor } from '../api/visorApi';
import styles from '../assets/VisorsPage.module.css';

const VisorsPage: Component = () => {
    const [visors, { refetch }] = createResource(fetchVisors);
    const [deleteConfirmationId, setDeleteConfirmationId] = createSignal<number | null>(null);
    const navigate = useNavigate();

    const confirmDelete = async (id: number) => {
        try {
            await deleteVisor(id);
            await refetch();
            setDeleteConfirmationId(null);
        } catch (error) {
            alert('Błąd podczas usuwania visora');
        }
    };

    const cancelDelete = () => {
        setDeleteConfirmationId(null);
    }

    const handleEdit = (id: number) => {
        navigate(`/visors/edit/${id}`);
    };

    const handleAdd = () => {
        navigate('/visors/new');
    };
    return (
        <div class={styles.pageWrapper}>
            <main class={styles.container}>
                <header class={styles.header}>
                    <h1 class={styles.title}>Lista Visorów</h1>
                    <div class={styles.headerActions}>
                        <button class={styles.backButton} onClick={() => navigate('/control-panel')}>
                            ← Strona Główna
                        </button>
                        <button class={styles.addButton} onClick={handleAdd}>
                            + Dodaj nowego
                        </button>
                    </div>
                </header>

                <Show when={!visors.loading} fallback={<p class={styles.info}>Ładowanie danych...</p>}>
                    <Show when={!visors.error} fallback={
                        <p class={`${styles.info} ${styles.error}`}>Błąd: {visors.error.message}</p>
                    }>
                        <div class={styles.tableWrapper}>
                            <table class={styles.table}>
                                <thead>
                                    <tr>
                                        <th>Id</th>
                                        <th>Imię</th>
                                        <th>Nazwisko</th>
                                        <th class={styles.actionsHeader}>Akcje</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <For each={visors()}>
                                        {(visor) => (
                                            <tr>
                                                <td class={styles.idCell}>{visor.id}</td>
                                                <td>{visor.firstName}</td>
                                                <td>{visor.lastName}</td>
                                                <td class={styles.actionsCell}>
                                                    <button class={styles.detailsButton} onClick={() => navigate(`/visors/details/${visor.id}`)}>
                                                        Szczegóły
                                                    </button>
                                                    <button class={styles.editButton} onClick={() => handleEdit(visor.id!)}>
                                                        Edytuj
                                                    </button>
                                                    <div class={styles.deleteWrapper}>
                                                        <button class={styles.deleteButton}
                                                            onClick={() => setDeleteConfirmationId(visor.id!)}>
                                                            Usuń
                                                        </button>
                                                        <Show when={deleteConfirmationId() === visor.id}>
                                                            <div class={styles.confirmationPopup}>
                                                                <p class={styles.popupText}>Czy na pewno?</p>
                                                                <div class={styles.popupActions}>
                                                                    <button class={styles.cancelBtn} onClick={cancelDelete}>Nie</button>
                                                                    <button class={styles.confirmBtn} onClick={() => confirmDelete(visor.id!)}>Tak</button>
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

export default VisorsPage;