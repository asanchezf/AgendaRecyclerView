package com.antonioejemplos.agendarecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import Beans.Contactos;

/**
 * Created by Susana on 04/02/2016.
 */
public class AdaptadorRecyclerView3 extends RecyclerView.Adapter<AdaptadorRecyclerView3.ContactosViewHolder> {

    private ArrayList<Contactos> items;//ArrayList de contactos
    private OnItemClickListener escucha;
    private final Context contexto;

    Contactos contactos;

    interface OnItemClickListener {
        public void onClick(RecyclerView.ViewHolder holder, int idPromocion, View v);
    }

    //CLASE INTERNA CON VIEWHOLDER. CONTIENE EL MANEJADOR DE EVENTOS
    public class ContactosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //Campos a mostrar en la celda
        TextView titulo;
        TextView subtitulo;
        TextView descripcion;
        TextView telefono;
        ImageView categoria;
        TextView txtObservaciones;
        Button contactar;

        ImageView ubicacion;
        TextView txtUbicacion;
        //Button ruta;
        TextView txtRuta;
        ImageView ruta;

        public ContactosViewHolder(View v) {
            super(v);

            titulo = (TextView) v.findViewById(R.id.text1);
            subtitulo = (TextView) v.findViewById(R.id.text2);
            descripcion = (TextView) v.findViewById(R.id.text3);
            categoria = (ImageView) v.findViewById(R.id.category);
            telefono = (TextView) v.findViewById(R.id.text4);
            txtObservaciones = (TextView) v.findViewById(R.id.txtobservaciones);
            contactar=(Button)v.findViewById(R.id.btncontactar);

            ubicacion= (ImageView) v.findViewById(R.id.posicionamiento);
            txtUbicacion= (TextView) v.findViewById(R.id.txtubicacion);
            ruta= (ImageView) v.findViewById(R.id.imgruta);
            txtRuta= (TextView) v.findViewById(R.id.txtruta);
            v.setOnClickListener(this);
            categoria.setOnClickListener(this);
            contactar.setOnClickListener(this);

            txtUbicacion.setOnClickListener(this);
            txtRuta.setOnClickListener(this);
            //ruta.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {

            escucha.onClick(this, obtenerIdContacto(getAdapterPosition()),v);



        }

        private int obtenerIdContacto(int posicion) {

            return (int)items.get(posicion).get_id();

           //return (int)contactos.get_id();
            //return items.getInt(contactos.get_id());

        /*    if (items != null) {



                if (items.moveToPosition(posicion)) {
                    return items.getInt(ConsultaAlquileres.ID_ALQUILER);
                } else {
                    return -1;
                }
            } else {
                return -1;
            }*/

        }
    }

    //CONSTRUCTOR DEL ADAPTADOR
    public AdaptadorRecyclerView3(ArrayList<Contactos> datos, OnItemClickListener escucha, Context contexto) {

        this.items = datos;
        this.escucha = escucha;
        this.contexto = contexto;



    }



    @Override
    public AdaptadorRecyclerView3.ContactosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila_recyclerview2, parent,false);


        return new ContactosViewHolder(v);
    }


    @Override
    public void onBindViewHolder(AdaptadorRecyclerView3.ContactosViewHolder holder, int position) {

          contactos=items.get(position);


        holder.titulo.setText(items.get(position).getNombre());
        holder.subtitulo.setText(items.get(position).getEmail());
        holder.telefono.setText(items.get(position).getTelefono());
        holder.txtObservaciones.setText(items.get(position).getObservaciones());

        //Evento scroll en una textView
       // holder.txtObservaciones.setMovementMethod(new ScrollingMovementMethod());

        holder.categoria.setImageResource(R.drawable.imgcontacto3);
        holder.ubicacion.setImageResource(R.drawable.icono_ubicacion);

        //holder.descripcion.setText("CATEGORIA");

        if (contactos.getId_Categoria() == 1) {
            holder.descripcion.setText("Familia");
            //holder.categoria.setImageResource(R.drawable.furgopeque);
        }else if(contactos.getId_Categoria() == 2){
            holder.descripcion.setText("Amigos");
        }else if(contactos.getId_Categoria() == 3){
            holder.descripcion.setText("Compañeros");
        }else if(contactos.getId_Categoria() == 4){
            holder.descripcion.setText("Otros");
        }else if(contactos.getId_Categoria() == 5){
            holder.descripcion.setText("Importado");
        }






        //holder.categoria.setOnClickListener(holder);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }


}
