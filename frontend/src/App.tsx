import { Router, Route } from "@solidjs/router";
import LandingPage from "./pages/LandingPage";
import ControlPanel from "./pages/ControlPanel";
import VisorsPage from "./pages/VisorsPage";
import VisorDetailsPage from "./pages/VisorDetailsPage";
import VisorFormPage from "./pages/VisorFormPage";
import UsersPage from "./pages/UsersPage";
import UserDetailsPage from "./pages/UserDetailsPage";
import UserFormPage from "./pages/UserFormPage";

function App() {
    return (
        <Router>
            <Route path="/" component={LandingPage} />
            <Route path="/control-panel" component={ControlPanel} />
            <Route path="/visors" component={VisorsPage} />
            <Route path="/visors/details/:id" component={VisorDetailsPage} />
            <Route path="/visors/new" component={VisorFormPage} />
            <Route path="/visors/edit/:id" component={VisorFormPage} />
            <Route path="/users" component={UsersPage} />
            <Route path="/users/details/:id" component={UserDetailsPage} />
            <Route path="/users/new" component={UserFormPage} />
            <Route path="/users/edit/:id" component={UserFormPage} />
        </Router>
    )
}

export default App;