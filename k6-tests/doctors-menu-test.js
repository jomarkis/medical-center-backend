import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { BASE_URL, getProfiles, adminSetup } from './common.js';

export const options = getProfiles({
    smoke: {
        'group_duration{group:::Get Doctors List}': ['p(95)<250'],
        'group_duration{group:::Create Doctor}': ['p(95)<350'],
    },
    load: {
        'group_duration{group:::Get Doctors List}': ['p(95)<400'],
        'group_duration{group:::Create Doctor}': ['p(95)<600'],
    },
    stress: {
        'group_duration{group:::Get Doctors List}': ['p(95)<800'],
        'group_duration{group:::Create Doctor}': ['p(95)<1200'],
    },
});

export const setup = adminSetup;

export default function (data) {
    const headers = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.token}`,
        },
    };

    group('Get Doctors List', function () {
        const res = http.get(`${BASE_URL}/doctors`, headers);
        check(res, { 'get doctors status 200': (r) => r.status === 200 });
    });

    sleep(1);

    group('Create Doctor', function () {
        const uniqueId = `${__VU}_${__ITER}_${Date.now()}`;
        const newDoctor = JSON.stringify({
            doctorName: `Test Patient ${uniqueId}`,
            doctorPhone: `69000${Math.floor(10000 + Math.random() * 90000)}`,
            specialty: 'CARDIOLOGY',
            years: 5,
        });

        const res = http.post(`${BASE_URL}/doctors`, newDoctor, headers);
        check(res, {
            'create doctors status 200': (r) => r.status === 200
        })
    })

    sleep(1);
}