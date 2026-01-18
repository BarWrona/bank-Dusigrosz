import { Router, Route } from "@solidjs/router";
import LandingPage from "./pages/LandingPage";
import ControlPanel from "./pages/ControlPanel";
import VisorsPage from "./pages/VisorsPage";
import VisorDetailsPage from "./pages/VisorDetailsPage";
import VisorFormPage from "./pages/VisorFormPage";
import UsersPage from "./pages/UsersPage";
import UserDetailsPage from "./pages/UserDetailsPage";
import UserFormPage from "./pages/UserFormPage";
import TransferPage from "./pages/TransferPage";
import AccountFormPage from "./pages/AccountFormPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import UserDashboard from "./pages/UserDashboard";
import AccountsPage from "./pages/AccountsPage";
import TransfersListPage from "./pages/TransfersListPage";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    return (
        <Router>
            <Route path="/login" component={LoginPage} />
            <Route path="/register" component={RegisterPage} />
            <Route path="/" component={LandingPage} />



            <Route path="/" component={(props) => <ProtectedRoute roles={['ADMIN', 'VISOR']}>{props.children}</ProtectedRoute>} >
                <Route path="/control-panel" component={ControlPanel} />
                <Route path="/visors" component={VisorsPage} />
                <Route path="/visors/details/:id" component={VisorDetailsPage} />
                <Route path="/visors/new" component={VisorFormPage} />
                <Route path="/visors/edit/:id" component={VisorFormPage} />
                <Route path="/users" component={UsersPage} />
                <Route path="/users/details/:id" component={UserDetailsPage} />
                <Route path="/users/edit/:id" component={UserFormPage} />
                <Route path="/accounts" component={AccountsPage} />
                <Route path="/transfers/list" component={TransfersListPage} />
            </Route>

            <Route path="/" component={(props) => <ProtectedRoute roles={['ADMIN']}>{props.children}</ProtectedRoute>} >
                <Route path="/users/new" component={UserFormPage} />
            </Route>


            <Route path="/" component={(props) => <ProtectedRoute roles={['USER']}>{props.children}</ProtectedRoute>} >
                <Route path="/dashboard" component={UserDashboard} />
            </Route>


            <Route path="/" component={(props) => <ProtectedRoute>{props.children}</ProtectedRoute>} >
                <Route path="/transfers" component={TransferPage} />
                <Route path="/accounts/new" component={AccountFormPage} />
            </Route>

        </Router >
    )
}

export default App;