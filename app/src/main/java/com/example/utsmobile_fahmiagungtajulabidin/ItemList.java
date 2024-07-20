package com.example.utsmobile_fahmiagungtajulabidin;

public class ItemList {

    private String id;
    private String judul;
    private String subJudul;
    private String imageUrl;

    public ItemList(String judul, String subJudul, String imageUrl) {
        this.judul = judul;
        this.subJudul = subJudul;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubJudul() {
        return subJudul;
    }

    public void setSubJudul(String subJudul) {
        this.subJudul = subJudul;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }
}
