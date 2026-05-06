import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ==========================================
// ENUMS
// ==========================================
enum Direccion { SUBIENDO, BAJANDO, DETENIDO }

// ==========================================
// CLASES DE BOTONES (Herencia y Polimorfismo)
// ==========================================
abstract class Boton {
    protected boolean iluminado;
    protected String tipo;

    public Boton(String tipo) {
        this.tipo = tipo;
        this.iluminado = false;
    }

    // Polimorfismo: Acción común para todos los botones
    public void presionar() {
        this.iluminado = true;
        System.out.println("[" + tipo + "] Sonido suave emitido. Botón iluminado.");
    }

    public void apagar() {
        this.iluminado = false;
    }
    
    public boolean isIluminado() {
        return iluminado;
    }
}

class BotonPiso extends Boton {
    private Direccion direccionSolicitada;

    public BotonPiso(Direccion direccion) {
        super("Botón de Piso (" + direccion + ")");
        this.direccionSolicitada = direccion;
    }

    public Direccion getDireccionSolicitada() {
        return direccionSolicitada;
    }
}

class BotonAscensor extends Boton {
    private int pisoDestino;

    public BotonAscensor(int pisoDestino) {
        super("Botón de Ascensor (Piso " + pisoDestino + ")");
        this.pisoDestino = pisoDestino;
    }

    public int getPisoDestino() {
        return pisoDestino;
    }
}

// ==========================================
// CLASE PUERTA
// ==========================================
class Puerta {
    private boolean abierta;
    private boolean obstaculoDetectado;

    public Puerta() {
        this.abierta = false;
        this.obstaculoDetectado = false;
    }

    public void abrir() {
        abierta = true;
        System.out.println("Puertas abriéndose automáticamente...");
    }

    public void cerrar() {
        if (obstaculoDetectado) {
            System.out.println("ALERTA: Obstáculo detectado por los sensores. Las puertas no se pueden cerrar.");
        } else {
            abierta = false;
            System.out.println("Puertas cerrándose automáticamente...");
        }
    }

    public void mantenerAbierta() {
        abierta = true;
        System.out.println("Botón 'Mantener puertas abiertas' presionado. Puertas permanecen abiertas.");
    }

    public void setObstaculoDetectado(boolean estado) {
        this.obstaculoDetectado = estado;
    }
}

// ==========================================
// CLASE PISO
// ==========================================
class Piso {
    private int numeroPiso;
    private BotonPiso botonSubida;
    private BotonPiso botonBajada;
    private Puerta puertaPiso;

    public Piso(int numeroPiso) {
        this.numeroPiso = numeroPiso;
        this.botonSubida = new BotonPiso(Direccion.SUBIENDO);
        this.botonBajada = new BotonPiso(Direccion.BAJANDO);
        this.puertaPiso = new Puerta();
    }

    public int getNumeroPiso() { return numeroPiso; }
    public BotonPiso getBotonSubida() { return botonSubida; }
    public BotonPiso getBotonBajada() { return botonBajada; }
    public Puerta getPuertaPiso() { return puertaPiso; }
}

// ==========================================
// CLASE ASCENSOR
// ==========================================
class Ascensor {
    private int pisoActual;
    private Direccion direccionActual;
    private Puerta puertaAscensor;
    private List<Integer> solicitudesSubida;
    private List<Integer> solicitudesBajada;
    private boolean enEmergencia;

    public Ascensor() {
        this.pisoActual = 1; // Inicia en el piso 1
        this.direccionActual = Direccion.DETENIDO;
        this.puertaAscensor = new Puerta();
        this.solicitudesSubida = new ArrayList<>();
        this.solicitudesBajada = new ArrayList<>();
        this.enEmergencia = false;
    }

    public void solicitarPiso(int piso, Direccion dir) {
        if (dir == Direccion.SUBIENDO && !solicitudesSubida.contains(piso)) {
            solicitudesSubida.add(piso);
            Collections.sort(solicitudesSubida); // Optimiza orden de subida
        } else if (dir == Direccion.BAJANDO && !solicitudesBajada.contains(piso)) {
            solicitudesBajada.add(piso);
            solicitudesBajada.sort(Collections.reverseOrder()); // Optimiza orden de bajada
        }
    }

    public void mover() {
        if (enEmergencia) return;

        // Lógica de cambio de dirección y optimización
        if (direccionActual == Direccion.DETENIDO) {
            if (!solicitudesSubida.isEmpty()) direccionActual = Direccion.SUBIENDO;
            else if (!solicitudesBajada.isEmpty()) direccionActual = Direccion.BAJANDO;
        }

        if (direccionActual == Direccion.SUBIENDO) {
            if (!solicitudesSubida.isEmpty()) {
                pisoActual = solicitudesSubida.remove(0);
                llegarAPiso();
            } else {
                direccionActual = solicitudesBajada.isEmpty() ? Direccion.DETENIDO : Direccion.BAJANDO;
            }
        } else if (direccionActual == Direccion.BAJANDO) {
            if (!solicitudesBajada.isEmpty()) {
                pisoActual = solicitudesBajada.remove(0);
                llegarAPiso();
            } else {
                direccionActual = solicitudesSubida.isEmpty() ? Direccion.DETENIDO : Direccion.SUBIENDO;
            }
        }
    }

    private void llegarAPiso() {
        System.out.println("--- Ascensor ha llegado al piso " + pisoActual + " ---");
        puertaAscensor.abrir();
        // Simulando tiempo de espera
        puertaAscensor.cerrar();
    }

    public void activarEmergencia() {
        this.enEmergencia = true;
        this.direccionActual = Direccion.DETENIDO;
        System.out.println("ASCENSOR DETENIDO EN EL PISO MÁS CERCANO (" + pisoActual + ").");
        puertaAscensor.abrir(); // Protocolo de emergencia
    }

    public Puerta getPuertaAscensor() { return puertaAscensor; }
    public Direccion getDireccionActual() { return direccionActual; }
    public boolean isEnEmergencia() { return enEmergencia; }
}

// ==========================================
// CLASE SISTEMACONTROL
// ==========================================
class SistemaControl {
    private Ascensor ascensor;
    private List<Piso> edificio;

    public SistemaControl(int totalPisos) {
        ascensor = new Ascensor();
        edificio = new ArrayList<>();
        for (int i = 1; i <= totalPisos; i++) {
            edificio.add(new Piso(i));
        }
    }

    public void llamarAscensorDesdePiso(int numeroPiso, Direccion direccion) {
        System.out.println("\nUsuario en piso " + numeroPiso + " solicita ascensor para ir " + direccion);
        Piso piso = edificio.get(numeroPiso - 1);
        if (direccion == Direccion.SUBIENDO) piso.getBotonSubida().presionar();
        else piso.getBotonBajada().presionar();

        ascensor.solicitarPiso(numeroPiso, direccion);
    }

    public void seleccionarPisoDesdeAscensor(int pisoDestino) {
        System.out.println("\nUsuario dentro del ascensor presiona el botón hacia el piso " + pisoDestino);
        BotonAscensor boton = new BotonAscensor(pisoDestino);
        boton.presionar();
        
        // Determinar si sube o baja para optimizar
        Direccion dir = (pisoDestino > ascensor.getDireccionActual().ordinal()) ? Direccion.SUBIENDO : Direccion.BAJANDO;
        ascensor.solicitarPiso(pisoDestino, dir);
    }

    public void reportarFalla(String componente) {
        System.out.println("\n[NOTIFICACIÓN] Falla detectada en: " + componente);
        System.out.println("[NOTIFICACIÓN] Enviando alerta al equipo de mantenimiento...");
        ascensor.activarEmergencia();
    }

    public void simular() {
        while (ascensor.getDireccionActual() != Direccion.DETENIDO && !ascensor.isEnEmergencia()) {
            ascensor.mover();
        }
        System.out.println("\nSimulación finalizada. Ascensor en reposo.");
    }
    
    public Ascensor getAscensor() {
        return ascensor;
    }
}

// ==========================================
// CLASE MAIN (Ejecución del Sistema)
// ==========================================
public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Control de Ascensores...\n");
        SistemaControl control = new SistemaControl(5); // Edificio de 5 pisos

        // 1. Simulación de solicitudes normales
        control.llamarAscensorDesdePiso(3, Direccion.SUBIENDO);
        control.llamarAscensorDesdePiso(5, Direccion.BAJANDO);
        control.seleccionarPisoDesdeAscensor(4);
        
        // Ejecutamos el movimiento
        control.simular();

        // 2. Simulación de sensor de obstáculo en las puertas
        System.out.println("\n--- Probando sensor de puertas ---");
        control.getAscensor().getPuertaAscensor().abrir();
        control.getAscensor().getPuertaAscensor().setObstaculoDetectado(true);
        control.getAscensor().getPuertaAscensor().cerrar(); 

        // 3. Simulación de mantener puertas abiertas
        System.out.println("\n--- Probando botón de mantener abierto ---");
        control.getAscensor().getPuertaAscensor().mantenerAbierta();

        // 4. Simulación de falla y protocolo de emergencia
        System.out.println("\n--- Probando Protocolo de Emergencia ---");
        control.reportarFalla("Motor de Dirección");
    }
}