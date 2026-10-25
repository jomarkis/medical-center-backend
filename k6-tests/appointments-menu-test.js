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