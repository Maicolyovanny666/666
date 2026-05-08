import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// ENUMS
enum Direccion { SUBIENDO, BAJANDO, DETENIDO }
// CLASES DE BOTONES (Herencia y Polimorfismo)
abstract class Boton {
    protected boolean iluminado;
    protected String tipo;

    public Boton(String tipo) {
        this.tipo = tipo;
        this.iluminado = false;
    }

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

// CLASES DE PUERTAS (NUEVO: Herencia implementada)
abstract class Puerta {
    protected boolean abierta;
    protected boolean obstaculoDetectado;
    protected String identificador; // Para saber qué puerta es al imprimir

    public Puerta(String identificador) {
        this.identificador = identificador;
        this.abierta = false;
        this.obstaculoDetectado = false;
    }

    public void abrir() {
        abierta = true;
        System.out.println("[" + identificador + "] abriéndose automáticamente...");
    }

    public void cerrar() {
        if (obstaculoDetectado) {
            System.out.println("ALERTA [" + identificador + "]: Obstáculo detectado por los sensores. Las puertas no se pueden cerrar.");
        } else {
            abierta = false;
            System.out.println("[" + identificador + "] cerrándose automáticamente...");
        }
    }

    public void setObstaculoDetectado(boolean estado) {
        this.obstaculoDetectado = estado;
    }
    
    public boolean isAbierta() {
        return abierta;
    }
}

// Hija 1: Puerta específica para cada piso
class PuertaPiso extends Puerta {
    public PuertaPiso(int numeroPiso) {
        super("Puerta del Piso " + numeroPiso);
    }
}

// Hija 2: Puerta específica de la cabina del ascensor
class PuertaAscensor extends Puerta {
    public PuertaAscensor() {
        super("Puerta del Ascensor");
    }

    // Comportamiento exclusivo: El botón de mantener abierto está dentro del ascensor
    public void mantenerAbierta() {
        this.abierta = true;
        System.out.println("[" + identificador + "] Botón 'Mantener puertas abiertas' presionado. Puertas permanecen abiertas.");
    }
}

// CLASE PISO
class Piso {
    private int numeroPiso;
    private BotonPiso botonSubida;
    private BotonPiso botonBajada;
    private PuertaPiso puertaPiso; // Actualizado para usar la clase hija

    public Piso(int numeroPiso) {
        this.numeroPiso = numeroPiso;
        this.botonSubida = new BotonPiso(Direccion.SUBIENDO);
        this.botonBajada = new BotonPiso(Direccion.BAJANDO);
        this.puertaPiso = new PuertaPiso(numeroPiso); // Instancia la puerta del piso
    }

    public int getNumeroPiso() { return numeroPiso; }
    public BotonPiso getBotonSubida() { return botonSubida; }
    public BotonPiso getBotonBajada() { return botonBajada; }
    public PuertaPiso getPuertaPiso() { return puertaPiso; }
}

// CLASE ASCENSOR
class Ascensor {
    private int pisoActual;
    private Direccion direccionActual;
    private PuertaAscensor puertaAscensor; // Actualizado para usar la clase hija
    private List<Integer> solicitudesSubida;
    private List<Integer> solicitudesBajada;
    private boolean enEmergencia;

    public Ascensor() {
        this.pisoActual = 1; // Inicia en el piso 1
        this.direccionActual = Direccion.DETENIDO;
        this.puertaAscensor = new PuertaAscensor(); // Instancia la puerta del ascensor
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

    public void mover(List<Piso> edificio) {
        if (enEmergencia) return;

        // Lógica de cambio de dirección y optimización
        if (direccionActual == Direccion.DETENIDO) {
            if (!solicitudesSubida.isEmpty()) direccionActual = Direccion.SUBIENDO;
            else if (!solicitudesBajada.isEmpty()) direccionActual = Direccion.BAJANDO;
        }

        if (direccionActual == Direccion.SUBIENDO) {
            if (!solicitudesSubida.isEmpty()) {
                pisoActual = solicitudesSubida.remove(0);
                llegarAPiso(edificio);
            } else {
                direccionActual = solicitudesBajada.isEmpty() ? Direccion.DETENIDO : Direccion.BAJANDO;
            }
        } else if (direccionActual == Direccion.BAJANDO) {
            if (!solicitudesBajada.isEmpty()) {
                pisoActual = solicitudesBajada.remove(0);
                llegarAPiso(edificio);
            } else {
                direccionActual = solicitudesSubida.isEmpty() ? Direccion.DETENIDO : Direccion.SUBIENDO;
            }
        }
    }

    private void llegarAPiso(List<Piso> edificio) {
        System.out.println("\n--- Ascensor ha llegado al piso " + pisoActual + " ---");
        
        // Se abren tanto las puertas del ascensor como las del piso actual
        PuertaPiso puertaDelPisoActual = edificio.get(pisoActual - 1).getPuertaPiso();
        
        puertaAscensor.abrir();
        puertaDelPisoActual.abrir();
        
        // Simulando tiempo de espera y cierre
        puertaAscensor.cerrar();
        puertaDelPisoActual.cerrar();
    }

    public void activarEmergencia() {
        this.enEmergencia = true;
        this.direccionActual = Direccion.DETENIDO;
        System.out.println("ASCENSOR DETENIDO EN EL PISO MÁS CERCANO (" + pisoActual + ").");
        puertaAscensor.abrir(); // Protocolo de emergencia
    }

    public PuertaAscensor getPuertaAscensor() { return puertaAscensor; }
    public Direccion getDireccionActual() { return direccionActual; }
    public boolean isEnEmergencia() { return enEmergencia; }
}
// CLASE SISTEMACONTROL
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
            // Se pasa el edificio para que el ascensor pueda abrir la puerta del piso correspondiente
            ascensor.mover(edificio); 
        }
        System.out.println("\nSimulación finalizada. Ascensor en reposo.");
    }
    
    public Ascensor getAscensor() {
        return ascensor;
    }
}

// CLASE MAIN (Ejecución del Sistema)
public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Control de Ascensores...\n");
        SistemaControl control = new SistemaControl(5);

        //  Simulación de solicitudes normales
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

        //  Simulación de mantener puertas abiertas (Solo en puerta de ascensor)
        System.out.println("\n--- Probando botón de mantener abierto ---");
        control.getAscensor().getPuertaAscensor().mantenerAbierta();

        // Simulación de falla y protocolo de emergencia
        System.out.println("\n--- Probando Protocolo de Emergencia ---");
        control.reportarFalla("Motor de Dirección");
    }
}