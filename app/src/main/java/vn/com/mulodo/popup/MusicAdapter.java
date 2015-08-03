package vn.com.mulodo.popup;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tampham on 28/07/2015.
 */
public class MusicAdapter extends ArrayAdapter<ListMusic> {

    private List<ListMusic> mDataList;
    private Context mContext;
    private int mLayoutResourceId;

    public MusicAdapter(Context context, int resource, List<ListMusic> data) {
        super(context, resource, data);
        mDataList = data;
        mContext = context;
        mLayoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        MusicViewHolder holder = null;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            rowView = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new MusicViewHolder(rowView);

            //remember the view
            rowView.setTag(holder);
        } else {
            holder = (MusicViewHolder) rowView.getTag();
        }

        ListMusic data = mDataList.get(position);
        if (data != null) {
            holder.title.setText(data.getTitle());
            holder.artist.setText(data.getArtist());
            holder.hostName.setText(data.getHostName());
            if (null != data.getAvatar() && !data.getAvatar().isEmpty()) {
                Picasso.with(mContext)
                        .load(data.getAvatar())
                        .placeholder(R.drawable.final1)
                        .error(R.drawable.error)
                        .into(holder.avatar);
            }

        }

        return rowView;
    }

    static class MusicViewHolder {
        public TextView title;
        public TextView artist;
        public TextView hostName;
        public ImageView avatar;

        public MusicViewHolder(View v) {
            title = (TextView) v.findViewById(R.id.textSongName);
            artist = (TextView) v.findViewById(R.id.textSingerName);
            avatar = (ImageView) v.findViewById(R.id.imageAvatar);
            hostName = (TextView) v.findViewById(R.id.hostName);
        }
    }
}
