import { createSignal, type Component, Show } from 'solid-js';
import { useNavigate, A } from '@solidjs/router';
import { registerUser } from '../api/userApi';
import styles from '../assets/RegisterPage.module.css';

const RegisterPage: Component = () => {
    const navigate = useNavigate();

    const [firstName, setFirstName] = createSignal('');
    const [lastName, setLastName] = createSignal('');
    const [pesel, setPesel] = createSignal('');
    const [phoneNumber, setPhoneNumber] = createSignal('');
    const [username, setUsername] = createSignal('');
    const [password, setPassword] = createSignal('');


    const [error, setError] = createSignal('');

    const handleRegister = async (e: Event) => {
        e.preventDefault();
        setError('');

        if (!firstName() || !lastName() || !username() || !pesel() || !phoneNumber() || !password()) {
            setError('Wypełnij wszystkie pola');
            return;
        }

        const userData: any = {
            firstName: firstName(),
            lastName: lastName(),
            pesel: pesel().trim(),
            phoneNumber: phoneNumber(),
            username: username(),
            password: password(),
            profile: {
                userId: 0,
                twoFactorEnabled: false,
                additionalDataCollecting: false
            }
        };

        try {
            await registerUser(userData);
            navigate('/login');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Błąd rejestracji');
        }
    };

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.registerCard}>
                <h2 class={styles.title}>Dusigrosz</h2>
                <p class={styles.subtitle}>Utwórz nowe konto</p>

                <Show when={error()}>
                    <div class={styles.errorMessage}>{error()}</div>
                </Show>

                <form onSubmit={handleRegister} class={styles.form}>
                    <div class={styles.formGroup}>
                        <input
                            type="text"
                            id="firstName"
                            class={styles.input}
                            value={firstName()}
                            onInput={(e) => setFirstName(e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label for="firstName" class={styles.label}>Imię</label>
                    </div>

                    <div class={styles.formGroup}>
                        <input
                            type="text"
                            id="lastName"
                            class={styles.input}
                            value={lastName()}
                            onInput={(e) => setLastName(e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label for="lastName" class={styles.label}>Nazwisko</label>
                    </div>

                    <div class={styles.formGroup}>
                        <input
                            type="text"
                            id="username"
                            class={styles.input}
                            value={username()}
                            onInput={(e) => setUsername(e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label for="username" class={styles.label}>Nazwa użytkownika</label>
                    </div>

                    <div class={styles.formGroup}>
                        <input
                            type="password"
                            id="password"
                            class={styles.input}
                            value={password()}
                            onInput={(e) => setPassword(e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label for="password" class={styles.label}>Hasło</label>
                    </div>

                    <div class={styles.formGroup}>
                        <input
                            type="text"
                            id="pesel"
                            class={styles.input}
                            value={pesel()}
                            onInput={(e) => setPesel(e.currentTarget.value)}
                            required
                            pattern="[0-9]{11}"
                            placeholder=" "
                        />
                        <label for="pesel" class={styles.label}>PESEL</label>
                    </div>

                    <div class={styles.formGroup}>
                        <input
                            type="tel"
                            id="phoneNumber"
                            class={styles.input}
                            value={phoneNumber()}
                            onInput={(e) => setPhoneNumber(e.currentTarget.value)}
                            required
                            placeholder=" "
                        />
                        <label for="phoneNumber" class={styles.label}>Telefon</label>
                    </div>

                    <button type="submit" class={styles.submitButton}>Zarejestruj się</button>

                    <div class={styles.loginLink}>
                        Masz już konto? <A href="/login" class={styles.link}>Zaloguj się</A>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default RegisterPage;
