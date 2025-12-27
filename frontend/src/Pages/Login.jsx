

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiService } from '../Services/Api';


const Login = () => {

    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();


    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };


    const handleSubmit = async (e) => {

        e.preventDefault();
        setLoading(true);
        setError('');

        try {

            const response = await apiService.login(formData);

            if (response.data.statusCode === 200) {

                const { token, roles } = response.data.data;

                apiService.saveAuthData(token, roles)

                navigate('/home')
            } else {
                setError(response.data.message || 'Login failed');
            }

        } catch (error) {
            setError(error.response?.data?.message || 'An error occurred during Login');

        } finally {
            setLoading(false)
        }
    }





    return (
        <div className="container">
            <div className="form-container">
                <h2 className="form-title">Login</h2>

                {error && (
                    <div className="alert alert-error">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>

                    <div className="form-group">
                        <label className="form-label">Email</label>
                        <input
                            type="email"
                            name="email"
                            className="form-input"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Password</label>
                        <input
                            type="password"
                            name="password"
                            className="form-input"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            minLength={6}
                        />
                    </div>

                    <button
                        type="submit"
                        className="form-btn"
                        disabled={loading}
                    >
                        {loading ? 'Login...' : 'Login'}
                    </button>
                </form>


                <div className="form-link">
                    <p>
                        Don't have an account?{' '}
                        <Link to="/register">Register as Patient</Link> or{' '}
                        <Link to="/register-doctor">Register as Doctor</Link>
                    </p>
                    <p>
                        Forgot Password?{' '}
                        <Link to="/forgot-password">Reset Password here</Link>

                    </p>
                </div>

            </div>
        </div>
    );


}
export default Login;
