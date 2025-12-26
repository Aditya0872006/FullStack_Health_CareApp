import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './Component/Navbar';
import Footer from './Component/Footer';



function App() {
  return (
    <Router>
      {/* Navbar always visible */}
      <Navbar />

      {/* Main content */}
      <main>
        
      </main>

      {/* Footer always visible */}
      <Footer />
    </Router>
  );
}

export default App;
