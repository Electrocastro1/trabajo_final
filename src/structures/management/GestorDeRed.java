package structures.management;

import model.LecturaSensor;
import java.util.*;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

/**
 * GestorDeRed - Módulo Integrador de Estructuras de Datos
 * VERSION ESTUDIANTE (COMPLETADA)
 *
 * Objetivo: Implementar la gestión de la red utilizando las colecciones de Java (JCF).
 * El éxito de este módulo no es solo que el código compile, sino que elijas
 * la estructura correcta para cada problema.
 */
public class GestorDeRed {
    // 1. Historial de Eventos: Registro lineal de todas las actividades
    private List<String> historialEventos;

    // 2. Navegador de Alertas: Permite moverse adelante y atrás entre alertas
    private Deque<String> navegadorAlertas;
    private String alertaActual;

    // 3. Ciclo de Monitoreo: Gestión de la rotación de sensores a consultar
    private Queue<String> colaMonitoreo;

    // 4. Topología de la Red: Grafo de conexiones entre estaciones (Lista de adyacencia)
    private Map<String, List<String>> topologiaRed;

    // 5. Buffer de Ingesta: Cola de espera para procesar lecturas entrantes
    private Queue<LecturaSensor> bufferIngesta;

    public GestorDeRed() {
        this.historialEventos = new ArrayList<>();
        this.navegadorAlertas = new ArrayDeque<>();
        this.colaMonitoreo = new ArrayDeque<>();
        this.topologiaRed = new HashMap<>();
        this.bufferIngesta = new ArrayDeque<>();
    }

    // =========================================================================
    // SECCIÓN 1: HISTORIAL Y EVENTOS
    // =========================================================================

    public void agregarEvento(String evento) {
        // TODO 1: Implementar la adición del evento al historial.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String registro = "[" + sdf.format(new Date()) + "] " + evento;
        historialEventos.add(registro);
    }

    public List<String> obtenerEventosRecientes(int limite) {
        if (historialEventos.size() <= limite) return new ArrayList<>(historialEventos);
        return historialEventos.subList(historialEventos.size() - limite, historialEventos.size());
    }

    public void limpiarHistorialAntiguo(int mantener) {
        // TODO 10: Implementar la optimización del historial.
        while (historialEventos.size() > mantener) {
            historialEventos.remove(0); // Elimina constantemente el elemento más antiguo de la List
        }
    }

    // =========================================================================
    // SECCIÓN 2: NAVEGACIÓN DE ALERTAS
    // =========================================================================

    public void registrarAlerta(String alerta) {
        navegadorAlertas.addLast(alerta);
        alertaActual = alerta;
    }

    public String navegarAlSiguiente() {
        // TODO 2: Implementar la lógica de desplazamiento hacia adelante.
        if (navegadorAlertas.isEmpty()) return null;

        String alerta = navegadorAlertas.pollFirst(); // Extrae del frente
        navegadorAlertas.addLast(alerta);             // Inserta al final (ciclo infinito)
        alertaActual = alerta;                        // Actualiza estado

        return alertaActual;
    }

    public String navegarAlAnterior() {
        // TODO 3: Implementar la lógica de desplazamiento hacia atrás.
        if (navegadorAlertas.isEmpty()) return null;

        String alerta = navegadorAlertas.pollLast();  // Extrae del final
        navegadorAlertas.addFirst(alerta);            // Inserta al frente (ciclo inverso)
        alertaActual = alerta;                        // Actualiza estado

        return alertaActual;
    }

    public String getAlertaActual() { return alertaActual; }

    // =========================================================================
    // SECCIÓN 3: CICLO DE MONITOREO
    // =========================================================================

    public void programarConsulta(String idSensor) {
        // TODO 4: Implementar la programación de la consulta.
        if (!colaMonitoreo.contains(idSensor)) {
            colaMonitoreo.add(idSensor);
        }
    }

    public String obtenerSiguienteSensor() {
        // TODO 5: Implementar la rotación del ciclo.
        if (colaMonitoreo.isEmpty()) return null;

        String sensor = colaMonitoreo.poll(); // Saca el primero (FIFO)
        colaMonitoreo.add(sensor);            // Lo devuelve al final de la fila

        return sensor;
    }

    // =========================================================================
    // SECCIÓN 4: TOPOLOGÍA DE RED
    // =========================================================================

    public void registrarConexion(String sensorA, String sensorB) {
        // TODO 6: Implementar la conexión bidireccional entre dos sensores.
        // computeIfAbsent crea la lista automáticamente si el sensor no existía.
        List<String> vecinosA = topologiaRed.computeIfAbsent(sensorA, k -> new ArrayList<>());
        if (!vecinosA.contains(sensorB)) {
            vecinosA.add(sensorB);
        }

        List<String> vecinosB = topologiaRed.computeIfAbsent(sensorB, k -> new ArrayList<>());
        if (!vecinosB.contains(sensorA)) {
            vecinosB.add(sensorA);
        }
    }

    public List<String> obtenerVecinos(String idSensor) {
        // TODO 7: Implementar la consulta de conexiones.
        // getOrDefault evita excepciones retornando lista vacía si la clave no existe.
        return topologiaRed.getOrDefault(idSensor, new ArrayList<>());
    }

    public List<String> detectarEstacionesAisladas() {
        // TODO 9: Implementar la detección de nodos huérfanos.
        return topologiaRed.entrySet().stream()
                .filter(entrada -> entrada.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // SECCIÓN 5: BUFFER DE INGESTA
    // =========================================================================

    public void recibirLectura(LecturaSensor lectura) {
        bufferIngesta.add(lectura);
    }

    public LecturaSensor procesarSiguienteLectura() {
        // TODO 8: Implementar la extracción del buffer.
        // poll() extrae y retorna la lectura más antigua, devolviendo null si está vacío.
        return bufferIngesta.poll();
    }

    public List<LecturaSensor> filtrarAlertasCriticas(double limitePm25) {
        // TODO EXTRA: Usar Streams para filtrar lecturas con PM2.5 superior al límite.
        return bufferIngesta.stream()
                .filter(lectura -> lectura.getPm25() > limitePm25)
                .collect(Collectors.toList());
    }
}