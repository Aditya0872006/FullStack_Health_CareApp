import { Navigate } from "react-router-dom";
import { apiService } from "./Api";

// Only PATIENT can access
export const PatientsOnlyRoute = ({ element: Component }) => {
    return apiService.isPatient()
        ? Component
        : <Navigate to="/login" />;
};

// Only DOCTOR can access
export const DoctorsOnlyRoute = ({ element: Component }) => {
    return apiService.isDoctor()
        ? Component
        : <Navigate to="/login" />;
};

// Both DOCTOR & PATIENT can access
export const DoctorsAndPatientRoute = ({ element: Component }) => {
    return apiService.isAuthenticated()
        ? Component
        : <Navigate to="/login" />;
};
