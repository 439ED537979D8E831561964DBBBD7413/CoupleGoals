package couplegoals.com.couplegoals.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import couplegoals.com.couplegoals.R;
import couplegoals.com.couplegoals.database.DatabaseValues;
import couplegoals.com.couplegoals.model.Post;

/**
 * Created by Brijesh on 8/16/2017.
 */

public class PostListAdapter extends ArrayAdapter<Post> {
    private Activity context;
    private List<Post> postDetailsList;

    public PostListAdapter(Activity context,List<Post> postDetailsList){
        super(context, R.layout.card_layout_view_daily_post,postDetailsList);
        this.context = context;
        this.postDetailsList = postDetailsList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        final View listViewItems = layoutInflater.inflate(R.layout.card_layout_view_daily_post,null,true);

        TextView textViewTodaysMessage = (TextView) listViewItems.findViewById(R.id.cardMessageToday);
        CardView cardViewPostListDetails = (CardView) listViewItems.findViewById(R.id.cardViewPostListDetails);

        //ImageView cardImageTodayPost = (ImageView) listViewItems.findViewById(R.id.cardImageTodayPost);

        final Post postDetails = postDetailsList.get(position);

        textViewTodaysMessage.setText(Html.fromHtml(postDetails.getsTodayPostMessage()+"<b> posted by </b>" + postDetails.getsPostedBy()));
        if (DatabaseValues.getUserDisplayName().equalsIgnoreCase(postDetails.getsPostedBy())){
            cardViewPostListDetails.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_shadow_start_color));
        }
        //Picasso.with(context).load(postDetails.getsTodayPostImagePath()).into(cardImageTodayPost);
        return listViewItems;
    }
}
