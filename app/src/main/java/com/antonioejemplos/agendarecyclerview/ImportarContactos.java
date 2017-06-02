package com.antonioejemplos.agendarecyclerview;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import Beans.Contactos;
import controlador.SQLControlador;

public class ImportarContactos extends AppCompatActivity {

    private SQLControlador Connection;
    private ArrayList<String> nombres;
    private ArrayList<String> telefonos;
    private ArrayList<String> emails;
    private ArrayList<String> direcciones;

    private Cursor pCur;//Cursor para los teléfonos
    private Cursor emailCur;//Cursor para los emails
    private Cursor cur;//Cursor para los nombres
    private Cursor addrCur;//Cursor para la dirección


    private ArrayList<Contactos> ArrayListcontactos;

    //private SQLiteDatabase db;

    //Para la barra de progreso
    //private static int progreso;
    private ProgressBar barraProgreso;
    //private TextView txt;
    //private int estatusProgreso = 0;
    private Handler manejador = new Handler();//Manejador del hilo

    //private static final int IMPORTADO = 6;

    private TextView txtconfirm;


    //Añadimos la toolbar
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importar_contactos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//Añadimos la toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //La acitivity debe extender de AppCompatActivity para poder hacer el seteo a ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TypedValue typedValueColorPrimaryDark = new TypedValue();
        ImportarContactos.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValueColorPrimaryDark, true);
        final int colorPrimaryDark = typedValueColorPrimaryDark.data;
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(colorPrimaryDark);
        }


        //progreso = 0;
        barraProgreso = (ProgressBar) findViewById(R.id.barradeprogreso);
        /*txt=(TextView) findViewById(R.id.textView1);
        txt.setVisibility(View.INVISIBLE);*/
        txtconfirm=(TextView)findViewById(R.id.txtconfirm);

        txtconfirm.setVisibility(View.INVISIBLE);

        //barraProgreso.setVisibility(View.INVISIBLE);

        //Diálogo de confirmación....


        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(
                R.string.title_activity_importar_contactos));
        dialogEliminar.setMessage(getResources().getString(
                R.string.aceptar_import));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(
                getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int boton) {
                        barraProgreso.setVisibility(View.VISIBLE);
                        // ---Hacer alg�n trabajo en el hilo de fondo---
                        new Thread(new Runnable() {
                            public void run() {

                                // ---hacer alg�n trabajo aqu�---
                                importar();
                                // ---ocultar la barra de progreso---
                                manejador.post(new Runnable() {
                                    public void run() {
                                        // ---0 - VISIBLE; 4 - INVISIBLE; 8 - GONE---
                                        //barraProgreso.setVisibility(8);
                                        barraProgreso.setVisibility(View.INVISIBLE);
                                        //txt.setVisibility(View.VISIBLE);
                                        txtconfirm.setVisibility(View.VISIBLE);

                                    }
                                });
                            }


                        }).start();


                    }//Fin onclick del alert

                });//Fin setPositiveButton


        dialogEliminar.setNegativeButton(android.R.string.no,


                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int boton) {
                        barraProgreso.setVisibility(View.INVISIBLE);

                        System.exit(0);//Cerramos la activity actual
                        //Intent intent=new Intent(ImportarContactos.this,ActivityLista.class);
                        //startActivity(intent);

                    }
                });




        dialogEliminar.show();


    }//Fin Oncreate


    private void importar(){
		/*
		* Trae todos los contacto de la agenda de Android y los inserta en la BB.DD. de la app.
		* Les asigna categoría 6(sin zona).
		*
		*
		*
		* */


        ContentResolver cr = getContentResolver();
        cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,
                ContactsContract.Contacts.IN_VISIBLE_GROUP, null,
                ContactsContract.Contacts.DISPLAY_NAME);

        nombres=new ArrayList<String>();
        telefonos=new ArrayList<String>();
        emails=new ArrayList<String>();

        direcciones=new ArrayList<String>();

        ArrayListcontactos = new ArrayList<Contactos>();
        Contactos contactos;

        //Se puede eliminar...
        ContentValues valores = new ContentValues();

        String phone="";
        String apellidos="";
        String direccion="";
        String email="";

        int categoria=5;
        //public static final int IMPORTADO = 6;
        //int categoria=IMPORTADO;

        String idgrupo="";
        String observaciones="";

        int importado=1;
        int sincronizado=0;


        //cur;//Cursor para los nombres
        //pCur;//Cursor para los teléfonos
        //emailCur;//Cursor para los emails




        if (cur.getCount() > 0) {


            while (cur.moveToNext()) {

//	            	contactos=new Contactos(cur.getInt(0),cur.getString(1),apellidos,direccion,pCur.getString(0),emailCur.getString(0),categoria,observaciones);
//	            	ArrayListcontactos.add(contactos);

                //registros=new ArrayList<String>();
                int telefono=0;

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                long id_convert= Long.parseLong(id);//Para el arrylist de la tabla contactos... es un long

                //int contactPhoneType = cur .getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                nombres.add(name);

                //valores.put("Nombre", name);



                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //System.out.println("name : " + name + ", ID : " + id);


                    pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);


                    while (pCur.moveToNext()) {


                        phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        // System.out.println("phone" + phone);

                        int contactPhoneType = pCur .getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                        int contactTieneEmail =pCur .getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));


                        if(telefono==0 ){//Para recoger solo el primer tel�fono en caso de qu tenga varios...
                            telefonos.add(phone);
                            telefono++;
                        }
//	                          switch (contactPhoneType) {
//		 		                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//		 		                    //holder = holder + ", Home";
//		 		                	telefonos.add(phone);
//		 		                    break;
//		 		                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//		 		                    //holder = holder + ", Work";
//		 		                	telefonos.add(phone);
//		 		                    break;
//		 		                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//		 		                    //holder = holder + ", Mobile";
//		 		                	//phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//		 		                	telefonos.add(phone);
//		 		                	 //valores.put("Telefono", phone);
//
//		 		                	//Arraycontactos.add(object)
//		 		                    break;
//		 		                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
//		 		                    //holder = holder + ", Other";
//		 		                	telefonos.add(phone);
//		 		                    break;
//		 		                }



                    }
                    pCur.close();


                    // get email and type

                    emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    email="Email no disponible";

                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//	                        String emailType = emailCur.getString(
//	                                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        int emailType = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

//				            switch (emailType) {
//				            case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
//				                //holder = holder + ", Home";
//				            	//email= cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
//				            	registros.add(email);
//				                break;
//				            case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
//				                //holder = holder + ", Work";
//				                break;
//				            case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
//				                //holder = holder + ", Mobile";
//				                break;
//				            case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
//				                //holder = holder + ", Other";
//				                break;
//
//				            }

                        emails.add(email);
                        //System.out.println("Email " + email + " Email Type : " + emailType);


                    }//Fin while (emailCur.moveToNext())

                    emailCur.close();


    //DIRECCIÓN==============================================================================

	                    String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

	                    String[] addrWhereParams = new String[]{id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

	                    addrCur = cr.query(ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null);
                    direccion="";
	                    while(addrCur.moveToNext()) {
	                        String poBox = addrCur.getString(
	                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
	                        String street = addrCur.getString(
	                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
	                        String city = addrCur.getString(
	                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
	                        String state = addrCur.getString(
	                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
	                        String postalCode = addrCur.getString(
	                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
	                        String country = addrCur.getString(
	                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
	                        String type = addrCur.getString(
	                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

	                        //Recojemos calle y ciudad:
                            direccion=street+ " "+ city;
                            direcciones.add(direccion);

	                    }
	                    addrCur.close();


   //FIN DIRECCIÓN==============================================================================



                    // Get Grupo........OBTENEMOS TODOS LOS TIPOS DE GRUPO QUE EXISTEN EN LA AGENDA======================

//	                    final String[] GROUP_PROJECTION = new String[] {ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
//	                    Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null,  null, ContactsContract.Groups.TITLE);
//	                          while (cursor.moveToNext()) {
//
//	                               idgrupo = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
//	                              String gTitle = (cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE)));
//
//	                              if (gTitle.contains("Group:")) {
//	                                gTitle = gTitle.substring(gTitle.indexOf("Group:") + 6).trim();
//	                              }
//	                              if (gTitle.contains("Favorite_")) {
//	                                   gTitle = "Favorites";
//	                              }
//	                              if (gTitle.contains("Starred in Android") || gTitle.contains("My Contacts")) {
//	                                  continue;
//	                              }
////	                              arr_groups.add(gTitle);
////	                              arr_groupswithid.add(id + "." + gTitle);
//	                             // System.out.println(idgrupo + "  "+gTitle);
//	                              /*
//	                               * 1-Family
//	                               * 2-Friends
//	                               * 3-Compa�eros de trabajo
//	                               * 4-Emergencias
//	                               * 5-
//	                               * 6-
//	                               * 7-Amigos
//	                               * 8-Family
//	                               * */
//
//	                              if (idgrupo.equals("1") || idgrupo.equals("8")   ){//Family
//
//	                            	  categoria=1;
//
//	                              }
//	                              else if(idgrupo.equals("3") || (idgrupo.equals("9") ) )//Compa�eros
//	                              {
//
//	                            	  categoria=3;
//
//	                              }
//	                              else if(idgrupo.equals("2") || (idgrupo.equals("7") ) )//Amigos
//	                              {
//
//	                            	  categoria=2;
//
//	                              }
//
//	                              else if(idgrupo.equals("4") )//Emergencia
//	                              {
//
//	                            	  categoria=Integer.parseInt(idgrupo);
//
//	                              }
//
//
//	                              else
//	                              {
//
//	                            	  categoria=4;
//
//	                              }
//	                    }
//
//	                   cursor.close();


//	                    final String[] GROUP_PROJECTION = new String[] {ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
//	                    Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null,  null, null);
//
//	                     Cursor grupo=getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + " = ?",
//	                    		             											new String[]{id}, null);
//	                    if(grupo.moveToNext()){
//	                     idgrupo = grupo.getString(grupo.getColumnIndex(ContactsContract.Groups._ID));
////                       while (cursor.moveToNext()) {
//
//	 							while (cursor.moveToNext()) {
//
//
//
//
//	                              //String gTitle = (cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE)));
//	                               //idgrupo=grupo.getString(grupo.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
//
//
//	                               if (idgrupo.equals("1") || idgrupo.equals("8")   ){//Family
//
//		                            	  categoria=1;
//
//		                              }
//		                              else if(idgrupo.equals("3") || (idgrupo.equals("9") ) )//Compa�eros
//		                              {
//
//		                            	  categoria=3;
//
//		                              }
//		                              else if(idgrupo.equals("2") || (idgrupo.equals("7") ) )//Amigos
//		                              {
//
//		                            	  categoria=2;
//
//		                              }
//
//		                              else if(idgrupo.equals("4") )//Emergencia
//		                              {
//
//		                            	  categoria=Integer.parseInt(idgrupo);
//
//		                              }
//
//
////		                              else
////		                              {
////		                            	categoria=4;
////
////		                              }
//
//	                          }
//
//
//	                    }//Fin movetofirst







//???????????	           //CATEGORÍA SE INFORMA CON VALOR 5. lA VARIABLE ESTÁ DEFINIDA ARRIBA...
                    contactos=new Contactos(id_convert,name,apellidos,direccion, phone,email,categoria,observaciones,importado,sincronizado);
                    ArrayListcontactos.add(contactos);



                    // Get note.......==========================
//	                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//	                    String[] noteWhereParams = new String[]{id,
//	                    ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//	                            Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
//	                    if (noteCur.moveToFirst()) {
//	                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
//	                      System.out.println("Note " + note);
//	                    }
//	                    noteCur.close();





                    // Get Instant Messenger.........=============
//	                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//	                    String[] imWhereParams = new String[]{id,
//	                        ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
//	                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
//	                            null, imWhere, imWhereParams, null);
//	                    if (imCur.moveToFirst()) {
//	                        String imName = imCur.getString(
//	                                 imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
//	                        String imType;
//	                        imType = imCur.getString(
//	                                 imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
//	                    }
//	                    imCur.close();
//
                    // Get Organizations........======================

//	                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//	                    String[] orgWhereParams = new String[]{id,
//	                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
//	                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
//	                                null, orgWhere, orgWhereParams, null);
//	                    if (orgCur.moveToFirst()) {
//	                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
//	                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
//	                    }
//	                    orgCur.close();




                }//Fin Teléfonos

            }//Fin while (cur.moveToNext())

        }//Fin cur.getCount




////==============================================================================================================
        //Construimos la fecha de la operación de importación de la agenda
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = new GregorianCalendar();

        String dia = Integer.toString(c2.get(Calendar.DATE));
        String mes = Integer.toString(c2.get(Calendar.MONTH) + 1);
        String annio = Integer.toString(c2.get(Calendar.YEAR));


        String fecha=dia+"/"+ mes+"/"+annio;


        //Vamos a SQLControlador para comparar lo que hemos traido de Android con lo que había en SQLite
        Connection = new SQLControlador(getApplicationContext());//Objeto SQLControlador
        try {
            Connection.abrirBaseDeDatos(2);

            //Connection.ImportaColeccionContent(nombres, telefonos, emails);//Enviando tres colecciones. Problemas al recuperar los valores de email
            //Connection.ImportaCursorContent(cur, pCur, emailCur);//Enviando los tres cursores del Contentprovider..Problemas recibiendo..

            //Connection.ImportCollectionContactsContent(ArrayListcontactos);

            Connection.ImportCollectionContactsContent(ArrayListcontactos,fecha);




        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //===============================

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//Evitamos que funcione la tecla del menú que traen por defecto los samsung...
        switch(keyCode) {
            case KeyEvent.KEYCODE_MENU:
                // Toast.makeText(this, "Menú presionado",
                //       Toast.LENGTH_LONG);
                //toolbar.canShowOverflowMenu();
                //toolbar.setFocusable(true);
                //toolbar.collapseActionView();



                return true;
        }

        return super.onKeyUp(keyCode, event);
    }
}
