import { createSignal, createRoot } from "solid-js";

interface User {
    id: number;
    username: string;
    role: "USER" | "VISOR" | "ADMIN";
}

function createAuthStore() {
    const [token, setToken] = createSignal<string | null>(localStorage.getItem("token"));
    const [user, setUser] = createSignal<User | null>(
        localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")!) : null
    );

    const login = (newToken: string, newUser: User) => {
        setToken(newToken);
        setUser(newUser);
        localStorage.setItem("token", newToken);
        localStorage.setItem("user", JSON.stringify(newUser));
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/login";
    };

    return { token, user, login, logout, isAuthenticated: () => !!token() };
}

export const authStore = createRoot(createAuthStore);
