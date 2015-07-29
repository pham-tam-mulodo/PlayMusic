package vn.com.mulodo.popup;

/**
 * Created by tampham on 28/07/2015.
 */
public class ListMusic {
    private String _title;
    private String _artist;

    public ListMusic(String title, String artist) {
        _title = title;
        _artist = artist;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle() {
        this._title = _title;
    }

    public String getArtist() {
        return _artist;
    }

    public void setArtist() {
        this._artist = _artist;
    }
}
