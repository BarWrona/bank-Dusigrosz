import { type Component, createEffect, createSignal, Show } from 'solid-js';
import { useNavigate, useParams } from '@solidjs/router';
import { fetchUser, createUser, updateUser, type User } from '../api/userApi';
import styles from '../assets/UserForm.module.css';

const UserFormPage: Component = () => {
    const params = useParams();
    const navigate = useNavigate();

    const [step, setStep] = createSignal(1);
    const isEditMode = () => !!params.id;

    const [firstName, setFirstName] = createSignal('');
    const [lastName, setLastName] = createSignal('');
    const [pesel, setPesel] = createSignal('');
    const [phoneNumber, setPhoneNumber] = createSignal('');
    const [username, setUsername] = createSignal('');
    const [password, setPassword] = createSignal('');

    const [twoFactor, setTwoFactor] = createSignal(false);
    const [dataCollecting, setDataCollecting] = createSignal(false);

    const [error, setError] = createSignal('');

    createEffect(async () => {
        if (isEditMode()) {

            try {
                const user = await fetchUser(parseInt(params.id || '0'));
                setFirstName(user.firstName);
                setLastName(user.lastName);
                setPesel(user.pesel);
                setPhoneNumber(user.phoneNumber);
                setUsername(user.username);
            } catch (err) {
                setError('Nie udało się pobrać danych użytkownika');
            }
        }
    });

    const handleNext = (e: Event) => {
        e.preventDefault();
        if (!firstName() || !lastName() || !username() || !pesel() || !phoneNumber()) {
            setError('Wypełnij wszystkie pola obowiązkowe');
            return;
        }
        setError('');
        setStep(2);
    };

    const handleBack = () => {
        setError('');
        setStep(1);
    };

    const handleSubmit = async () => {
        const userData: Partial<User> = {
            firstName: firstName(),
            lastName: lastName(),
            pesel: pesel().trim(),
            phoneNumber: phoneNumber(),
            username: username(),
            password: password(),
            profile: {
                userId: 0,
                twoFactorEnabled: twoFactor(),
                additionalDataCollecting: dataCollecting()
            }
        };

        try {
            if (isEditMode()) {

                await updateUser(parseInt(params.id || '0'), { ...userData, id: parseInt(params.id || '0') } as User);
            } else {
                await createUser(userData as User);
            }
            navigate('/users');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Wystąpił błąd podczas zapisywania');
        }
    };

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.container}>
                <div class={styles.header}>
                    <h1 class={styles.title}>{isEditMode() ? 'Edytuj Użytkownika' : 'Kreator Użytkownika'}</h1>
                    <button class={styles.backButtonTop} onClick={() => navigate('/control-panel')}>Wróć do Panelu</button>
                </div>

                <div class={styles.steps}>
                    <div class={`${styles.step} ${step() >= 1 ? styles.activeStep : ''}`}>1</div>
                    <div class={styles.stepLine}></div>
                    <div class={`${styles.step} ${step() >= 2 ? styles.activeStep : ''}`}>2</div>
                </div>
                <div class={styles.stepLabels}>
                    <span>Dane</span>
                    <span>Profil</span>
                </div>

                <Show when={error()}>
                    <div class={styles.error}>{error()}</div>
                </Show>

                <form class={styles.form}>
                    <Show when={step() === 1}>
                        <div class={styles.stepContent}>
                            <div class={styles.formGroup}>
                                <input class={styles.input} type="text" id="firstName" value={firstName()} onInput={(e) => setFirstName(e.currentTarget.value)} required placeholder=" " />
                                <label class={styles.label} for="firstName">Imię</label>
                            </div>
                            <div class={styles.formGroup}>
                                <input class={styles.input} type="text" id="lastName" value={lastName()} onInput={(e) => setLastName(e.currentTarget.value)} required placeholder=" " />
                                <label class={styles.label} for="lastName">Nazwisko</label>
                            </div>
                            <div class={styles.formGroup}>
                                <input class={styles.input} type="text" id="username" value={username()} onInput={(e) => setUsername(e.currentTarget.value)} required placeholder=" " />
                                <label class={styles.label} for="username">Nazwa użytkownika</label>
                            </div>
                            <Show when={!isEditMode()}>
                                <div class={styles.formGroup}>
                                    <input class={styles.input} type="password" id="password" value={password()} onInput={(e) => setPassword(e.currentTarget.value)} required placeholder=" " />
                                    <label class={styles.label} for="password">Hasło</label>
                                </div>
                            </Show>
                            <div class={styles.formGroup}>
                                <input class={styles.input} type="text" id="pesel" value={pesel()} onInput={(e) => setPesel(e.currentTarget.value)} required pattern="\d{11}" disabled={isEditMode()} placeholder=" " />
                                <label class={styles.label} for="pesel">PESEL</label>
                            </div>
                            <div class={styles.formGroup}>
                                <input class={styles.input} type="tel" id="phoneNumber" value={phoneNumber()} onInput={(e) => setPhoneNumber(e.currentTarget.value)} required placeholder=" " />
                                <label class={styles.label} for="phoneNumber">Telefon</label>
                            </div>
                            <button class={styles.nextButton} onClick={handleNext}>Dalej →</button>
                        </div>
                    </Show>

                    <Show when={step() === 2}>
                        <div class={styles.stepContent}>
                            <h3 class={styles.stepTitle}>Preferencje Profilu</h3>

                            <div class={styles.toggleRow}>
                                <label class={styles.switch}>
                                    <input type="checkbox" checked={twoFactor()} onChange={(e) => setTwoFactor(e.currentTarget.checked)} />
                                    <span class={styles.slider}></span>
                                </label>
                                <span class={styles.toggleLabel}>Uwierzytelnianie dwuetapowe (2FA)</span>
                            </div>

                            <div class={styles.toggleRow}>
                                <label class={styles.switch}>
                                    <input type="checkbox" checked={dataCollecting()} onChange={(e) => setDataCollecting(e.currentTarget.checked)} />
                                    <span class={styles.slider}></span>
                                </label>
                                <span class={styles.toggleLabel}>Zbieranie danych</span>
                            </div>

                            <div class={styles.buttonGroup}>
                                <button type="button" class={styles.backButton} onClick={handleBack}>← Wstecz</button>
                                <button type="button" class={styles.submitButton} onClick={handleSubmit}>
                                    {isEditMode() ? 'Zapisz zmiany' : 'Utwórz Użytkownika'}
                                </button>
                            </div>
                        </div>
                    </Show>
                </form>
            </div>
        </div>
    );
};

export default UserFormPage;
