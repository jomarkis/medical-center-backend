import http from 'k6/http';

export const BASE_URL = 'http://localhost:8081/api';

export function login(role = 'ADMIN', pin = 1234) {
    return http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({ role, pin }),
        { headers: { 'Content-Type': 'application/json' } }
    );
}

export function adminSetup() {
    const res = login('ADMIN', 1234);
    return { token: res.json('token') };
}

export function getProfiles(customThresholds = {}) {
    const testType = __ENV.TEST_TYPE || 'smoke';

    const profiles = {
        smoke: {
            vus: 1,
            duration: '15s',
            thresholds: {
                http_req_failed: ['rate<0.01'],
                http_req_duration: ['p(95)<400'],
                ...customThresholds.smoke,
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
                ...customThresholds.load,
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
                ...customThresholds.stress,
            },
        },
    };

    return profiles[testType] || profiles.smoke;
}