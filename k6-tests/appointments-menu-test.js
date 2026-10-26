import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { BASE_URL, getProfiles, login } from './common.js';

export const options = getProfiles({
    smoke: {
        'group_duration{group:::Get Appointments List}': ['p(95)<250'],
        'group_duration{group:::Get Appointments List with filter}': ['p(95)<300'],
        'group_duration{group:::Create Appointment}': ['p(95)<350'],
        'group_duration{group:::Delete Appointment}': ['p(95)<250'],
    },
    load: {
        'group_duration{group:::Get Appointments List}': ['p(95)<400'],
        'group_duration{group:::Get Appointments List with filter}': ['p(95)<500'],
        'group_duration{group:::Create Appointment}': ['p(95)<600'],
        'group_duration{group:::Delete Appointment}': ['p(95)<400'],
    },
    stress: {
        'group_duration{group:::Get Appointments List}': ['p(95)<800'],
        'group_duration{group:::Get Appointments List with filter}': ['p(95)<1000'],
        'group_duration{group:::Create Appointment}': ['p(95)<1200'],
        'group_duration{group:::Delete Appointment}': ['p(95)<800'],
    },
});

export function setup() {
    const loginRes = login('ADMIN', 1234);
    const token = loginRes.json('token');

    const docRes = http.post(`${BASE_URL}/doctors`, JSON.stringify({
        doctorName: `Dr. Fixture`,
        doctorPhone: `69000${Math.floor(10000 + Math.random() * 90000)}`,
        specialty: 'CARDIOLOGY',
        years: 5
    }), {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });

    const patientRec = http.post(`${BASE_URL}/patients`, JSON.stringify({
        patientName: `John`,
        patientPhone: `69000${Math.floor(10000 + Math.random() * 90000)}`,
        email: `john_${Date.now()}@example.com`,
    }), {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });

    const examRec = http.post(`${BASE_URL}/exams`, JSON.stringify({
        examName: `Blood Test`,
        examCategory: "IMAGING",
        examType: "IMAGING",
        maxSlotsPerDay: 10000,
        cost: 52.3,
        doctorID: docRes.json('doctorID'),
        machineType: "MRI",
    }), {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });

    return {
        token: token,
        doctorId: docRes.json('doctorID'),
        patientId: patientRec.json('patientID'),
        examId: examRec.json('examID'),
    };
}

export default function (data) {
    const headers = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.token}`,
        },
    };

    group('Get Appointments List', function () {
        const res = http.get(`${BASE_URL}/appointments`, headers);
        check(res, { 'get appointments status 200': (r) => r.status === 200 });
    });

    sleep(1);

    group('Get Appointments List with filter', function () {
        const res = http.get(`${BASE_URL}/appointments?examID=${data.examId}`, headers);
        check(res, {
            'filtered with examID appointments status 200': (r) => r.status === 200,
        });

        const res2 = http.get(`${BASE_URL}/appointments?patientID=${data.patientId}`, headers);
        check(res2, {
            'filtered with patientID appointments status 200': (r) => r.status === 200,
        });

        const res3 = http.get(`${BASE_URL}/appointments?doctorID=${data.doctorId}`, headers);
        check(res3, {
            'filtered with doctorID appointments status 200': (r) => r.status === 200,
        });

        const res4 = http.get(`${BASE_URL}/appointments?date=2027-12-03`, headers);
        check(res4, {
            'filtered with date appointments status 200': (r) => r.status === 200,
        });
    });

    sleep(1);

    let createdAppointmentId = null;

    group('Create Appointment', function () {
        const newAppointment = JSON.stringify({
            patientID: data.patientId,
            examID: data.examId,
            appointmentDate: "2027-12-03",
            fastResults: true,
        });

        const res = http.post(`${BASE_URL}/appointments`, newAppointment, headers);
        check(res, {
            'create appointment status 200': (r) => r.status === 201
        })

        if (res.status === 200 || res.status === 201) {
            createdAppointmentId = res.json('appointmentID');
        }
    })

    sleep(1);

    group('Delete Appointment', function () {
        if (!createdAppointmentId) {
            return;
        }
        const res = http.del(`${BASE_URL}/appointments/delete/${createdAppointmentId}`, null, headers);
        check(res, {
            'delete appointment status 200 or 204': (r) => r.status === 200 || r.status === 204
        });
    });

    sleep(1);
}