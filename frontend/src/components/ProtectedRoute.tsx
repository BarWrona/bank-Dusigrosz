import { Navigate } from "@solidjs/router";
import { authStore } from "../store/auth";
import type { Component, JSX } from "solid-js";

interface ProtectedRouteProps {
    roles?: string[];
    children?: JSX.Element;
}

const ProtectedRoute: Component<ProtectedRouteProps> = (props) => {
    const isAuthenticated = authStore.isAuthenticated();
    const userRole = authStore.user()?.role;

    if (!isAuthenticated) {
        return <Navigate href="/login" />;
    }

    if (props.roles && userRole && !props.roles.includes(userRole)) {
        return <Navigate href="/" />;
    }

    return <>{props.children}</>;
};

export default ProtectedRoute;
