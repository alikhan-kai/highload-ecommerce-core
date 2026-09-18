import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 500,
    duration: '15s',
};

export default function () {
    const productId = 1;
    const userId = Math.floor(Math.random() * 1000000);
    const idempotencyKey = "k6-" + userId + "-" + Date.now();

    const res = http.post(`http://host.docker.internal:8080/api/orders?userId=${userId}&productId=${productId}`, null, {
        headers: { 'Idempotency-Key': idempotencyKey }
    });
    check(res, {
        'is status 200 (successful purchase)': (r) => r.status === 200,
        'is status 400 (The product is out)': (r) => r.status === 400,
    });

    sleep(0.1);

}