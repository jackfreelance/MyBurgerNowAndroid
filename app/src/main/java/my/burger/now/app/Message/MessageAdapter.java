package my.burger.now.app.Message;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

import my.burger.now.app.R;

/**
 * Created by 8029 on 01/03/2016.
 */
public class MessageAdapter  extends BaseAdapter {
    Context messageContext;
    ArrayList<Message> messageList;


    public MessageAdapter(Context context, ArrayList<Message> messages) {
        messageList = messages;
        messageContext = context;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    private static class MessageViewHolder {
        public ImageView thumbnailImageView;
        public TextView senderView;
        public TextView bodyView;
        public TextView date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageViewHolder holder;

        // if there is not already a view created for an item in the Message list.

        if (convertView == null){
            LayoutInflater messageInflater = (LayoutInflater) messageContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            // create a view out of our `message.xml` file
            convertView = messageInflater.inflate(R.layout.message, null);

            // create a MessageViewHolder
            holder = new MessageViewHolder();

            // set the holder's properties to elements in `message.xml`
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.senderView = (TextView) convertView.findViewById(R.id.message_sender);
            holder.bodyView = (TextView) convertView.findViewById(R.id.message_body);
            holder.date = (TextView)convertView.findViewById(R.id.time);

            // assign the holder to the view we will return
            convertView.setTag(holder);
        } else {

            // otherwise fetch an already-created view holder
            holder = (MessageViewHolder) convertView.getTag();
        }

        // get the message from its position in the ArrayList
        Message message = (Message) getItem(position);


        // set the elements' contents
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            convertView.setBackground(messageContext.getResources().getDrawable(R.drawable.bg_bubble_white));
        }
        holder.bodyView.setText(message.text);
        holder.senderView.setText(message.name);
        holder.date.setText(DateUtils.getRelativeDateTimeString(messageContext,
                message.time, DateUtils.SECOND_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS, 0));

        // fetch the user's Twitter avatar from their username
        // and place it into the thumbnailImageView.
        /*Picasso.with(messageContext).
                load("https://twitter.com/" + message.name + "/profile_image?size=original").
                placeholder(R.mipmap.ic_launcher).
                into(holder.thumbnailImageView);*/

        return convertView;

    }

    public void add(Message message){
        messageList.add(message);
        notifyDataSetChanged();
    }


}
