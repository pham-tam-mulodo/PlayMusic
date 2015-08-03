package vn.com.mulodo.popup;

/**
 * Created by tampham on 28/07/2015.
 */
public class ListMusic {
    private String _id;
    private String _title;
    private String _artist;
    private String _avatar;
    private String _url;
    private String _hostName;
    private String _urlLyrics;

    /**
     *
     * @param title String
     * @param artist String
     * @param url String
     * @param avatar String
     */
    public ListMusic(String title, String artist, String url, String avatar, String hostName) {
        _title = title;
        _artist = artist;
        _url = url;
        _avatar = avatar;
        _hostName = hostName;
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

    public String getUrl() {
        return _url;
    }

    public void setUrl() {
        this._url = _url;
    }

    public String getAvatar() {
        return _avatar;
    }

    public void setAvatar() {
        this._avatar = _avatar;
    }

    public String getHostName() {
        return _hostName;
    }

    public void setHostName() {
        this._hostName = _hostName;
    }

    public String getUrlLyrics() {
        return _urlLyrics;
    }

    public void setUrlLyrics() {
        this._urlLyrics = _urlLyrics;
    }
}
