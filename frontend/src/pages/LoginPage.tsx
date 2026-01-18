import { createSignal } from "solid-js";
import { useNavigate } from "@solidjs/router";
import { login } from "../api/authApi";
import { authStore } from "../store/auth";
import styles from "../assets/LoginPage.module.css";

const LoginPage = () => {
    const [username, setUsername] = createSignal("");
    const [password, setPassword] = createSignal("");
    const [error, setError] = createSignal("");
    const navigate = useNavigate();

    const handleLogin = async (e: Event) => {
        e.preventDefault();
        setError("");
        try {
            const response = await login({ username: username(), password: password() });
            authStore.login(response.token, {
                id: response.id,
                username: response.username,
                role: response.role as "USER" | "VISOR" | "ADMIN"
            });

            // Redirect based on role
            if (response.role === "ADMIN") {
                navigate("/control-panel");
            } else if (response.role === "VISOR") {
                navigate("/users"); // Or visor dashboard
            } else {
                navigate("/dashboard");
            }
        } catch (err) {
            setError("Invalid credentials");
            console.error("Login failed", err);
        }
    };

    return (
        <div class={styles.pageWrapper}>
            <div class={styles.loginCard}>
                <h2 class={styles.title}>Dusigrosz</h2>
                <p class={styles.subtitle}>Zaloguj się do swojego konta</p>

                {error() && <div class={styles.errorMessage}>{error()}</div>}

                <form onSubmit={handleLogin} class={styles.form}>
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

                    <button type="submit" class={styles.submitButton}>Zaloguj się</button>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
