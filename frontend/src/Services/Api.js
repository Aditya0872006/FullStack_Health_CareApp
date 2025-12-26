import axios from "axios";

// ✅ Local variable for API base URL
const API_BASE_URL = "http://localhost:8086/api";

// Create axios instance
const api = axios.create({
    baseURL: API_BASE_URL,          // Base URL for all requests
    headers: {
        'Content-Type': 'application/json'  // Default content type
    }
});

// Add token to requests if available
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token'); // Get token from local storage
        if (token) {
            config.headers.Authorization = `Bearer ${token}`; // Add Authorization header
        }
        return config;
    },
    (error) => {
        return Promise.reject(error); // Handle request errors
    }
);

export const apiService = {
    // AUTH DATA MANAGEMENT
    saveAuthData: (token, roles) => {
        localStorage.setItem('token', token);         // Save token
        localStorage.setItem('roles', JSON.stringify(roles)); // Save roles
    },

    logout: () => {
        localStorage.removeItem('token');             // Remove token
        localStorage.removeItem('roles');             // Remove roles
    },

    hasRole(role) {
        const roles = localStorage.getItem('roles'); // Get saved roles
        return roles ? JSON.parse(roles).includes(role) : false; // Check if user has role
    },

    isAuthenticated: () => {
        return localStorage.getItem('token') !== null; // Check if token exists
    },

    isDoctor() {
        return this.hasRole('DOCTOR'); // Check if user is doctor
    },

    isPatient() {
        return this.hasRole('PATIENT'); // Check if user is patient
    },

    // AUTH & USERS MANAGEMENT METHODS
    login: (body) => api.post('/auth/login', body),
    register: (body) => api.post('/auth/register', body),
    forgetPassword: (body) => api.post('/auth/forgot-password', body),
    resetPassword: (body) => api.post('/auth/reset-password', body),
    getMyUserDetails: () => api.get("/users/me"),
    getUserById: (userId) => api.get(`/users/by-id/${userId}`),
    getAllUsers: () => api.get("/users/all"),
    updatePassword: (updatePasswordRequest) => api.put("/users/update-password", updatePasswordRequest),

    uploadProfilePicture: (file) => {
        const formData = new FormData();
        formData.append('file', file);
        return api.put("/users/profile-picture", formData, {
            headers: { 'Content-Type': 'multipart/form-data' } // For file uploads
        });
    },

    // PATIENTS ACCOUNT MANAGEMENT
    getMyPatientProfile: () => api.get("/patients/me"),
    updateMyPatientProfile: (body) => api.put('/patients/me', body),
    getPatientById: (patientId) => api.get(`/patients/${patientId}`),
    getAllGenotypeEnums: () => api.get(`/patients/genotype`),
    getAllBloodGroupEnums: () => api.get(`/patients/bloodgroup`),

    // DOCTORS ACCOUNT MANAGEMENT
    getMyDoctorProfile: () => api.get("/doctors/me"),
    updateMyDoctorProfile: (body) => api.put("/doctors/me", body),
    getAllDoctors: () => api.get("/doctors"),
    getDoctorById: (doctorId) => api.get(`/doctors/${doctorId}`),
    getAllDoctorSpecializations: () => api.get("/doctors/specializations"),

    // APPOINTMENT MANAGEMENT
    bookAppointment: (appointmentDTO) => api.post("/appointments", appointmentDTO),
    getMyAppointments: () => api.get("/appointments"),
    cancelAppointment: (appointmentId) => api.put(`/appointments/cancel/${appointmentId}`),
    completeAppointment: (appointmentId) => api.put(`/appointments/complete/${appointmentId}`),

    // CONSULTATION MANAGEMENT
    createConsultation: (consultationDTO) => api.post("/consultations", consultationDTO),
    getConsultationByAppointmentId: (appointmentId) => api.get(`/consultations/appointment/${appointmentId}`),
    getConsultationHistoryForPatient: (patientId) => api.get("/consultations/history", {
        params: { patientId }
    }),
};

export default api;
