import { type Component, createSignal, Show, onMount } from 'solid-js';
import { useNavigate, useParams } from '@solidjs/router';
import { createVisor, fetchVisor, updateVisor } from '../api/visorApi';
import styles from '../assets/VisorForm.module.css';

const VisorFormPage: Component = () => {
    const params = useParams();
    const navigate = useNavigate();
    const isEditMode = !!params.id;

    const [formState, setFormState] = createSignal<{
        firstName: string;
        lastName: string;
        pesel: string;
        phoneNumber: string;
        username: string;
        password?: string;
    }>({
        firstName: '',
        lastName: '',
        pesel: '',
        phoneNumber: '',
        username: '',
        password: ''
    });

    const [loading, setLoading] = createSignal(false);
    const [error, setError] = createSignal<string | null>(null);

    const [validationErrors, setValidationErrors] = createSignal<Record<string, string>>({});

    onMount(async () => {
        if (isEditMode) {
            try {
                setLoading(true);
                const data = await fetchVisor(Number(params.id));
                setFormState({
                    firstName: data.firstName,
                    lastName: data.lastName,
                    pesel: data.pesel,
                    phoneNumber: data.phoneNumber,
                    username: data.username
                });
            } catch (err) {
                setError("Nie udało się pobrać danych");
            } finally {
                setLoading(false);
            }
        }
    });

    const handleSubmit = async (e: Event) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setValidationErrors({});

        try {
            const payload = {
                ...formState(),
                pesel: formState().pesel.trim(),
                id: isEditMode ? Number(params.id) : 0
            };

            if (isEditMode) {
                await updateVisor(Number(params.id), payload as any);
            } else {
                await createVisor(payload as any);
            }
            navigate('/visors');
        } catch (err: any) {
            console.error(err);
            const responseData = err.response?.data;

            if (responseData && typeof responseData === 'object' && !responseData.message && !responseData.error) {
                setValidationErrors(responseData);
            } else {
                setError(responseData?.message || "Wystąpił błąd podczas zapisywania");
            }
        } finally {
            setLoading(false);
        }
    };

    const updateField = (field: keyof ReturnType<typeof formState>, value: string) => {
        setFormState(prev => ({ ...prev, [field]: value }));
        if (validationErrors()[field]) {
            setValidationErrors(prev => {
                const newErrors = { ...prev };
                delete newErrors[field];
                return newErrors;
            });
        }
    };

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <header class={styles.header}>
                    <h1 class={styles.title}>
                        {isEditMode ? 'Edytuj Visora' : 'Dodaj Nowego Visora'}
                    </h1>
                </header>

                <form class={styles.form} onSubmit={handleSubmit}>
                    <div class={styles.formGroup}>
                        <input
                            class={styles.input}
                            id="firstName"
                            type="text"
                            value={formState().firstName}
                            onInput={(e) => updateField('firstName', e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label class={styles.label} for="firstName">Imię</label>
                        <Show when={validationErrors().firstName}>
                            <div class={styles.error}>{validationErrors().firstName}</div>
                        </Show>
                    </div>

                    <div class={styles.formGroup}>
                        <input
                            class={styles.input}
                            id="lastName"
                            type="text"
                            value={formState().lastName}
                            onInput={(e) => updateField('lastName', e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label class={styles.label} for="lastName">Nazwisko</label>
                        <Show when={validationErrors().lastName}>
                            <div class={styles.error}>{validationErrors().lastName}</div>
                        </Show>
                    </div>

                    <div class={styles.formGroup}>
                        <input
                            class={styles.input}
                            id="pesel"
                            type="text"
                            value={formState().pesel}
                            onInput={(e) => updateField('pesel', e.currentTarget.value)}
                            required
                            maxLength={11}
                            placeholder=" "
                            disabled={isEditMode}
                        />
                        <label class={styles.label} for="pesel">PESEL</label>
                        <Show when={validationErrors().pesel}>
                            <div class={styles.error}>{validationErrors().pesel}</div>
                        </Show>
                    </div>

                    <div class={styles.formGroup}>
                        <input
                            class={styles.input}
                            id="phoneNumber"
                            type="tel"
                            value={formState().phoneNumber}
                            onInput={(e) => updateField('phoneNumber', e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label class={styles.label} for="phoneNumber">Numer Telefonu</label>
                        <Show when={validationErrors().phoneNumber}>
                            <div class={styles.error}>{validationErrors().phoneNumber}</div>
                        </Show>
                    </div>
                    <div class={styles.formGroup}>
                        <input
                            class={styles.input}
                            id="username"
                            type="text"
                            value={formState().username}
                            onInput={(e) => updateField('username', e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label class={styles.label} for="username">Nazwa Użytkownika (Login)</label>
                        <Show when={validationErrors().username}>
                            <div class={styles.error}>{validationErrors().username}</div>
                        </Show>
                    </div>

                    <Show when={!isEditMode}>
                        <div class={styles.formGroup}>
                            <input
                                class={styles.input}
                                id="password"
                                type="password"
                                value={formState().password}
                                onInput={(e) => updateField('password', e.currentTarget.value)}
                                required
                                placeholder=" "
                            />
                            <label class={styles.label} for="password">Hasło</label>
                            <Show when={validationErrors().password}>
                                <div class={styles.error}>{validationErrors().password}</div>
                            </Show>
                        </div>
                    </Show>

                    <Show when={error()}>
                        <div class={styles.globalError}>{error()}</div>
                    </Show>

                    <div class={styles.actions}>
                        <button type="button" class={`${styles.button} ${styles.cancelButton}`} onClick={() => navigate('/visors')}>
                            Anuluj
                        </button>
                        <button type="submit" class={`${styles.button} ${styles.saveButton}`} disabled={loading()}>
                            {loading() ? 'Zapisywanie...' : 'Zapisz'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default VisorFormPage;
