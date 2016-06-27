package com.antonioejemplos.agendarecyclerview;

import android.widget.Filter;

import java.util.ArrayList;

import Beans.Contactos;

/*Clase que gestiona el filtrado*/



public class CustomFilter extends Filter{

    AdaptadorRecyclerViewSearch adapter;
    ArrayList<Contactos> filterList;


    public CustomFilter(ArrayList<Contactos> filterList,AdaptadorRecyclerViewSearch adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Contactos> filteredPlayers=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getNombre().toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                    adapter.filter=constraint.toString();//Cambia el color de los caracteres
                }
            }

            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.items= (ArrayList<Contactos>) results.values;

        //REFRESH
        adapter.notifyDataSetChanged();
    }
}

