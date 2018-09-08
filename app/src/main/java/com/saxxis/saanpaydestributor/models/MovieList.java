package com.saxxis.saanpaydestributor.models;

/**
 * Created by saxxis25 on 8/9/2017.
 */

public class MovieList {

    private String movieTitle;
    private String language;
    private int imageSource;

    public MovieList(String movieTitle,String language,int imageSource){
        this.movieTitle=movieTitle;
        this.language=language;
        this.imageSource=imageSource;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getImageSource() {
        return imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }
}
