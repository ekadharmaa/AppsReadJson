package com.example.appsreadjson;

import android.os.Parcel;
import android.os.Parcelable;

public class Ormawa implements Parcelable {
    private String Id;
    private String Nama;
    private String myFoto;

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    private String Path;

    private int JumlahAnggota;

    public Ormawa(String id, String nama, String MyFoto, int Jml_Anggota) {
        Id = id;
        Nama = nama;
        this.myFoto = MyFoto;
        JumlahAnggota = Jml_Anggota;
    }

    public Ormawa(String id, String nama, int jmlAnggota, String MyFoto) {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getMyFoto() {
        return myFoto;
    }

    public void setMyFoto(String myFoto) {
        this.myFoto = myFoto;
    }

    public int getJumlahAnggota() {
        return JumlahAnggota;
    }

    public void setJumlahAnggota(int jumlahAnggota) {
        JumlahAnggota = jumlahAnggota;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.Nama);
        dest.writeString(this.myFoto);
        dest.writeInt(this.JumlahAnggota);
    }

    public void readFromParcel(Parcel source) {
        this.Id = source.readString();
        this.Nama = source.readString();
        this.myFoto = source.readString();
        this.JumlahAnggota = source.readInt();
    }

    protected Ormawa(Parcel in) {
        this.Id = in.readString();
        this.Nama = in.readString();
        this.myFoto = in.readString();
        this.JumlahAnggota = in.readInt();
    }

    public static final Creator<Ormawa> CREATOR = new Creator<Ormawa>() {
        @Override
        public Ormawa createFromParcel(Parcel source) {
            return new Ormawa(source);
        }

        @Override
        public Ormawa[] newArray(int size) {
            return new Ormawa[size];
        }
    };

}
