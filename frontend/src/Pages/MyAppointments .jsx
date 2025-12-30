import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiService } from '../Services/Api';



const MyAppointments = () => {


    const [appointments, setAppointments] = useState([]);
    const [error, setError] = useState('');
    const [rescheduleId, setRescheduleId] = useState(null);
    const [newDateTime, setNewDateTime] = useState('');


    useEffect(() => {
        fetchAppointments();
    }, [])


    const fetchAppointments = async () => {

        try {

            const response = await apiService.getMyAppointments();

            if (response.data.statusCode === 200) {
                setAppointments(response.data.data);
            }

        } catch (error) {
            setError('Failed to load appointments');

        }
    }


    const formatDateTime = (dateTimeString) => {
        return new Date(dateTimeString).toLocaleString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };



    const getStatusBadge = (status) => {
        const statusConfig = {
            'SCHEDULED': { class: 'status-scheduled', text: 'Scheduled' },
            'COMPLETED': { class: 'status-completed', text: 'Completed' },
            'CANCELLED': { class: 'status-cancelled', text: 'Cancelled' },
            'IN_PROGRESS': { class: 'status-in-progress', text: 'In Progress' }
        };

        const config = statusConfig[status] || { class: 'status-default', text: status };
        return <span className={`status-badge ${config.class}`}>{config.text}</span>;
    };



    const handleCancelAppointment = async (appointmentId) => {
        if (!window.confirm('Are you sure you want to cancel this appointment?')) {
            return;
        }

        try {
            const response = await apiService.cancelAppointment(appointmentId);
            if (response.data.statusCode === 200) {
                // Refresh appointments list
                fetchAppointments();
            } else {
                setError('Failed to cancel appointment');
            }
        } catch (error) {
            setError('Error cancelling appointment');
        }
    };



    if (error) {
        return (
            <div className="container">
                <div className="form-container">
                    <div className="alert alert-error">{error}</div>
                </div>
            </div>
        );
    }

    


const submitReschedule = async (appointmentId) => {
    if (!newDateTime) {
        setError("Please select a date and time");
        return;
    }

    try {
        const response = await apiService.rescheduleAppointment(
            appointmentId,
            { newStartTime: newDateTime }
        );

        if (response.data.statusCode === 200) {
            setRescheduleId(null);
            setNewDateTime('');
            fetchAppointments();
        }
    } catch (err) {
        setError("Failed to reschedule appointment");
    }
};


const oneHourLater = new Date(
    Date.now() + 60 * 60 * 1000
).toISOString().slice(0, 16);





    return (
        <div className="container">
            <div className="page-container">
                <div className="page-header">
                    <h1 className="page-title">My Appointments</h1>
                    <Link to="/book-appointment" className="btn btn-primary">
                        Book New Appointment
                    </Link>
                </div>

                {appointments.length === 0 ? (
                    <div className="empty-state">
                        <h3>No Appointments Found</h3>
                        <p>You haven't booked any appointments yet.</p>
                        <Link to="/book-appointment" className="btn btn-primary">
                            Book Your First Appointment
                        </Link>
                    </div>
                ) : (
                    <div className="appointments-list">
                        {appointments.map((appointment) => (
                            <div key={appointment.id} className="appointment-card">
                                <div className="appointment-header">
                                    <div className="appointment-info">
                                        <h3 className="doctor-name">
                                            Dr. {appointment.doctor.firstName} {appointment.doctor.lastName}
                                        </h3>
                                        <p className="specialization">
                                            {appointment.doctor.specialization?.replace(/_/g, ' ')}
                                        </p>
                                    </div>
                                    <div className="appointment-actions">
                                        {getStatusBadge(appointment.status)}
                                        {appointment.status === 'SCHEDULED' && (
                                            <button
                                                onClick={() => handleCancelAppointment(appointment.id)}
                                                className="btn btn-danger btn-sm"
                                            >
                                                Cancel
                                            </button>
                                        )}
                                    </div>
                                                        {appointment.status === 'SCHEDULED' && (
                        rescheduleId === appointment.id ? (
                            <div className="reschedule-box">
                                <input
                                    type="datetime-local"
                                    value={newDateTime}
                                    min={oneHourLater}
                                    onChange={(e) => setNewDateTime(e.target.value)}
                                    className="form-input"
                                />

                                <button
                                    className="btn btn-success btn-sm"
                                    onClick={() => submitReschedule(appointment.id)}
                                >
                                    Confirm
                                </button>

                                <button
                                    className="btn btn-secondary btn-sm"
                                    onClick={() => {
                                        setRescheduleId(null);
                                        setNewDateTime('');
                                    }}
                                >
                                    Cancel
                                </button>
                            </div>
                        ) : (
                            <button
                                className="btn btn-warning btn-sm"
                                onClick={() => setRescheduleId(appointment.id)}
                            >
                                Reschedule
                            </button>
                        )
                    )}

                                </div>

                                <div className="appointment-details">
                                    <div className="detail-row">
                                        <div className="detail-item">
                                            <label>Date & Time:</label>
                                            <span>{formatDateTime(appointment.startTime)}</span>
                                        </div>
                                        <div className="detail-item">
                                            <label>Duration:</label>
                                            <span>1 hour</span>
                                        </div>
                                    </div>

                                    <div className="detail-item">
                                        <label>Purpose:</label>
                                        <span>{appointment.purposeOfConsultation}</span>
                                    </div>

                                    <div className="detail-item">
                                        <label>Symptoms:</label>
                                        <span>{appointment.initialSymptoms}</span>
                                    </div>

                                    {appointment.meetingLink && appointment.status === 'SCHEDULED' && (
                                        <div className="detail-item">
                                            <label>Meeting Link:</label>
                                            <a
                                                href={appointment.meetingLink}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="meeting-link"
                                            >
                                                Join Video Consultation
                                            </a>
                                        </div>
                                    )}

                                    {appointment.status === 'COMPLETED' && (
                                        <div className="detail-item">
                                            <Link
                                                to={`/consultation-history?appointmentId=${appointment.id}`}
                                                className="btn btn-outline btn-sm"
                                            >
                                                View Consultation Notes
                                            </Link>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
export default MyAppointments;