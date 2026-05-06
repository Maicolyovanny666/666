// Clase principal
public class ArreglosEjemplo {

    public static void main(String[] args) {

        // Arreglo de enteros
        int[] x = {2, 4, 6, 7, 15, 13};

        // Arreglo de doubles
        double[] notas = {3.4, 4.5, 2.1};

        // Imprimir arreglos básicos
        System.out.println("Arreglo de enteros:");
        for (int i = 0; i < x.length; i++) {
            System.out.println("x[" + i + "] = " + x[i]);
        }

        System.out.println("\nArreglo de notas:");
        for (int i = 0; i < notas.length; i++) {
            System.out.println("notas[" + i + "] = " + notas[i]);
        }

        //  Arreglo de objetos (ejemplo de la próxima clase)
        Estudiante[] estudiantes = new Estudiante[3];

        estudiantes[0] = new Estudiante("Juan", 3.5);
        estudiantes[1] = new Estudiante("Maria", 4.2);
        estudiantes[2] = new Estudiante("Carlos", 2.8);

        System.out.println("\nArreglo de objetos (Estudiantes):");
        for (Estudiante e : estudiantes) {
            e.mostrarInfo();
        }
    }
}

// Clase objeto
class Estudiante {
    String nombre;
    double nota;

    // Constructor
    public Estudiante(String nombre, double nota) {
        this.nombre = nombre;
        this.nota = nota;
    }

    // Método para mostrar información
    public void mostrarInfo() {
        System.out.println("Nombre: " + nombre + " | Nota: " + nota);
    }
}