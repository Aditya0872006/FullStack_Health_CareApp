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
          
        </Routes>
      </main>

      {/* Footer always visible */}
      <Footer />
    </Router>
  );
}

export default App;
