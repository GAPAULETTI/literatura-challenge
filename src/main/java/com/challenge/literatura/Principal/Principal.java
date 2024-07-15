package com.challenge.literatura.Principal;

import com.challenge.literatura.model.Autor;
import com.challenge.literatura.model.DatosLibro;
import com.challenge.literatura.model.Libro;
import com.challenge.literatura.model.Resultados;
import com.challenge.literatura.repository.AutorRepository;
import com.challenge.literatura.repository.LibroRepository;
import com.challenge.literatura.service.ConsumoApi;
import com.challenge.literatura.service.ConvertirDatos;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Scanner;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/" ;
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvertirDatos conversor = new ConvertirDatos();

    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;

    }

    private Scanner teclado = new Scanner(System.in);

    public void muestraElMenu() {

        var json = consumoApi.obtenerDatos(URL_BASE);

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                   ########### MENU #################
                    Elija la opcion a traves de su numero:
                    1 - Buscar libro por titulo
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma                                 
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                        buscarLibro();
                        break;
                case 2:
                        listarLibros();
                        break;
                case 3:
                        listarAutores();
                        break;
                case 4:
                        autoresVivosPorPeriodo();
                        break;
                case 5:
                        listarLibrosPorIdioma();
                        break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
                    break;
            }
        }
    }
    private DatosLibro obtenerLibros(){
        var json = consumoApi.obtenerDatos(URL_BASE);
        System.out.println("Ingrese el título del libro: ");
        var tituloLibro = teclado.nextLine();
        json = consumoApi.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(' ', '+'));
        Resultados datosBusqueda = conversor.obtenerDatos(json, Resultados.class);
        return datosBusqueda.libros().get(0);
    }
    private void buscarLibro() {
        try{
            DatosLibro datos = obtenerLibros();
            Libro libro = new Libro(datos);

            Autor autor = autorRepository.findByNombre(datos.autores().get(0).nombre());

            if(autor != null){
                libro.addAutor(autor);
                libro.setAutor(autor);
            }else {
                autorRepository.save(libro.getAutor());
            }

            libroRepository.save(libro);
            System.out.println(libro);
        }catch (IndexOutOfBoundsException e){
            System.out.println("libro no encontrado");
        }catch (Exception e){
            System.out.println("No se puede registrar libro repetido");

        }

    }
    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();
        libros.forEach(System.out::println);

    }
    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }
    private void autoresVivosPorPeriodo() {
        System.out.println("Ingrese el año por el que desea buscar: ");
        var anio = teclado.nextInt();

        List<Autor> autoresVivos = autorRepository.buscarAutoresVivosPorAnio(anio);

        if(autoresVivos.isEmpty()){
            System.out.println("ningun autor vivo encontrado en ese año");
        }else {
            autoresVivos.forEach(System.out::println);
        }

    }
    private void listarLibrosPorIdioma() {
        String opciones = """
                Ingrese el idioma para buscar los libros:
                es - Español
                en - Ingles              
                """;
        System.out.println(opciones);
        var opcion = teclado.nextLine();
        if(opcion.equalsIgnoreCase("es") || opcion.equalsIgnoreCase("en")){
            List<Libro> librosIdioma = libroRepository.findByIdiomaIgnoreCase(opcion);
            if(librosIdioma.isEmpty()){
                System.out.println("no se encontraron libros en el idioma seleccionado");
            }else{
                librosIdioma.forEach(System.out::println);
            }
        }else{
            System.out.println("opcion no valida");
        }

    }









}
