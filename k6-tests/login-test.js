import http from 'k6/http';
import { check, sleep } from 'k6';

const testType = __ENV.TEST_TYPE || 'smoke';

const profiles = {
    smoke: {
        vus: 1,
        duration: '15s',
        thresholds: {
            http_req_failed: ['rate<0.01'],
            http_req_duration: ['p(95)<400'],
        },
    },
    load: {
        stages: [
            { duration: '30s', target: 15 },
            { duration: '1m', target: 15 },
            { duration: '15s', target: 0 },
        ],
        thresholds: {
            http_req_failed: ['rate<0.01'],
            http_req_duration: ['p(95)<500'],
        },
    },
    stress: {
        stages: [
            { duration: '30s', target: 20 },
            { duration: '1m', target: 60 },
            { duration: '30s', target: 100 },
            { duration: '30s', target: 0 },
        ],
        thresholds: {
            http_req_failed: ['rate<0.05'],
            http_req_duration: ['p(95)<1000'],
        },
    },
    spike: {
        stages: [
            { duration: '10s', target: 5 },
            { duration: '10s', target: 80 },
            { duration: '40s', target: 80 },
            { duration: '15s', target: 0 },
        ],
        thresholds: {
            http_req_failed: ['rate<0.10'],
            http_req_duration: ['p(95)<1500'],
        },
    },
};

export const options = profiles[testType] || profiles.smoke;

export default function () {
    const baseUrl = 'http://localhost:8081/api';

    // Βήμα 1: Σύνδεση ως Admin
    const loginPayload = JSON.stringify({
        role: 'ADMIN',
        pin: 1234,
    });

    const loginParams = {
        headers: { 'Content-Type': 'application/json' },
    };

    const loginRes = http.post(`${baseUrl}/auth/login`, loginPayload, loginParams);

    const loginSuccess = check(loginRes, {
        'login status 200': (r) => r.status === 200,
        'token received': (r) => r.json('token') !== undefined,
        'role is admin': (r) => r.json('role') === 'ROLE_ADMIN',
    });

    sleep(1);
}