import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 500,
    duration: '15s',
};

// Выполняется один раз перед началом теста для получения токена
export function setup() {
    const res = http.post('http://host.docker.internal:8080/api/auth/login?userId=999');
    return { token: res.body }; // передаем токен всем виртуальным юзерам
}

export default function (data) {
    const productId = 1;
    const userId = Math.floor(Math.random() * 1000000);
    const idempotencyKey = "k6-" + userId + "-" + Date.now();

    const res = http.post(`http://host.docker.internal:8080/api/orders?productId=${productId}`, null, {
        headers: { 
            'Idempotency-Key': idempotencyKey,
            'Authorization': `Bearer ${data.token}`
        }
    });
    check(res, {
        'is status 200 (successful purchase)': (r) => r.status === 200,
        'is status 400 (The product is out)': (r) => r.status === 400,
    });

    sleep(0.1);

}