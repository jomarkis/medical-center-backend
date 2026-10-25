import { check, sleep } from 'k6';
import { getProfiles, login } from './common.js';

export const options = getProfiles();

export default function () {
    const res = login('ADMIN', 1234);

    check(res, {
        'login status 200': (r) => r.status === 200,
        'token received': (r) => r.json('token') !== undefined,
        'role is admin': (r) => r.json('role') === 'ROLE_ADMIN',
    });

    sleep(1);
}