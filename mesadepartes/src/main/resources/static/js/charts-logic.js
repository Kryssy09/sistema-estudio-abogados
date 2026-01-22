/**
 * Lógica inicial para gráficos del dashboard y reportes
 */

function initializeCharts() {
    console.log('Inicializando gráficos...');

    // Gráfico de Expedientes por Tipo (Dashboard)
    const dashboardChart = document.getElementById('chartExpedientesTipo');
    if (dashboardChart) {
        // Fetch data from backend
        fetch('/reportes/estadisticas')
            .then(res => res.ok ? res.json() : Promise.reject('Error fetching tipo data'))
            .then(porTipo => {
                const labels = Object.keys(porTipo);
                const data = Object.values(porTipo);
                new Chart(dashboardChart.getContext('2d'), {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Expedientes',
                            data: data,
                            backgroundColor: labels.map((_, i) => `rgba(${42 + i * 40}, ${157 + i * 20}, ${143 + i * 10}, 0.6)`),
                            borderColor: labels.map((_, i) => `rgba(${42 + i * 40}, ${157 + i * 20}, ${143 + i * 10}, 1)`),
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: { beginAtZero: true, ticks: { color: 'rgba(255,255,255,0.7)' } },
                            x: { ticks: { color: 'rgba(255,255,255,0.7)' } }
                        },
                        plugins: { legend: { labels: { color: 'rgba(255,255,255,0.7)' } } }
                    }
                });
            })
            .catch(err => {
                console.error(err);
                // Fallback to empty chart
                new Chart(dashboardChart.getContext('2d'), { type: 'bar', data: { labels: [], datasets: [] } });
            });
    }
    // Gráfico de Distribución por Estado (Dashboard)
    const estadoChart = document.getElementById('chartExpedientesEstado');
    if (estadoChart) {
        fetch('/reportes/estadisticas/estado')
            .then(res => res.ok ? res.json() : Promise.reject('Error fetching estado data'))
            .then(porEstado => {
                const labels = Object.keys(porEstado);
                const data = Object.values(porEstado);
                new Chart(estadoChart.getContext('2d'), {
                    type: 'doughnut',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: data,
                            backgroundColor: labels.map((_, i) => `rgba(${40 + i * 30}, ${167 + i * 10}, ${69 + i * 5}, 0.8)`),
                            borderColor: labels.map((_, i) => `rgba(${40 + i * 30}, ${167 + i * 10}, ${69 + i * 5}, 1)`),
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'right',
                                labels: {
                                    color: 'rgba(255, 255, 255, 0.7)',
                                    padding: 15
                                }
                            }
                        }
                    }
                });
            })
            .catch(err => {
                console.error(err);
                new Chart(estadoChart.getContext('2d'), { type: 'doughnut', data: { labels: [], datasets: [] } });
            });
    }
    // Gráfico de Reportes - COMENTADO porque script.js maneja esta gráfica con datos reales
    /*
    const reportChart = document.getElementById('reportChart');
    if (reportChart) {
        new Chart(reportChart.getContext('2d'), {
            type: 'line',
            data: {
                labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio'],
                datasets: [{
                    label: 'Conciliación',
                    data: [12, 19, 15, 25, 22, 30],
                    borderColor: 'rgba(54, 162, 235, 1)',
                    backgroundColor: 'rgba(54, 162, 235, 0.1)',
                    tension: 0.4
                }, {
                    label: 'Patrocinio Legal',
                    data: [8, 12, 10, 14, 18, 16],
                    borderColor: 'rgba(255, 99, 132, 1)',
                    backgroundColor: 'rgba(255, 99, 132, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(255, 255, 255, 0.1)'
                        },
                        ticks: {
                            color: 'rgba(255, 255, 255, 0.7)'
                        }
                    },
                    x: {
                        grid: {
                            color: 'rgba(255, 255, 255, 0.1)'
                        },
                        ticks: {
                            color: 'rgba(255, 255, 255, 0.7)'
                        }
                    }
                },
                plugins: {
                    legend: {
                        labels: {
                            color: 'rgba(255, 255, 255, 0.7)'
                        }
                    }
                }
            }
        });
    }
    */
}

// Función utilitaria para mostrar notificaciones toast
function showToast(message, type = 'info') {
    const id = 'toast-' + Date.now();
    const wrapper = document.getElementById('toasts-wrapper');
    if (!wrapper) return;

    const bgClass = {
        'success': 'text-bg-success',
        'danger': 'text-bg-danger',
        'warning': 'text-bg-warning',
        'info': 'text-bg-info'
    }[type] || 'text-bg-secondary';

    const html = `
      <div id="${id}" class="toast align-items-center ${bgClass} border-0 mb-2" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
          <div class="toast-body">${message}</div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
        </div>
      </div>`;

    wrapper.insertAdjacentHTML('beforeend', html);
    const toastEl = document.getElementById(id);
    const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
    toast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
}

// Inicialización legacy para compatibilidad
// NOTA: Esta función está comentada porque script.js ya maneja la inicialización de reportChart
// para evitar el error "Canvas is already in use"
/*
document.addEventListener("DOMContentLoaded", async () => {
    const reportChartCtx = document.getElementById("reportChart")?.getContext("2d");
    let reportChart = null;

    const renderReportChart = async () => {
        if (!reportChartCtx) return;

        if (reportChart) {
            reportChart.destroy();
        }

        try {
            // Obtener datos reales del backend
            const response = await fetch('/reportes/estadisticas');
            if (!response.ok) {
                throw new Error('Error al obtener estadísticas');
            }

            const tipos = await response.json();

            // Verificar si hay datos
            if (Object.keys(tipos).length === 0) {
                console.log('No hay datos de expedientes');
                // Mostrar mensaje en el canvas
                reportChartCtx.font = '16px Arial';
                reportChartCtx.fillStyle = '#fff';
                reportChartCtx.textAlign = 'center';
                reportChartCtx.fillText('No hay datos disponibles', reportChartCtx.canvas.width / 2, reportChartCtx.canvas.height / 2);
                return;
            }

            const data = {
                labels: Object.keys(tipos),
                datasets: [{
                    label: 'Número de Expedientes',
                    data: Object.values(tipos),
                    backgroundColor: [
                        'rgba(42, 157, 143, 0.6)',
                        'rgba(233, 196, 106, 0.6)',
                        'rgba(231, 111, 81, 0.6)',
                        'rgba(54, 162, 235, 0.6)',
                        'rgba(255, 99, 132, 0.6)',
                    ],
                    borderColor: [
                        'rgba(42, 157, 143, 1)',
                        'rgba(233, 196, 106, 1)',
                        'rgba(231, 111, 81, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 99, 132, 1)',
                    ],
                    borderWidth: 1
                }]
            };

            reportChart = new Chart(reportChartCtx, {
                type: 'bar',
                data: data,
                options: {
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { color: '#fff', stepSize: 1 }
                        },
                        x: {
                            ticks: { color: '#fff' }
                        }
                    },
                    plugins: {
                        legend: {
                            labels: {
                                color: '#fff'
                            }
                        }
                    },
                    responsive: true,
                    maintainAspectRatio: false
                }
            });

        } catch (error) {
            console.error('Error al cargar estadísticas:', error);
            // Mostrar mensaje de error en el canvas
            reportChartCtx.font = '16px Arial';
            reportChartCtx.fillStyle = '#ff6b6b';
            reportChartCtx.textAlign = 'center';
            reportChartCtx.fillText('Error al cargar datos', reportChartCtx.canvas.width / 2, reportChartCtx.canvas.height / 2);
        }
    };

    if (document.querySelector("#reportChart")) {
        await renderReportChart();
    }
});
*/
