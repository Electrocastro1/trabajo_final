# Criterios de Decisión: Estructuras de Datos en la Red de Sensores

Este documento es la parte más importante de tu entrega. No basta con que el código funcione; debes justificar **por qué** elegiste una colección de Java sobre otra.

Responde a las siguientes preguntas basándote en la teoría de nodos y el rendimiento del JCF.

### 1. El dilema del Historial
Para el `historialEventos`, podrías usar un `ArrayList` o un `LinkedList`.
- Si la plataforma registra miles de eventos por segundo y necesitas eliminar los más antiguos frecuentemente para ahorrar memoria, ¿cuál de las dos colecciones es más eficiente y por qué? (Pista: piensa en el costo de eliminar el primer elemento).

**Respuesta:**
La colección más eficiente para este escenario es `LinkedList`. En un `ArrayList`, los datos se guardan en bloques continuos; si eliminas el primer elemento (`remove(0)`), el sistema tiene que desplazar todos los demás elementos una posición hacia la izquierda para llenar el hueco, lo cual tiene un costo computacional muy alto de O(n). En un `LinkedList`, los elementos son nodos independientes, por lo que eliminar el más antiguo en la primera posición es una operación de tiempo constante O(1) que solo requiere romper el enlace del primer nodo y hacer que la cabeza apunte al segundo, sin reubicar el resto de los datos.

### 2. Navegación Bidireccional
En el `navegadorAlertas`, utilizamos un `Deque` (Double Ended Queue).
- ¿Qué ventaja ofrece un `Deque` sobre una `Queue` simple para implementar la funcionalidad de "Atrás" y "Adelante"?
- ¿Cómo se relaciona esto con el concepto de un `NodoDoble`?

**Respuesta:**
La principal ventaja del `Deque` radica en que expone operaciones en ambos extremos de la fila (`addFirst`, `addLast`, `pollFirst`, `pollLast`), a diferencia de una `Queue` clásica que es estrictamente FIFO (entra por atrás, sale por el frente). Esto es indispensable para extraer un elemento de un extremo y reinsertarlo en el opuesto, logrando el efecto de navegación hacia "Atrás" y hacia "Adelante". Esto materializa la teoría del `NodoDoble`, donde cada nodo tiene un puntero al nodo `siguiente` y otro al `anterior`, siendo precisamente esa estructura bidireccional la que permite recorrer y manipular los datos físicamente en ambas direcciones.

### 3. El Ciclo de Monitoreo
Para la `colaMonitoreo`, implementaste una rotación (sacar del frente y meter al final).
- ¿Por qué una `Queue` es la estructura natural para este problema?
- ¿Qué pasaría con la equidad del sistema si usaras una `Stack` (Pila) en su lugar?

**Respuesta:**
La `Queue` es la estructura natural porque implementa el principio FIFO (First In, First Out), garantizando un ciclo equitativo tipo "Round-Robin" donde el sensor que lleva más tiempo esperando es obligatoriamente el siguiente en ser consultado. Si se usara un `Stack` (Pila), el sistema se basaría en el principio LIFO (Last In, First Out). Esto destruiría la equidad del sistema, provocando un problema de "inanición" (starvation): los últimos sensores en programarse serían consultados de inmediato, y los que quedaron en el fondo de la pila jamás serían atendidos si llegan solicitudes nuevas constantemente.

### 4. Topología de Red (El Grafo)
Representaste la red de sensores como un `Map<String, List<String>>`.
- ¿Por qué usamos un `Map` y no un arreglo bidimensional (matriz de adyacencia) considerando que la mayoría de los sensores solo están conectados a 2 o 3 vecinos?
- ¿Cómo se traduce esta estructura de `Map` a la idea de un `NodoGrafo` con una lista de adyacencias?

**Respuesta:**
Se usa un `Map` para evitar el desperdicio masivo de memoria que generaría una matriz dispersa. En una red de 1,000 sensores, una matriz de adyacencia requeriría 1,000,000 de celdas; si cada sensor solo se conecta a 3 vecinos, casi todas las celdas estarían vacías. El `Map` optimiza el espacio almacenando únicamente las conexiones reales. Teóricamente, esto corresponde a la idea de un `NodoGrafo`: la llave (`String`) representa el identificador único del nodo, y el valor (`List<String>`) almacena de forma compacta sus aristas o lista de adyacencias reales.

### 5. Buffer de Ingesta y Rendimiento
El `bufferIngesta` procesa datos en orden FIFO.
- Si el volumen de datos aumenta drásticamente y necesitas procesar las lecturas en paralelo usando múltiples hilos, ¿seguiría siendo adecuado un `ArrayDeque` o deberías cambiar a una `ConcurrentLinkedQueue`? Justifica tu respuesta basándote en la seguridad de hilos (Thread-safety).

**Respuesta:**
Sería obligatorio cambiar a una `ConcurrentLinkedQueue`. Las implementaciones estándar como `ArrayDeque` no son Thread-Safe (seguras para hilos). Si múltiples hilos intentan extraer o insertar lecturas simultáneamente en un `ArrayDeque`, la estructura colapsará, sobreescribirá lecturas críticas o lanzará una excepción de modificación concurrente (`ConcurrentModificationException`). `ConcurrentLinkedQueue` está diseñada con algoritmos no bloqueantes que permiten a múltiples hilos operar sobre el buffer al mismo tiempo sin corromper la memoria, siendo la opción correcta y segura para un procesamiento IoT masivo en paralelo.