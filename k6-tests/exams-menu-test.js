import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { BASE_URL, getProfiles, login } from './common.js';

export const options = getProfiles({
    smoke: {
        'group_duration{group:::Get Exams List}': ['p(95)<250'],
        'group_duration{group:::Get Exams List with filter}': ['p(95)<300'],
        'group_duration{group:::Create Exam}': ['p(95)<350'],

    },
    load: {
        'group_duration{group:::Get Exams List}': ['p(95)<400'],
        'group_duration{group:::Get Exams List with filter}': ['p(95)<500'],
        'group_duration{group:::Create Exam}': ['p(95)<600'],
    },
    stress: {
        'group_duration{group:::Get Exams List}': ['p(95)<800'],
        'group_duration{group:::Get Exams List with filter}': ['p(95)<1000'],
        'group_duration{group:::Create Exam}': ['p(95)<1200'],
    },
});

export function setup() {
    const loginRes = login('ADMIN', 1234);
    const token = loginRes.json('token');

    const docRes = http.post(`${BASE_URL}/doctors`, JSON.stringify({
        doctorName: `Dr. Fixture ${Date.now()}`,
        doctorPhone: `69000${Math.floor(10000 + Math.random() * 90000)}`,
        specialty: 'CARDIOLOGY',
        years: 5
    }), {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });

    // ΜΙΑ γραμμή: παίρνεις το ID όποιο κι αν είναι
    return {
        token: token,
        doctorId: docRes.json('doctorID')
    };
}

export default function (data) {
    const headers = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.token}`,
        },
    };

    group('Get Exams List', function () {
        const res = http.get(`${BASE_URL}/exams`, headers);
        check(res, { 'get exams status 200': (r) => r.status === 200 });
    });

    sleep(1);

    group('Get Exams List with filter', function () {
        const res = http.get(`${BASE_URL}/exams?doctorID=${data.doctorId}`, headers);
        check(res, {
            'filtered exams status 200': (r) => r.status === 200,
        });
    });

    sleep(1);

    group('Create Exam', function () {
        const uniqueId = `${__VU}_${__ITER}_${Date.now()}`;
        const newExam = JSON.stringify({
            examName: `Blood Test ${uniqueId}`,
            examCategory: "IMAGING",
            examType: "IMAGING",
            maxSlotsPerDay: 3,
            cost: 52.3,
            doctorID: data.doctorId,
            machineType: "MRI",
        });

        const res = http.post(`${BASE_URL}/exams`, newExam, headers);
        check(res, {
            'create exam status 200': (r) => r.status === 200
        })
    })

    sleep(1);
}
