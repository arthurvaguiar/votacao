import http from 'k6/http';
import exec from 'k6/execution';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';
const JSON_HEADERS = { headers: { 'Content-Type': 'application/json' } };

export const options = {
    scenarios: {
        votacao: {
            executor: 'constant-arrival-rate',
            rate: 150,     // votos por segundo
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 100,
            maxVUs: 300,
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],     // menos de 1% de erro
        http_req_duration: ['p(95)<200'],   // 95% das requisições abaixo de 200 ms
    },
};

export function setup() {
    const pauta = http.post(`${BASE_URL}/api/v1/pautas`,
        JSON.stringify({ titulo: 'Teste de carga' }), JSON_HEADERS).json();

    http.post(`${BASE_URL}/api/v1/pautas/${pauta.id}/sessao`,
        JSON.stringify({ duracaoEmMinutos: 10 }), JSON_HEADERS);

    return { pautaId: pauta.id };
}

export default function (data) {
    const associadoId = `carga-${exec.scenario.iterationInTest}`;
    const voto = Math.random() < 0.5 ? 'Sim' : 'Nao';

    const res = http.post(`${BASE_URL}/api/v1/pautas/${data.pautaId}/votos`,
        JSON.stringify({ associadoId, voto }), JSON_HEADERS);

    check(res, { 'voto registrado (201)': (r) => r.status === 201 });
}

export function teardown(data) {
    const res = http.get(`${BASE_URL}/api/v1/pautas/${data.pautaId}/resultado`);
    console.log(`Resultado: ${res.body}`);
}