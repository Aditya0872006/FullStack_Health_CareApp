import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './Component/Navbar';
import Footer from './Component/Footer';
import Home from './Pages/Home';
import Register from './Pages/Register';
import Login from './Pages/Login';
import DoctorRegister from './Pages/DoctorRegister ';
import ForgotPassword from './Pages/ForgotPassword';
import ResetPassword from './Pages/ResetPassword ';
import Profile from './Pages/Profile';
import UpdateProfile from './Pages/UpdateProfile ';
import UpdatePassword from './Pages/UpdatePassword ';
import BookAppointment from './Pages/BookAppointment';
import MyAppointments from './Pages/MyAppointments ';
import ConsultationHistory from './Pages/ConsultationHistory ';
import DoctorProfile from './Pages/DoctorProfile ';
import UpdateDoctorProfile from './Pages/UpdateDoctorProfile';




function App() {
  return (
    <Router>
      {/* Navbar always visible */}
      <Navbar />

      {/* Main content */}
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register-doctor" element={<DoctorRegister />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/update-profile" element={<UpdateProfile />} />
          <Route path="/update-password" element={<UpdatePassword />} />
          <Route path="/book-appointment" element={<BookAppointment />} />
          <Route path="/my-appointments" element={<MyAppointments />} />
          <Route path="/consultation-history" element={<ConsultationHistory />} />
          <Route path="/doctor/profile" element={<DoctorProfile />} />
          <Route path="/doctor/update-profile" element={<UpdateDoctorProfile />} />


           <Route path="*" element={<Home />} />
          
        </Routes>
      </main>

      {/* Footer always visible */}
      <Footer />
    </Router>
  );
}

export default App;
