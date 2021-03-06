package com.yucatancorp.petagram.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yucatancorp.petagram.pojo.Mascota;

import java.util.ArrayList;

/**
 * Created by marcos on 29/03/2017.
 */

public class BaseDatos extends SQLiteOpenHelper {

    private Context context;

    public BaseDatos(Context context) {
        super(context, ConstantesBaseDatos.DATABASE_NAME, null, ConstantesBaseDatos.DATEBASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCrearTablaMascota = "CREATE TABLE " + ConstantesBaseDatos.TABLE_MASCOTAS + "(" +
                                        ConstantesBaseDatos.TABLE_MASCOTAS_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        ConstantesBaseDatos.TABLE_MASCOTAS_NOMBRE   + " TEXT, " +
                                        ConstantesBaseDatos.TABLE_MASCOTAS_FOTO     + " INTEGER, " +
                                        ConstantesBaseDatos.TABLE_MASCOTAS_HUESO   + " INTEGER" +
                                        ")";

        String queryCrearTablaLikesMascota = "CREATE TABLE " + ConstantesBaseDatos.TABLE_LIKES_MASCOTA + "(" +
                                            ConstantesBaseDatos.TABLE_LIKES_MASCOTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                            ConstantesBaseDatos.TABLE_LIKES_MASCOTA_ID_MASCOTA + " INTEGER, " +
                                            ConstantesBaseDatos.TABLE_LIKES_MASCOTA_NUMERO_LIKES + " INTEGER, " +
                                            "FOREIGN KEY (" + ConstantesBaseDatos.TABLE_LIKES_MASCOTA_ID_MASCOTA + ") " +
                                            "REFERENCES " + ConstantesBaseDatos.TABLE_MASCOTAS + "("+ConstantesBaseDatos.TABLE_MASCOTAS_ID +")"+
                                            ")";

        db.execSQL(queryCrearTablaMascota);
        db.execSQL(queryCrearTablaLikesMascota);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + ConstantesBaseDatos.TABLE_MASCOTAS);
        db.execSQL("DROP TABLE IF EXIST " + ConstantesBaseDatos.TABLE_LIKES_MASCOTA);
        onCreate(db);
    }

    public ArrayList<Mascota> obtenerTodasLasMascotas()
    {
        ArrayList<Mascota> mascotas = new ArrayList<>();

        String query = "SELECT * FROM " + ConstantesBaseDatos.TABLE_MASCOTAS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor registros = db.rawQuery(query, null);

        while(registros.moveToNext())
        {
            Mascota mascotaActual = new Mascota();
            mascotaActual.setId(registros.getInt(0));
            mascotaActual.setNombre(registros.getString(1));
            mascotaActual.setFoto(registros.getInt(2));
            mascotaActual.setHueso(registros.getInt(3));

            String queryLikes = "SELECT COUNT("+ConstantesBaseDatos.TABLE_LIKES_MASCOTA_NUMERO_LIKES+") as likes " +
                                " FROM " +ConstantesBaseDatos.TABLE_LIKES_MASCOTA +
                                " WHERE " + ConstantesBaseDatos.TABLE_LIKES_MASCOTA_ID_MASCOTA + "=" + mascotaActual.getId();

            Cursor registrosLikes = db.rawQuery(queryLikes, null);
            if(registrosLikes.moveToNext())
            {
                mascotaActual.setRanking(registrosLikes.getInt(0));
            }else{
                mascotaActual.setRanking(0);
            }

            mascotas.add(mascotaActual);
        }

        db.close();

        return mascotas;
    }

    public void insertarContacto(ContentValues contentValues){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(ConstantesBaseDatos.TABLE_MASCOTAS,null, contentValues);
        db.close();
    }

    public void insertarLikeMascota(ContentValues contentValues)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(ConstantesBaseDatos.TABLE_LIKES_MASCOTA, null, contentValues);
        db.close();
    }

    public int obtenerLikesMascotas(Mascota mascota)
    {
        int likes = 0;

        String query = "SELECT COUNT("+ConstantesBaseDatos.TABLE_LIKES_MASCOTA_NUMERO_LIKES+")"+
                        " FROM " + ConstantesBaseDatos.TABLE_LIKES_MASCOTA +
                        " WHERE " + ConstantesBaseDatos.TABLE_LIKES_MASCOTA_ID_MASCOTA + "=" + mascota.getId();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor registros = db.rawQuery(query, null);

        if (registros.moveToNext()){
            likes = registros.getInt(0);
        }

        db.close();
        return likes;
    }
}
