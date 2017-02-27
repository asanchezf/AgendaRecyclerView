package com.antonioejemplos.agendarecyclerview;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import Beans.Contactos;
import controlador.SQLControlador;

import static android.R.attr.id;
import static android.widget.SearchView.OnQueryTextListener;

public class MainActivity extends AppCompatActivity implements AdaptadorRecyclerViewSearch.OnItemClickListener, OnQueryTextListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {


    //CONSTANTES PARA EL MODO FORMULARIO Y PARA LOS TIPOS DE LLAMADA.============================
    public static final String C_MODO = "modo";
    public static final int C_VISUALIZAR = 551;
    public static final int C_CREAR = 552;
    public static final int C_EDITAR = 553;
    public static final int C_ELIMINAR = 554;
    private static final int SOLICITUD_ACCESS_READ_CONTACTS = 1;//Para control de permisos en Android M o superior e importar contactos
    private static final int SOLICITUD_ACCESS_CALL_PHONE = 2;//Para control de permisos en Android M o superior y poder realizar llamadas
    //FIN CONSTANTES==============================================================================

    private RecyclerView lista;

    //private AdaptadorRecyclerView3 adaptador;
    private AdaptadorRecyclerViewSearch adaptadorBuscador;
    private FloatingActionButton btnFab;
    private CollapsingToolbarLayout ctlLayout;
    private SQLControlador dbConnection;//CONTIENE LAS CONEXIONES A BBDD (CREADA EN DBHELPER.CLASS) Y LOS M�TODOS INSERT, UPDATE, DELETE, BUSCAR....
    private ArrayList<Contactos> contactos;

    private static long back_pressed;//Contador para cerrar la app al pulsar dos veces seguidas el btón de cerrar. Se gestiona en el evento onBackPressed

    private static int index = -1;
    private static int top = -1;
    private LinearLayoutManager llmanager;
    private SearchView searchView;
    private int id_Contacto_Llamada=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Mi Aplicación");

        // Configuración del RecyclerView-----------------------------
        lista = (RecyclerView) findViewById(R.id.lstLista);
        lista.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));//Layout para el RecyclerView

        lista.setItemAnimator(new DefaultItemAnimator());//Animación por defecto....
        llmanager = new LinearLayoutManager(this);
        llmanager.setOrientation(LinearLayoutManager.VERTICAL);


        consultar();

        //adaptador = new AdaptadorRecyclerView3(contactos, this, this);//IMplementa el adapatador: pasamos ahora tres parámetros....
        //lista.setAdapter(adaptador);
        //adaptador.notifyDataSetChanged();


        //Floating Action Button
        btnFab = (FloatingActionButton) findViewById(R.id.btnFab);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                crear();

            }
        });

        ctlLayout = (CollapsingToolbarLayout) findViewById(R.id.ctlLayout);
        ctlLayout.setTitle("Mi Agenda");

    }


    @Override
    protected void onRestart() {
            /*
    		 *  Indica que la actividad va a volver a ser representada despu�s de haber pasado por onStop().*/

        super.onRestart();
        //Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();

        consultar();

    }


    @Override
    protected void onResume() {
        super.onResume();

        //Para preserver el scroll del listView
        //Establecer variables en onCreate (), guardar posición en onPause () y ajuste la posición de desplazamiento desplazarse en onResume ()
        if (index != -1) {
            llmanager.scrollToPositionWithOffset(index, top);
        }


    }

    @Override
    protected void onPause() {

//    	 Indica que la actividad est� a punto de ser lanzada a segundo plano, normalmente porque otra actividad es lanzada.
//    	 Es el lugar adecuado para detener animaciones, m�sica o almacenar los datos que estaban en edici�n.


        //Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();


        //Para preserver el scroll del Recyclerview;
        //Establecer variables en onCreate (), guardar posición en onPause () y ajuste la posición de desplazamiento desplazarse en onResume ()

        index = llmanager.findFirstVisibleItemPosition();
        View v = lista.getChildAt(0);
        //top = (v == null) ? 0 : (v.getTop() - lista.getPaddingTop());

        if (v == null) {
            top = 0;
        } else {
            top = v.getTop() - lista.getPaddingTop();
        }
    }

    private void consultar() {
        //ES EL PRIMER MÉTODO LLAMADO QUE ACCEDE A LA BB.DD DONDE SE ENCUENTRAN LOS REGISTROS.
        //SI LA BB.DD NO EXISTE SE CREARÁ. SI YA EXISTE LA DEVUELVE SEGÚN EL MODO EN QUE LLAMEMOS: EXCRITURA O LECTURA.
        //AL INSTALAR LA APP ES AQUÍ DONDE REALMENTE SE CREA PQ LA CLASE DBhelper QUE ES LA ENCARGADA DE CREAR LA BB.DD
        //SE INSTANCIA DESDE LA CLASE SQLcontrolador DISTINGUIENDO SI LLAMA A ONCREATE O A ONUPGRADE.. PARA GESTIONAR LAS
        //VERSIONES DE LA bb.dd.

        dbConnection = new SQLControlador(getApplicationContext());
        try {
            dbConnection.abrirBaseDeDatos(1);//Modo lectura
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// Lectura. Solo para ver


        contactos = dbConnection.BuscarTodos();// llamamos a BuscarTodos() que devuelve un arraylist de contactos...


        //adaptador = new AdaptadorRecyclerView3(contactos, this, this);//IMplementa el adapatador: pasamos ahora tres parámetros....
        adaptadorBuscador = new AdaptadorRecyclerViewSearch(contactos, this, this);//IMplementa el adapatador: pasamos ahora tres parámetros....

        /*lista.setAdapter(adaptador);
        adaptador.notifyDataSetChanged();*/

        //DECORACIÓN Y ANIMACIÓN DEL RECYCLERVIEWw. Se define en una clase aparte...

        /*lista.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        lista.setItemAnimator(new DefaultItemAnimator());*/


        //int total=contactos.size();

        //lista.setAdapter( new ContactosAdapter_Imagenes(this,contactos));

        //concatena_numero_registros=getResources().getString(R.string.concatenar_numero_registros);//DEvuelve HAY para concatenar y  mostrar el número total de registros...
        //totalRegistros=  contactosAdapter_imagenes.getCount();
        //adaptador.getItemCount();

        //totales= String.valueOf(totalRegistros);


//        if(totalRegistros>0) {
//            txtTotales.setText(concatena_numero_registros+ " " + totales + " " + getResources().getString(R.string.titulo_activity_lista));
//        }
//
//        else{
//            txtTotales.setText(getResources().getString(R.string.no_hay_registros));
//        }

        //lista.setAdapter(contactosAdapter_imagenes);

        lista.setAdapter(adaptadorBuscador);


        dbConnection.cerrar();
    }


    //Método que realiza la llamada telefónica.
    public void call(long id) throws SQLException {

        dbConnection = new SQLControlador(getApplicationContext());
        dbConnection.abrirBaseDeDatos(1);// Lectura. Solo para ver
        Cursor c = dbConnection.CursorBuscarUno(id);// Devuelve un Cursor


        String telefono = c.getString(c.getColumnIndex("Telefono"));

        dbConnection.cerrar();


        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telefono));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //View v = null;
          /*  Snackbar snack = Snackbar.make(view, R.string.agenda_permiso_llamadas, Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(getResources().getColor(R.color.md_deep_orange_700));
            snack.show();*/
            return;
        }
        startActivity(intent);

    }


    @Override
    public void onClick(RecyclerView.ViewHolder holder, final int idPromocion, final View view) {//idPromocion y View se definen como final pq son llamada desde la clase interna del evento onclick() del AlertDialog
 /*
    * onClick es un método obligado a implementarse pq MainActivity implements AdaptadorRecyclerViewSearchView que cuenta con la
    * interface OnItemClickListener
    * */
        Log.i("Demo Recycler", "Se ha pulsado en la siguiente view: " + holder);

        //Pulsando en el icono de la categoría...
        if (view.getId() == R.id.category) {
            //Toast.makeText(MainActivity.this, "Se ha pulsado en categoría: " + idPromocion + " " + holder, Toast.LENGTH_SHORT).show();
        }

        //Pulsando en en el btn de llamar se abre el dialer para llamar al contacto seleccionado
        else if (view.getId() == R.id.btncontactar) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permisosPorAplicacion(idPromocion,2);
                //solicitarPermisoLlamadas(idPromocion,view);
                //Toast.makeText(view.getContext(),"Hola",Toast.LENGTH_LONG).show();
                return;
            }


            AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

            dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
            dialogEliminar.setTitle(getResources().getString(
                    R.string.agenda_call_titulo));
            dialogEliminar.setMessage(getResources().getString(
                    R.string.agenda_call_mensaje));
            dialogEliminar.setCancelable(false);

            dialogEliminar.setPositiveButton(
                    getResources().getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int boton) {

                            try {
                                call(idPromocion);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }
                    });

            dialogEliminar.setNegativeButton(android.R.string.no, null);

            dialogEliminar.show();


        }//Fin else if

        else if (view.getId() == R.id.txtubicacion) {
            //
            // Toast.makeText(MainActivity.this, "Se ha pulsado en ubicación: " + idPromocion + " " + holder, Toast.LENGTH_SHORT).show();

            //NO FUNCIONA
            /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
            startActivity(intent);*/


            //INICIA NAVEGACIÓN DESDE LA UBICACIÓN ACTUAL A LA DIRECCIÓN INTRODUCIDA....
       /*     Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=an+Mestizaje, 2+Alcorcon"));
            startActivity(intent);*/

            try {
                ubicar(idPromocion);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        } else if (view.getId() == R.id.txtruta) {
            //
            // Toast.makeText(MainActivity.this, "Se ha pulsado en ubicación: " + idPromocion + " " + holder, Toast.LENGTH_SHORT).show();

            //NO FUNCIONA
            /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
            startActivity(intent);*/


            //INICIA NAVEGACIÓN DESDE LA UBICACIÓN ACTUAL A LA DIRECCIÓN INTRODUCIDA....
       /*     Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=an+Mestizaje, 2+Alcorcon"));
            startActivity(intent);*/

            try {
                visitar(idPromocion);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }


        //Pulsando en otra parte de los CardView distinta se abre la Activity para editar o eliminar el contacto
        else {
            //Toast.makeText(MainActivity.this, "Se ha pulsado en la celda: " + idPromocion + " " + holder, Toast.LENGTH_SHORT).show();
            try {
                editar(idPromocion);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void visitar(int idPromocion) throws SQLException {

        dbConnection = new SQLControlador(getApplicationContext());
        dbConnection.abrirBaseDeDatos(1);// Lectura. Solo para ver

        Cursor c = dbConnection.CursorBuscarUno(idPromocion);// Devuelve un Cursor

        String direccion = c.getString(c.getColumnIndex("Direccion"));

        dbConnection.cerrar();

        if (direccion.equals("")) {

            //Toast.makeText(MainActivity.this,"Este contacto no tiene ninguna dirección asignada..!",Toast.LENGTH_LONG).show();

            Snackbar snack = Snackbar.make(lista, R.string.agenda_contacto_sin_direccion, Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(getResources().getColor(R.color.md_deep_orange_500));
            snack.show();


        } else {


            //Si el GPS no está habilitado
            LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Snackbar snack = Snackbar.make(lista, R.string.agenda_gps_no_activado, Snackbar.LENGTH_LONG);
                ViewGroup group = (ViewGroup) snack.getView();
                group.setBackgroundColor(getResources().getColor(R.color.md_deep_orange_500));
                snack.show();
            } else {
                //Uri.parse("google.navigation:q=an+Mestizaje, 2+Alcorcon"));

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + direccion));
                startActivity(intent);
            }

        }
    }


    private void ubicar(int idPromocion) throws SQLException {

        dbConnection = new SQLControlador(getApplicationContext());
        dbConnection.abrirBaseDeDatos(1);// Lectura. Solo para ver

        Cursor c = dbConnection.CursorBuscarUno(idPromocion);// Devuelve un Cursor

        String direccion = c.getString(c.getColumnIndex("Direccion"));

        dbConnection.cerrar();

        if (direccion.equals("")) {

            //Toast.makeText(MainActivity.this,"Este contacto no tiene ninguna dirección asignada..!",Toast.LENGTH_LONG).show();

            Snackbar snack = Snackbar.make(lista, R.string.agenda_contacto_sin_direccion, Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(getResources().getColor(R.color.md_deep_orange_500));
            snack.show();

        } else {

            //Si el GPS no está habilitado
            LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Snackbar snack = Snackbar.make(lista, R.string.agenda_gps_no_activado, Snackbar.LENGTH_LONG);
                ViewGroup group = (ViewGroup) snack.getView();
                group.setBackgroundColor(getResources().getColor(R.color.md_deep_orange_500));
                snack.show();
            }

            //El GPS está habilitado y el contacto tiene dirección asociada
            else {

                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + direccion);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }
        }
    }


    private void crear() {
        Intent i = new Intent(MainActivity.this, AltaUsuarios.class);
        i.putExtra(C_MODO, C_CREAR);
        startActivityForResult(i, C_CREAR);
    }

    private void editar(long id) throws SQLException {
        dbConnection = new SQLControlador(getApplicationContext());
        dbConnection.abrirBaseDeDatos(1);// Lectura. Solo para ver

        Cursor c = dbConnection.CursorBuscarUno(id);// Devuelve un Cursor

        int idenviado = c.getInt(c.getColumnIndex("_id"));
        String nombre = c.getString(c.getColumnIndex("Nombre"));
        String apellidos = c.getString(c.getColumnIndex("Apellidos"));
        String direccion = c.getString(c.getColumnIndex("Direccion"));
        String telefono = c.getString(c.getColumnIndex("Telefono"));
        String email = c.getString(c.getColumnIndex("Email"));

        int Id_Categ = c.getInt(c.getColumnIndex("Id_Categoria"));
        String observ = c.getString(c.getColumnIndex("Observaciones"));


        dbConnection.cerrar();

        // Pasamos datos al formulario en modo visualizar
        Intent i = new Intent(MainActivity.this, AltaUsuarios.class);
        i.putExtra("_id", idenviado);
        i.putExtra("Nombre", nombre);
        i.putExtra("Apellidos", apellidos);
        i.putExtra("Direccion", direccion);
        i.putExtra("Telefono", telefono);
        i.putExtra("Email", email);

        i.putExtra("Id_Categoria", Id_Categ);
        i.putExtra("Observaciones", observ);

        i.putExtra(C_MODO, C_EDITAR);
        startActivityForResult(i, C_EDITAR);

    }


    public void borrarTodos() {
		/*
		 * Borramos todos los registros y refrescamos el recyclerView
		 */
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(
                R.string.agenda_eliminar_todos_titulo));
        dialogEliminar.setMessage(getResources().getString(
                R.string.agenda_eliminar_todos_mensaje));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(
                getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int boton) {

                        dbConnection = new SQLControlador(
                                getApplicationContext());
                        try {
                            dbConnection.abrirBaseDeDatos(2);
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }// Escritura. Borrar

                        dbConnection.borrarTodos();

                        Toast.makeText(MainActivity.this,
                                R.string.agenda_eliminar_todos_confirmacion,
                                Toast.LENGTH_SHORT).show();
                        dbConnection.cerrar();
                        consultar();
                    }
                });

        dialogEliminar.setNegativeButton(android.R.string.no, null);

        dialogEliminar.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //SE IMPLEMENTA EL MENÚ BUSCAR. Se añaden a la clase dos interfaces y se implmenta sus métodos más abajo...


        // MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem searchItem = menu.findItem(R.id.buscar);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.buscar_en_searchview));

        //Personalizamos con color y tamaño de letra
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        searchAutoComplete.setTextSize(16);


        //searchView.setSubmitButtonEnabled(true);


        searchView.setOnQueryTextListener(this);


        // LISTENER PARA LA APERTURA Y CIERRE DEL WIDGET
        //MenuItemCompat.setOnActionExpandListener(searchItem, this);
        //FIN IMPLEMENTACION DEL MENU BUSCAR

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptadorBuscador.getFilter().filter(newText);
                return false;
            }
        });*/


        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    //1-Gestionamos los permisos según la versión. A partir de Android M algnos permisos catalogados como peligrosos se gestionan en tiempo de ejecución
    private void permisosPorAplicacion(final int id,int idPermiso) {


        switch (idPermiso) {

            case 1://Acceso a los contactos
            //Permisos para acceder a los Contactos
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //1-La aplicación tiene permisos....

                Intent i = new Intent(this, ImportarContactos.class);
                startActivity(i);

            } else {//No tiene permisos

                //explicarUsoPermiso();
                //solicitarPermiso();

                solicitarPermisoImportContacts();
            }
                break;

            case 2://Permiso para las llamadas
            //Permiso para realizar llamadas
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                //1-La aplicación ya tiene permisos....
                AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

                dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
                dialogEliminar.setTitle(getResources().getString(
                        R.string.agenda_call_titulo));
                dialogEliminar.setMessage(getResources().getString(
                        R.string.agenda_call_mensaje));
                dialogEliminar.setCancelable(false);

                dialogEliminar.setPositiveButton(
                        getResources().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int boton) {

                                try {
                                    call(id);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                dialogEliminar.setNegativeButton(android.R.string.no, null);

                dialogEliminar.show();


            } else {//No tiene permisos

                //explicarUsoPermiso();
                //solicitarPermiso();
                id_Contacto_Llamada=id;
                solicitarPermisoLlamadas();
            }

                break;



            default:
                break;
        }

    }


    private void solicitarPermisoLlamadas() {
        //1-BREVE EXPLICACIÓN DE PARA QUÉ SE SOLICITAN LOS PERMISOS...
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {
            //4-Pequeña explicación de para qué queremos los permisos
            CoordinatorLayout contenedor = (CoordinatorLayout) findViewById(R.id.contenedor);//Para el contexto del snackbar
            Snackbar.make(contenedor, "La Aplicación no tiene permisos para realizar esta acción.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    SOLICITUD_ACCESS_CALL_PHONE);
                        }
                    })
                    .show();
        } else {
            //5-Se muetra cuadro de diálogo predeterminado del sistema para que concedamos o denegemos el permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALL_LOG},
                    SOLICITUD_ACCESS_CALL_PHONE);
        }
    }




    //2-GESTIONAMOS LA CONCESIÓN O NO DE LOS PERMISOS Y LA EXPLICACIÓN PARA QUE TENGAN QUE CONCEDERSE:
    private void solicitarPermisoImportContacts() {
        //1-BREVE EXPLICACIÓN DE PARA QUÉ SE SOLICITAN LOS PERMISOS...
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            //4-Pequeña explicación de para qué queremos los permisos
            CoordinatorLayout contenedor = (CoordinatorLayout) findViewById(R.id.contenedor);//Para el contexto del snackbar
            Snackbar.make(contenedor, "La Aplicación no tiene permisos para realizar esta acción.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    SOLICITUD_ACCESS_READ_CONTACTS);
                        }
                    })
                    .show();
        } else {
            //5-Se muetra cuadro de diálogo predeterminado del sistema para que concedamos o denegemos el permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALL_LOG},
                    SOLICITUD_ACCESS_READ_CONTACTS);
        }
    }

    //3-GESTIONAMOS EL RESULTADO DE LA ELECCIÓN DEL USUARIO EN LA CONCESIÓN DE PERMISOS...

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //Si se preguntara por más permisos el resultado se gestionaría desde aquí.
        if (requestCode == SOLICITUD_ACCESS_READ_CONTACTS) {//6-Se ha concedido los permisos... procedemos a ejecutar el proceso
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent(this, ImportarContactos.class);
                startActivity(i);

            } else {//7-NO se han concedido los permisos. No se puede ejecutar el proceso. Se le informa de ello al usuario.

                /*Snackbar.make(vista, "Sin el permiso, no puedo realizar la" +
                        "acción", Snackbar.LENGTH_SHORT).show();*/
                //1-Seguimos el proceso de ejecucion sin esta accion: Esto lo recomienda Google
                //2-Cancelamos el proceso actual
                //3-Salimos de la aplicacion
                Toast.makeText(this, "No se han concedido los permisos necesarios para poder importar los contactos a la Aplicación.", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == SOLICITUD_ACCESS_CALL_PHONE) {//6-Se ha concedido los permisos... procedemos a ejecutar el proceso
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

                dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
                dialogEliminar.setTitle(getResources().getString(
                        R.string.agenda_call_titulo));
                dialogEliminar.setMessage(getResources().getString(
                        R.string.agenda_call_mensaje));
                dialogEliminar.setCancelable(false);

                dialogEliminar.setPositiveButton(
                        getResources().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int boton) {

                                try {
                                    call(id_Contacto_Llamada);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                            }

                            }
                        });

                dialogEliminar.setNegativeButton(android.R.string.no, null);

                dialogEliminar.show();


            } else {//7-NO se han concedido los permisos. No se puede ejecutar el proceso. Se le informa de ello al usuario.

                /*Snackbar.make(vista, "Sin el permiso, no puedo realizar la" +
                        "acción", Snackbar.LENGTH_SHORT).show();*/
                //1-Seguimos el proceso de ejecucion sin esta accion: Esto lo recomienda Google
                //2-Cancelamos el proceso actual
                //3-Salimos de la aplicacion
                Toast.makeText(this, "No se han concedido los permisos necesarios para poder realizar llamadas desde la Aplicación.", Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_traer_contactos) {
            //MIGRAR DATOS DE LA AGENDA DE ANDROID=========================
            //Intent i = new Intent(this, ImportarContactos.class);
            //startActivity(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permisosPorAplicacion(id,1);
            }else {

                Intent i = new Intent(this, ImportarContactos.class);
                startActivity(i);

            }

            return true;
        }

        if (id == R.id.menu_borrar_todos) {

            borrarTodos();
            return true;
        }

        if (id == R.id.menu_borrar_algunos) {
            //finish();
            Intent intent = new Intent(MainActivity.this, BorrarUsuarios.class);
            intent.putExtra(C_MODO, C_ELIMINAR);
            //startActivity(intent);
            startActivityForResult(intent, C_ELIMINAR);


            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //Evitamos que funcione la tecla del menú que traen por defecto los samsung...
            case KeyEvent.KEYCODE_MENU:
                // Toast.makeText(this, "Menú presionado",
                //       Toast.LENGTH_LONG);
                //toolbar.canShowOverflowMenu();
                //toolbar.setFocusable(true);
                //toolbar.collapseActionView();

                return true;

            /*case KeyEvent.KEYCODE_BACK:
                //int contadorsalida=0;
                //Toast.makeText(this,"Has pulsado tecla atras",Toast.LENGTH_SHORT).show();
                contadorsalida++;
                Toast.makeText(this,"Pulsa otra vez para salir",Toast.LENGTH_SHORT).show();
                if(contadorsalida==2){
                    finish();
                }*/
        }

        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onBackPressed() {
/**
 * Cierra la app cuando se ha pulsado dos veces seguidas en un intervalo inferior a dos segundos.
 */

        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), R.string.agenda_salir, Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
        // super.onBackPressed();
    }

    /**
     * Para el buscador de la Toolbar
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Para el buscador de la Toolbar
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        //Pasamos dos parámetros. Uno para el filtrado y otro para el cambio de color de letra
        adaptadorBuscador.filter = newText;//Color de letra
        adaptadorBuscador.getFilter().filter(newText);//filtrado

        //AdaptadorRecyclerView3.getFilter().filter(newTextt);
        return false;//se cambia a true
    }

    /**
     * Called when a menu item with {@link #SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW}
     * is expanded.
     *
     * @param item Item that was expanded
     * @return true if the item should expand, false if expansion should be suppressed.
     */
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    /**
     * Called when a menu item with {@link #SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW}
     * is collapsed.
     *
     * @param item Item that was collapsed
     * @return true if the item should collapse, false if collapsing should be suppressed.
     */
    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {


//consultar();

        return false;//Se cambia el return a true
    }
}
